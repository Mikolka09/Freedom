package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AlertController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AlertService alertService;


    @Autowired
    private UserService userService;


    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        List<Alert> alerts = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .collect(Collectors.toList());
        List<Message> messages = messageService.findAllMessagesUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .collect(Collectors.toList());
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                    role = r.getName();
            }
        }
        model.addAttribute("messages", messages);
        model.addAttribute("alerts", alerts);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
        model.addAttribute("user", user);
    }

    @GetMapping("/user/alerts")
    public String index(Model model) {
        CreateModelUser(model);
        return "alert/index";
    }

}
