package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Message;
import itstep.social_freedom.entity.Role;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                    role = r.getName();
            }
        }
        model.addAttribute("role", role);
        model.addAttribute("user", user);
    }

    @GetMapping("/user/messages")
    public String index(Model model) {
        List<Message> messages = messageService.findAllMessagesUserById(userService.getCurrentUsername().getId());
        CreateModelUser(model);
        model.addAttribute("messages", messages);
        return "mail/index";
    }
}
