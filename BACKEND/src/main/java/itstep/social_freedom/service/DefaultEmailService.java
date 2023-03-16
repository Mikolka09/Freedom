package itstep.social_freedom.service;

import itstep.social_freedom.entity.EmailContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendSimpleEmail(EmailContext email)
            throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariable("password", email.getContext());
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);
        mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setText(emailContent, true);
        emailSender.send(message);
    }
}
