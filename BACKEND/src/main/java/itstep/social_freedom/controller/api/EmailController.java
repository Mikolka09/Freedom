package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.EmailContext;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.EmailService;
import itstep.social_freedom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;


@RestController
public class EmailController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/email/simple-email/{email}")
    public String sendSimpleEmail(@PathVariable(value = "email") String email) {
        User user = userService.findUserByEmail(email);
        String subject = "NEW PASSWORD";
        String fromAddress = "admin@freedom.com";
        String newPassword = "Freedom_new23";
        String path = "/pages/fragments/email/new-password.html";
        if (user != null) {
            try {
                user.setPassword(newPassword);
                user.setPasswordConfirm(newPassword);
                if (userService.saveNewPassword(user)) {
                    EmailContext context = new EmailContext();
                    context.setTo(email);
                    context.setFrom(fromAddress);
                    context.setSubject(subject);
                    context.setContext(newPassword);
                    context.setTemplateLocation(path);
                    emailService.sendSimpleEmail(context);
                }
            } catch (MailException | MessagingException mailException) {
                LOG.error("Error while sending out email..{}", (Object) mailException.getStackTrace());
                return "NOT";
            }
            return "OK";
        } else
            return "NOT";
    }
}
