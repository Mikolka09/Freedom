package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.FriendService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        List<Alert> alerts = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .collect(Collectors.toList());
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                    role = r.getName();
            }
        }
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

    @GetMapping("/user/alerts/confirm/{id}")
    public String confirmFriend(@PathVariable(name = "id") Long id, Model model) {
        Alert alert = alertService.findAlertById(id);
        if (alert != null) {
            Invite invite = alert.getInvite();
            invite.setStatus(Status.ACCEPTED);
            if (inviteService.saveInvite(invite)) {
                User userFrom = userService.findUserById(invite.getUserFrom().getId());
                User userTo = userService.findUserById(invite.getUserTo().getId());
                Friend friendFrom = new Friend();
                friendFrom.setFriendRequester(userFrom);
                friendFrom.setFriendReceiver(userTo);
                friendService.saveFriend(friendFrom);
                Friend friendTo = new Friend();
                friendTo.setFriendRequester(userTo);
                friendTo.setFriendReceiver(userFrom);
                friendService.saveFriend(friendTo);
                alert.setInvite(invite);
                alertService.saveAlert(alert);
                sendResponse(userFrom, userTo);
                CreateModelUser(model);
                return "alert/index";
            }
        }
        CreateModelUser(model);
        return "alert/index";
    }

    @GetMapping("/user/alerts/accepted/{id}")
    public String acceptedAlert(@PathVariable(name = "id") Long id, Model model){
        Alert alert = alertService.findAlertById(id);
        if (alert != null){
            Invite invite = alert.getInvite();
            invite.setStatus(Status.ACCEPTED);
            if(inviteService.saveInvite(invite)){
                alert.setInvite(invite);
                alertService.saveAlert(alert);
                CreateModelUser(model);
                return "alert/index";
            }
        }
        CreateModelUser(model);
        return "alert/index";
    }

    public void sendResponse(User userFrom, User userTo) {
        Alert alert = new Alert();
        Invite invite = new Invite();
        String text = userTo.getFullName() + " accepted your friend request!";
        alert.setText(text);
        invite.setUserFrom(userTo);
        invite.setUserTo(userFrom);
        invite.setStatus(Status.REQUEST);
        alert.setInvite(invite);
        if (inviteService.saveInvite(invite)) {
            alert.setStatus(Status.ACTIVE);
            alertService.saveAlert(alert);
        }
    }

}
