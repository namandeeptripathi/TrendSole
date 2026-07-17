package com.trendsole.service;

import com.trendsole.dto.InspectionUpdateDTO;
import com.trendsole.dto.RefundUpdateDTO;
import com.trendsole.dto.ReturnRequestCreateDTO;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.*;
import com.trendsole.repository.OrderItemRepository;
import com.trendsole.repository.OrderRepository;
import com.trendsole.repository.ReturnRequestRepository;
import com.trendsole.repository.UserRepository;
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
public class ReturnRequestService {

    private static final String UPLOAD_DIR = "uploads/returns/";
    private static final int MAX_IMAGES = 5;

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusHistoryService statusHistoryService;

    @Autowired
    private EmailNotificationService emailService;

    @Transactional
    public ReturnRequest createReturnRequest(ReturnRequestCreateDTO dto, MultipartFile[] files, String authenticatedEmail) {
        if (dto == null || dto.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required for return request.");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        // Validate Ownership
        User customer = userRepository.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        boolean isOwner = false;
        if (customer != null && order.getUser() != null && order.getUser().getId().equals(customer.getId())) {
            isOwner = true;
        } else if (order.getEmail() != null && order.getEmail().equalsIgnoreCase(authenticatedEmail)) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new AccessDeniedException("You are not authorized to request a return for this order.");
        }

        // Rule: Only DELIVERED orders can be returned
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Return requests are allowed only for DELIVERED orders.");
        }

        // Rule: Prevent duplicate return requests
        OrderItem orderItem = null;
        if (dto.getOrderItemId() != null) {
            orderItem = orderItemRepository.findById(dto.getOrderItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + dto.getOrderItemId()));
            if (!orderItem.getOrder().getId().equals(order.getId())) {
                throw new IllegalArgumentException("Order item does not belong to the specified order.");
            }
            if (returnRequestRepository.existsByOrderIdAndOrderItemId(order.getId(), dto.getOrderItemId())) {
                throw new IllegalStateException("A return request already exists for this order item.");
            }
        } else {
            if (returnRequestRepository.existsByOrderIdAndOrderItemIdIsNull(order.getId())) {
                throw new IllegalStateException("A return request already exists for this order.");
            }
        }

        String returnNumber = generateReturnNumber();

        // Process Image Uploads (Max 5 files, 5MB limit, valid MIME type & extension)
        List<String> imageUrls = new ArrayList<>();
        if (files != null && files.length > 0) {
            if (files.length > MAX_IMAGES) {
                throw new IllegalArgumentException("Maximum of " + MAX_IMAGES + " images can be uploaded per return request.");
            }
            imageUrls = saveUploadedImages(files, returnNumber);
        }

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setReturnNumber(returnNumber);
        returnRequest.setOrder(order);
        returnRequest.setOrderItem(orderItem);
        returnRequest.setCustomer(customer != null ? customer : order.getUser());
        returnRequest.setReason(dto.getReason() != null ? dto.getReason() : "Return Requested");
        returnRequest.setDescription(dto.getDescription());
        returnRequest.setImageUrls(imageUrls);
        returnRequest.setReturnStatus(ReturnStatus.PENDING);
        returnRequest.setInspectionStatus(InspectionStatus.PENDING);
        returnRequest.setRefundStatus(RefundStatus.PENDING);
        returnRequest.setRequestedAt(LocalDateTime.now());
        returnRequest.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(returnRequest);

        // Record in Order Timeline
        String remarks = "Return requested (#" + saved.getReturnNumber() + "): " + saved.getReason();
        statusHistoryService.recordStatusChange(order, OrderStatus.DELIVERED, "CUSTOMER", remarks);

        // Email Notification
        emailService.sendReturnRequestedEmail(saved);

        return saved;
    }

    public List<ReturnRequest> getMyReturnRequests(String authenticatedEmail) {
        User customer = userRepository.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        if (customer != null) {
            List<ReturnRequest> list = returnRequestRepository.findByCustomerIdOrderByRequestedAtDesc(customer.getId());
            if (!list.isEmpty()) return list;
        }
        return returnRequestRepository.findByOrderEmailOrderByRequestedAtDesc(authenticatedEmail);
    }

    public List<ReturnRequest> getAllReturnRequests() {
        return returnRequestRepository.findAll();
    }

    public ReturnRequest getReturnRequestById(Long id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with id: " + id));
    }

    public ReturnRequest getReturnByIdForCustomer(Long id, String authenticatedEmail) {
        ReturnRequest req = getReturnRequestById(id);
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
            throw new AccessDeniedException("You are not authorized to view this return request.");
        }
        return req;
    }

    @Transactional
    public ReturnRequest approveReturn(Long id, String adminRemarks) {
        ReturnRequest req = getReturnRequestById(id);
        req.setReturnStatus(ReturnStatus.APPROVED);
        req.setInspectionStatus(InspectionStatus.PENDING);
        req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(req);

        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Return Approved (#" + req.getReturnNumber() + ")");
        emailService.sendReturnApprovedEmail(saved);

        return saved;
    }

    @Transactional
    public ReturnRequest rejectReturn(Long id, String adminRemarks) {
        ReturnRequest req = getReturnRequestById(id);
        req.setReturnStatus(ReturnStatus.REJECTED);
        req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(req);

        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Return Rejected (#" + req.getReturnNumber() + ")");
        emailService.sendReturnRejectedEmail(saved);

        return saved;
    }

    @Transactional
    public ReturnRequest updateInspectionStatus(Long id, InspectionUpdateDTO dto) {
        ReturnRequest req = getReturnRequestById(id);
        if (dto != null && dto.getInspectionStatus() != null) {
            req.setInspectionStatus(dto.getInspectionStatus());
            if (dto.getInspectionStatus() == InspectionStatus.PASSED) {
                req.setReturnStatus(ReturnStatus.INSPECTION_PENDING);
                req.setRefundStatus(RefundStatus.PENDING);
            } else if (dto.getInspectionStatus() == InspectionStatus.FAILED) {
                req.setReturnStatus(ReturnStatus.REJECTED);
                emailService.sendReturnRejectedEmail(req);
            }
        }
        if (dto != null && dto.getAdminRemarks() != null) {
            req.setAdminRemarks(dto.getAdminRemarks());
        }
        req.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Inspection Update (#" + req.getReturnNumber() + "): " + req.getInspectionStatus());

        return saved;
    }

    @Transactional
    public ReturnRequest updateRefundStatus(Long id, RefundUpdateDTO dto) {
        ReturnRequest req = getReturnRequestById(id);
        if (dto != null && dto.getRefundStatus() != null) {
            req.setRefundStatus(dto.getRefundStatus());
            if (dto.getRefundStatus() == RefundStatus.COMPLETED) {
                emailService.sendRefundCompletedEmail(req);
            }
        }
        if (dto != null && dto.getAdminRemarks() != null) {
            req.setAdminRemarks(dto.getAdminRemarks());
        }
        req.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(req);
        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Refund Update (#" + req.getReturnNumber() + "): " + req.getRefundStatus());

        return saved;
    }

    @Transactional
    public ReturnRequest completeReturn(Long id, String adminRemarks) {
        ReturnRequest req = getReturnRequestById(id);
        req.setReturnStatus(ReturnStatus.COMPLETED);
        req.setRefundStatus(RefundStatus.COMPLETED);
        req.setAdminRemarks(adminRemarks);
        req.setUpdatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(req);

        statusHistoryService.recordStatusChange(req.getOrder(), OrderStatus.DELIVERED, "ADMIN", "Return Completed (#" + req.getReturnNumber() + ")");
        emailService.sendReturnCompletedEmail(saved);

        return saved;
    }

    private List<String> saveUploadedImages(MultipartFile[] files, String returnNumber) {
        List<String> imageUrls = new ArrayList<>();
        String folderPath = UPLOAD_DIR + returnNumber + "/";
        File uploadDir = new File(folderPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        long maxSizeBytes = 5 * 1024 * 1024; // 5 MB

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            if (file.getSize() > maxSizeBytes) {
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

            String filename = "img_" + UUID.randomUUID().toString().substring(0, 8) + ext;
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

    private String generateReturnNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomHex = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RTN-" + dateStr + "-" + randomHex;
    }
}
