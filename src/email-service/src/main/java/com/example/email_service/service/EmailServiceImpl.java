package com.example.email_service.service.impl;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import com.example.email_service.exception.EmailServiceException;
import com.example.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Primary
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendOrderConfirmation(SendOrderConfirmationRequest request) {
        try {
            // Prepare email message
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set email properties
            helper.setFrom(senderEmail);
            helper.setTo(request.getEmail());
            helper.setSubject("Your Order Confirmation");
            
            // Prepare template context
            Context context = new Context();
            context.setVariable("order", request.getOrder());
            
            // Process template to get HTML content
            String htmlContent = templateEngine.process("confirmation", context);
            helper.setText(htmlContent, true);
            
            // Send email
            emailSender.send(message);
            
            logger.info("Order confirmation email sent successfully to {}", request.getEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send order confirmation email to {}: {}", request.getEmail(), e.getMessage());
            throw new EmailServiceException("Failed to send order confirmation email", e);
        }
    }
}