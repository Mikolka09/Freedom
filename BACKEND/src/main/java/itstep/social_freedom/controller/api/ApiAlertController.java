package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.FriendService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ApiAlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private FriendService friendService;

    @GetMapping("/user/alerts/follow/{id}")
    public String sendFollow(@PathVariable(name = "id") Long id,
                             @RequestParam(value = "userId") Long userId) {
        User userFrom = userService.findUserById(userId);
        User userTo = userService.findUserById(id);
        String text = userFrom.getFullName() + " sent you a friend request!";
        Alert alert = new Alert();
        Invite invite = new Invite();
        alert.setText(text);
        invite.setUserFrom(userFrom);
        invite.setUserTo(userTo);
        invite.setStatus(Status.REQUEST);
        alert.setInvite(invite);
        if (inviteService.saveInvite(invite)) {
            alert.setStatus(Status.ACTIVE);
            if (alertService.saveAlert(alert))
                return "OK";
            else
                return "NOT";
        } else
            return "NOT";
    }

    @GetMapping("/user/alerts/change/{id}")
    public Alert[] changeStatus(@PathVariable(name = "id") Long id) {
        Alert alert = alertService.findAlertById(id);
        Invite invite = alert.getInvite();
        if (invite.getStatus() != Status.VIEWED) {
            invite.setStatus(Status.VIEWED);
            alert.setInvite(invite);
            inviteService.saveInvite(invite);
            alertService.saveAlert(alert);
        }
        return alertService.findAllAlertsUserById(alert.getInvite().getUserTo().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .toArray(Alert[]::new);
    }

    @GetMapping("/user/alerts/accepted/{id}")
    public String acceptedAlert(@PathVariable(name = "id") Long id) {
        Alert alert = alertService.findAlertById(id);
        if (alert != null) {
            Invite invite = alert.getInvite();
            invite.setStatus(Status.ACCEPTED);
            if (inviteService.saveInvite(invite)) {
                alert.setInvite(invite);
                alertService.saveAlert(alert);
                return "OK";
            }
        }
        return "NOT";
    }

    @GetMapping("/user/alerts/deny/{id}")
    public String deniedFriend(@PathVariable(name = "id") Long id) {
        Alert alert = alertService.findAlertById(id);
        if (alert != null) {
            Invite invite = alert.getInvite();
            invite.setStatus(Status.DENIED);
            if (inviteService.saveInvite(invite)) {
                alert.setInvite(invite);
                alertService.saveAlert(alert);
                return "OK";
            }
        }
        return "NOT";
    }

    @GetMapping("/user/alerts/confirm/{id}")
    public String confirmFriend(@PathVariable(name = "id") Long id) {
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
                return "OK";
            }
        }
        return "NOT";
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
