package com.trendsole.service;

import com.trendsole.dto.ExchangeAdminActionDTO;
import com.trendsole.dto.ExchangeRequestCreateDTO;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.*;
import com.trendsole.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ExchangeRequestService {

    private static final String UPLOAD_DIR = "uploads/exchanges/";
    private static final int MAX_IMAGES = 5;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusHistoryService statusHistoryService;

    @Autowired
    private EmailNotificationService emailService;

    @Transactional
    public ExchangeRequest createExchangeRequest(ExchangeRequestCreateDTO dto, MultipartFile[] files, String authenticatedEmail) {
        if (dto == null || dto.getOrderId() == null || dto.getOrderItemId() == null) {
            throw new IllegalArgumentException("Order ID and Order Item ID are required for exchange request.");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + dto.getOrderItemId()));

        if (!orderItem.getOrder().getId().equals(order.getId())) {
            throw new IllegalArgumentException("Order item does not belong to the specified order.");
        }

        // Validate Ownership
        User customer = userRepository.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        boolean isOwner = false;
        if (customer != null && order.getUser() != null && order.getUser().getId().equals(customer.getId())) {
            isOwner = true;
        } else if (order.getEmail() != null && order.getEmail().equalsIgnoreCase(authenticatedEmail)) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new AccessDeniedException("You are not authorized to request an exchange for this order.");
        }

        // Rule: Only DELIVERED orders are eligible
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Exchange requests are allowed only for DELIVERED orders.");
        }

        // Rule: One exchange request per order item
        if (exchangeRequestRepository.existsByOrderIdAndOrderItemId(order.getId(), orderItem.getId())) {
            throw new IllegalStateException("An exchange request already exists for this order item.");
        }

        String exchangeNumber = generateExchangeNumber();

        // Upload images
        List<String> imageUrls = new ArrayList<>();
        if (files != null && files.length > 0) {
            if (files.length > MAX_IMAGES) {
                throw new IllegalArgumentException("Maximum of " + MAX_IMAGES + " images can be uploaded per exchange request.");
            }
            imageUrls = saveUploadedImages(files, exchangeNumber);
        }

        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setExchangeNumber(exchangeNumber);
        exchangeRequest.setOrder(order);
        exchangeRequest.setOrderItem(orderItem);
        exchangeRequest.setCustomer(customer != null ? customer : order.getUser());
        exchangeRequest.setExchangeReason(dto.getExchangeReason() != null ? dto.getExchangeReason() : "Exchange Requested");
        exchangeRequest.setDescription(dto.getDescription());
        exchangeRequest.setRequestedSize(dto.getRequestedSize());
        exchangeRequest.setRequestedColor(dto.getRequestedColor());
        exchangeRequest.setImageUrls(imageUrls);
        exchangeRequest.setExchangeStatus(ExchangeStatus.PENDING);
        exchangeRequest.setRequestedAt(LocalDateTime.now());
        exchangeRequest.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);

        // Record in Order Timeline
        String remarks = "Exchange requested (#" + saved.getExchangeNumber() + "): " + saved.getExchangeReason();
        statusHistoryService.recordStatusChange(order, OrderStatus.DELIVERED, "CUSTOMER", remarks);

        // Send Email
        emailService.sendExchangeRequestedEmail(saved);

        return saved;
    }

    public List<ExchangeRequest> getMyExchangeRequests(String authenticatedEmail) {
        User customer = userRepository.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        if (customer != null) {
            List<ExchangeRequest> list = exchangeRequestRepository.findByCustomerIdOrderByRequestedAtDesc(customer.getId());
            if (!list.isEmpty()) return list;
        }
        return exchangeRequestRepository.findByOrderEmailOrderByRequestedAtDesc(authenticatedEmail);
    }

    public List<ExchangeRequest> getAllExchangeRequests() {
        return exchangeRequestRepository.findAll();
    }

    public ExchangeRequest getExchangeRequestById(Long id) {
        return exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found with id: " + id));
    }

    public ExchangeRequest getExchangeByIdForCustomer(Long id, String authenticatedEmail) {
        ExchangeRequest req = getExchangeRequestById(id);
        User customer = userRepository.findByEmailIgnoreCase(authenticatedEmail).orElse(null);

        boolean isOwner = false;
        if (customer != null && req.getCustomer() != null && req.getCustomer().getId().equals(customer.getId())) {
            isOwner = true;
        } else if (req.getOrder() != null && req.getOrder().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            isOwner = true;
        } else if (customer != null && "ADMIN".equalsIgnoreCase(String.valueOf(customer.getRole()))) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new AccessDeniedException("You are not authorized to view this exchange request.");
        }
        return req;
    }

    @Transactional
    public ExchangeRequest approveExchange(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);

        Product product = req.getOrderItem().getProduct();
        if (product == null || product.getStock() == null || product.getStock() <= 0) {
            req.setExchangeStatus(ExchangeStatus.REJECTED);
            req.setAdminRemarks(adminRemarks != null ? adminRemarks : "Requested product variant is out of stock.");
            req.setUpdatedAt(LocalDateTime.now());
            ExchangeRequest saved = exchangeRequestRepository.save(req);
            emailService.sendExchangeRejectedEmail(saved);
            statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Exchange Rejected (Out of Stock): #" + req.getExchangeNumber());
            return saved;
        }

        // Reserve 1 unit
        product.setStock(product.getStock() - 1);
        productRepository.save(product);

        req.setExchangeStatus(ExchangeStatus.PRODUCT_RESERVED);
        req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);

        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Exchange Approved & Product Reserved (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeApprovedEmail(saved);
        emailService.sendExchangeReservedEmail(saved);

        return saved;
    }

    @Transactional
    public ExchangeRequest rejectExchange(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);
        req.setExchangeStatus(ExchangeStatus.REJECTED);
        req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);

        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Exchange Rejected (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeRejectedEmail(saved);

        return saved;
    }

    @Transactional
    public ExchangeRequest reserveProduct(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);
        if (req.getExchangeStatus() == ExchangeStatus.PRODUCT_RESERVED) {
            return req;
        }

        Product product = req.getOrderItem().getProduct();
        if (product == null || product.getStock() == null || product.getStock() <= 0) {
            throw new IllegalStateException("Product is out of stock for exchange reservation.");
        }

        product.setStock(product.getStock() - 1);
        productRepository.save(product);

        req.setExchangeStatus(ExchangeStatus.PRODUCT_RESERVED);
        if (adminRemarks != null) req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Product Reserved for Exchange (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeReservedEmail(saved);

        return saved;
    }

    @Transactional
    public ExchangeRequest shipExchange(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);
        req.setExchangeStatus(ExchangeStatus.SHIPPED);
        if (adminRemarks != null) req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Replacement Product Shipped (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeShippedEmail(saved);

        return saved;
    }

    @Transactional
    public ExchangeRequest deliverExchange(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);
        req.setExchangeStatus(ExchangeStatus.DELIVERED);
        if (adminRemarks != null) req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Replacement Product Delivered (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeDeliveredEmail(saved);

        return saved;
    }

    @Transactional
    public ExchangeRequest completeExchange(Long id, String adminRemarks) {
        ExchangeRequest req = getExchangeRequestById(id);
        req.setExchangeStatus(ExchangeStatus.COMPLETED);
        if (adminRemarks != null) req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ExchangeRequest saved = exchangeRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Exchange Completed (#" + req.getExchangeNumber() + ")");
        emailService.sendExchangeCompletedEmail(saved);

        return saved;
    }

    private List<String> saveUploadedImages(MultipartFile[] files, String exchangeNumber) {
        List<String> imageUrls = new ArrayList<>();
        String folderPath = UPLOAD_DIR + exchangeNumber + "/";
        File uploadDir = new File(folderPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File '" + file.getOriginalFilename() + "' exceeds maximum allowed size of 5 MB.");
            }

            String contentType = file.getContentType();
            if (contentType == null) {
                throw new IllegalArgumentException("File '" + file.getOriginalFilename() + "' has invalid content type.");
            }

            String mime = contentType.toLowerCase().trim();
            if (!mime.equals("image/jpeg") && !mime.equals("image/jpg") && !mime.equals("image/png")) {
                throw new IllegalArgumentException("File '" + file.getOriginalFilename() + "' has invalid MIME type '" + contentType + "'. Only image/jpeg, image/jpg, and image/png are allowed.");
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }

            if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".png".equals(ext)) {
                throw new IllegalArgumentException("File '" + file.getOriginalFilename() + "' has invalid extension. Only .jpg, .jpeg, and .png are allowed.");
            }

            String filename = "exc_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = Paths.get(folderPath + filename);
            try {
                Files.write(filePath, file.getBytes());
                imageUrls.add("/" + folderPath + filename);
            } catch (IOException e) {
                throw new RuntimeException("Could not save uploaded file: " + originalName, e);
            }
        }
        return imageUrls;
    }

    private String generateExchangeNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomHex = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "EXC-" + dateStr + "-" + randomHex;
    }
}
