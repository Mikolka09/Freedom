package itstep.social_freedom.controller.api;

import itstep.social_freedom.controller.UserController;
import itstep.social_freedom.entity.EmailContext;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.EmailService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.MessageService;
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
public class EmailController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private MessageService messageService;

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
    }

    @GetMapping(value = "/email/confirm/{id}")
    public String sendConfirmEmail(@PathVariable(value = "id") Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userService.saveEdit(user);
            String email = "mikolka09@gmail.com"; //user.getEmail();
            String subject = "CONFIRM EMAIL ADDRESS";
            String url = "http://localhost:8080/email/confirm-message/" + id + "/" + token;
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

    @GetMapping(value = "/email/confirm-message/{id}/{token}")
    public void confirmEmail(@PathVariable(value = "id") Long id, @PathVariable(value = "token") String token) {
        User user = userService.findUserById(id);
        if(user!=null){
            String tokenUser = user.getToken();
            if(Objects.equals(tokenUser, token)){
                user.setEmailConfirmed(true);
                User userFrom = userService.findUserByEmail("admin@gmail.com");
                String text = "Your email has been verified. Thank you very much!";
                UserController.createSendMessage(userFrom, user, text, userService, inviteService, messageService);
            }
        }
    }

    public static EmailContext createEmailSend(String email, String subject, HashMap<String, Object> base, String path) {
        String fromAddress = "admin@freedom.com";
        EmailContext context = new EmailContext();
        context.setTo(email);
        context.setFrom(fromAddress);
        context.setSubject(subject);
        context.setContext(base);
        context.setTemplateLocation(path);
        return context;
    }
}
