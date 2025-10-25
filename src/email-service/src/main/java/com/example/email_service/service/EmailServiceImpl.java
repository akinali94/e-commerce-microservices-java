package com.example.email_service.service;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

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

    @Async
    @Override
    @Retryable(
        value = {MessagingException.class}, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<Boolean> sendOrderConfirmation(@Valid SendOrderConfirmationRequest request) {
        try {
            logger.info("Starting to send order confirmation email to {}", request.getEmail());

            // Validate request
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                logger.error("Email address is null or empty");
                throw new EmailServiceException("Email address cannot be null or empty");
            }
            
            if (request.getOrder() == null) {
                logger.error("Order details are null");
                throw new EmailServiceException("Order details cannot be null");
            }

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
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            logger.error("Failed to send order confirmation email to {}: {}", request.getEmail(), e.getMessage());
            throw new EmailServiceException("Failed to send order confirmation email", e);
        } catch (Exception e) {
            logger.error("Unexpected error when sending email to {}: {}", request.getEmail(), e.getMessage());
            throw new EmailServiceException("Unexpected error when sending email", e);
        }
    }

    @Recover
    public CompletableFuture<Boolean> recoverSendOrderConfirmation(EmailServiceException e, SendOrderConfirmationRequest request) {
        logger.error("All attempts to send email to {} failed. Last error: {}", request.getEmail(), e.getMessage());
        // Burada başarısız e-posta gönderimlerini bir veritabanına kaydedebilir veya
        // alternatif bir bildirim mekanizması kullanabilirsiniz.
        return CompletableFuture.completedFuture(false);
    }
}