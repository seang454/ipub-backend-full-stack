package com.istad.docuhub.feature.sendMail;


import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.sendMail.dto.SendMailRequest;
import com.istad.docuhub.feature.sendMail.dto.SendMailResponse;
import com.istad.docuhub.feature.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailServiceImpl implements SendMailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final PaperRepository paperRepository;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public SendMailResponse sendMailReject(SendMailRequest sendMailRequest) {
        try {
            // Fetch paper details using paperUuid
            Paper paper = paperRepository.findByUuid(sendMailRequest.paperUuid())
                    .orElseThrow(() -> new RuntimeException("Paper not found with UUID: " + sendMailRequest.paperUuid()));

            // Get author information
            String authorName = paper.getAuthor().getFullName();
            String authorEmail = userService.getSingleUser(paper.getAuthor().getUuid()).email();

            // Create Thymeleaf context with template variables
            Context context = new Context();
            context.setVariable("authorName", authorName);
            context.setVariable("paperTitle", paper.getTitle());
            context.setVariable("submittedDate", paper.getSubmittedAt().toString());
            context.setVariable("rejectionReason", sendMailRequest.body());

            // Process the rejection email template
            String htmlContent = templateEngine.process("email/paper-rejection", context);

            // Create and configure the email message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(authorEmail);
            helper.setSubject("Paper Rejection - " + paper.getTitle());
            helper.setText(htmlContent, true); // true indicates HTML content

            // Send the email
            javaMailSender.send(mimeMessage);

            log.info("Paper rejection email sent successfully to: {} for paper: {}", authorEmail, paper.getTitle());

            return new SendMailResponse(
                    authorEmail,
                    "Paper Rejection - " + paper.getTitle(),
                    "Rejection email sent successfully to " + authorName
            );

        } catch (MessagingException e) {
            log.error("Failed to send rejection email for paper UUID: {}", sendMailRequest.paperUuid(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing paper rejection email: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing email: " + e.getMessage(), e);
        }

    }
}
