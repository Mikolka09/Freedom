package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;

@Controller
public class AlertController {

    @Autowired
    private AlertService alertService;

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

    @GetMapping("/user/alerts")
    public String index(Model model) {
        List<Alert> alerts = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId());
        CreateModelUser(model);
        model.addAttribute("alerts", alerts);
        return "alert/index";
    }

}
