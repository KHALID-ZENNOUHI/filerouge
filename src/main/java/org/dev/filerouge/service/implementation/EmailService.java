package org.dev.filerouge.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    /**
     * Send a simple text email
     *
     * @param to recipient email
     * @param subject email subject
     * @param content email content
     */
    @Async
    public void sendEmail(String to, String subject, String content) {
        log.debug("Sending email to {} with subject: {}", to, subject);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        emailSender.send(message);
    }

    /**
     * Send an HTML email using a Thymeleaf template
     *
     * @param to recipient email
     * @param subject email subject
     * @param templateName Thymeleaf template name
     * @param context Thymeleaf context with variables
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            log.debug("Sending HTML email to {} with subject: {}", to, subject);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email", e);
        }
    }

    /**
     * Send an email with attachment
     *
     * @param to recipient email
     * @param subject email subject
     * @param content email content
     * @param attachmentPath path to attachment
     * @param attachmentName attachment name
     */
    @Async
    public void sendEmailWithAttachment(String to, String subject, String content,
                                        String attachmentPath, String attachmentName) {
        try {
            log.debug("Sending email with attachment to {} with subject: {}", to, subject);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);

            helper.addAttachment(attachmentName, new java.io.File(attachmentPath));

            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment", e);
        }
    }
}
