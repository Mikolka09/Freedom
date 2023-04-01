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
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


@RestController
public class ApiEmailController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiEmailController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/email/new-password/{email}")
    public String sendSimpleEmail(@PathVariable(value = "email") String email) {
        User user = userService.findUserByEmail(email);
        String subject = "NEW PASSWORD";
        String newPassword = "Freedom_new23";
        String path = "/pages/fragments/email/new-password.html";
        HashMap<String, Object> base = new HashMap<>();
        base.put("password", newPassword);
        if (user != null) {
            if (user.isEmailConfirmed()) {
                try {
                    user.setPassword(newPassword);
                    user.setPasswordConfirm(newPassword);
                    if (userService.saveNewPassword(user)) {
                        emailService.sendSimpleEmail(createEmailSend(email, subject, base, path));
                    }
                } catch (MailException | MessagingException mailException) {
                    LOG.error("Error while sending out email..{}", (Object) mailException.getStackTrace());
                    return "NOT";
                }
                return "OK";
            } else
                return "NOT";
        } else
            return "NOT";
    }

    @GetMapping(value = "/email/confirm/{id}")
    public String sendConfirmEmail(@PathVariable(value = "id") Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userService.saveEdit(user);
            String email = user.getEmail();
            String subject = "CONFIRM EMAIL ADDRESS";
            String url = "http://freedom.eu-central-1.elasticbeanstalk.com/email/confirm-message/" + id + "/" + token;
            String path = "/pages/fragments/email/confirm.html";
            HashMap<String, Object> base = new HashMap<>();
            base.put("url", url);
            base.put("fullName", user.getFullName());
            base.put("email", user.getEmail());
            try {
                emailService.sendSimpleEmail(createEmailSend(email, subject, base, path));
            } catch (MailException | MessagingException mailException) {
                LOG.error("Error while sending out email..{}", (Object) mailException.getStackTrace());
                return "NOT";
            }
            return "OK";
        } else
            return "NOT";
    }

    @GetMapping("/contact-us/message")
    public String sendContactMessage(@RequestParam(value = "name") String name,
                                     @RequestParam(value = "email") String email,
                                     @RequestParam(value = "subject") String subject,
                                     @RequestParam(value = "message") String message) throws MessagingException {
        if (!Objects.equals(name, "") || !Objects.equals(email, "") ||
                !Objects.equals(subject, "") || !Objects.equals(message, "")) {
            String header = "NEW MESSAGE FROM CONTACT PAGE";
            String emailTo = "info.freedom2023@gmail.com";
            HashMap<String, Object> base = new HashMap<>();
            base.put("fullName", name);
            base.put("email", email);
            base.put("subject", subject);
            base.put("message", message);
            String path = "/pages/fragments/email/contact-message.html";
            emailService.sendSimpleEmail(createEmailSend(emailTo, header, base, path));
            return "OK";
        } else
            return "NOT";
    }

    public static EmailContext createEmailSend(String email, String subject, HashMap<String, Object> base, String path) {
        String fromAddress = "info.freedom2023@gmail.com";
        EmailContext context = new EmailContext();
        context.setTo(email);
        context.setFrom(fromAddress);
        context.setSubject(subject);
        context.setContext(base);
        context.setTemplateLocation(path);
        return context;
    }
}
