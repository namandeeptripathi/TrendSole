package com.trendsole.service;

import com.trendsole.config.CompanyProperties;
import com.trendsole.model.ReturnRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private CompanyProperties companyProperties;

    public void sendReturnRequestedEmail(ReturnRequest request) {
        String subject = "TrendSole Return Request Received - #" + request.getReturnNumber();
        String body = "Dear Customer,\n\n" +
                "Your return request for Order #" + request.getOrder().getOrderNumber() +
                " (Return #" + request.getReturnNumber() + ") has been successfully received.\n\n" +
                "Reason: " + request.getReason() + "\n" +
                "Status: PENDING\n\n" +
                "We will review your request shortly.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendReturnApprovedEmail(ReturnRequest request) {
        String subject = "TrendSole Return Request Approved - #" + request.getReturnNumber();
        String body = "Dear Customer,\n\n" +
                "Your return request #" + request.getReturnNumber() +
                " for Order #" + request.getOrder().getOrderNumber() + " has been APPROVED.\n\n" +
                "Please follow the return instructions. Item inspection will be conducted upon arrival.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendReturnRejectedEmail(ReturnRequest request) {
        String subject = "TrendSole Return Request Update - #" + request.getReturnNumber();
        String body = "Dear Customer,\n\n" +
                "Your return request #" + request.getReturnNumber() +
                " for Order #" + request.getOrder().getOrderNumber() + " has been REJECTED.\n\n" +
                "Remarks: " + (request.getAdminRemarks() != null ? request.getAdminRemarks() : "Does not meet return policy criteria.") + "\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendRefundCompletedEmail(ReturnRequest request) {
        String subject = "TrendSole Refund Completed - #" + request.getReturnNumber();
        String body = "Dear Customer,\n\n" +
                "The refund for your return request #" + request.getReturnNumber() +
                " (Order #" + request.getOrder().getOrderNumber() + ") has been COMPLETED.\n\n" +
                "The amount has been processed to your original payment method.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendReturnCompletedEmail(ReturnRequest request) {
        String subject = "TrendSole Return Completed - #" + request.getReturnNumber();
        String body = "Dear Customer,\n\n" +
                "Your return process #" + request.getReturnNumber() +
                " for Order #" + request.getOrder().getOrderNumber() + " is now COMPLETED.\n\n" +
                "Thank you for shopping with " + companyProperties.getName() + ".\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeRequestedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Exchange Request Received - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your exchange request for Order #" + request.getOrder().getOrderNumber() +
                " (Exchange #" + request.getExchangeNumber() + ") has been received.\n\n" +
                "Reason: " + request.getExchangeReason() + "\n" +
                "Status: PENDING\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeApprovedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Exchange Request Approved - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your exchange request #" + request.getExchangeNumber() +
                " for Order #" + request.getOrder().getOrderNumber() + " has been APPROVED.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeRejectedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Exchange Request Update - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your exchange request #" + request.getExchangeNumber() +
                " for Order #" + request.getOrder().getOrderNumber() + " has been REJECTED.\n\n" +
                "Remarks: " + (request.getAdminRemarks() != null ? request.getAdminRemarks() : "Out of stock or policy criteria not met.") + "\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeReservedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Replacement Product Reserved - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "The replacement product for Exchange #" + request.getExchangeNumber() + " has been RESERVED in inventory.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeShippedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Replacement Product Shipped - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your replacement product for Exchange #" + request.getExchangeNumber() + " has been SHIPPED.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeDeliveredEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Replacement Product Delivered - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your replacement product for Exchange #" + request.getExchangeNumber() + " has been DELIVERED.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    public void sendExchangeCompletedEmail(com.trendsole.model.ExchangeRequest request) {
        String subject = "TrendSole Exchange Completed - #" + request.getExchangeNumber();
        String body = "Dear Customer,\n\n" +
                "Your exchange process #" + request.getExchangeNumber() + " is now COMPLETED.\n\n" +
                "Best regards,\n" + companyProperties.getName();

        sendEmail(request.getOrder().getEmail(), subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(companyProperties.getEmail());
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                logger.info("Real email sent to {} for subject: {}", toEmail, subject);
            } else {
                logger.info("JavaMailSender bean not configured. Email logged: To={}, Subject={}", toEmail, subject);
            }
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
