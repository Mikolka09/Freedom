package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.Alert;
import itstep.social_freedom.entity.Invite;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiAlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @GetMapping("/user/alerts/follow/{id}")
    public int sendFollow(@PathVariable(name = "id") Long id,
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
        if(inviteService.saveInvite(invite)) {
            alert.setStatus(Status.ACTIVE);
            if (alertService.saveAlert(alert))
                return 200;
            else
                return 400;
        }else
            return 400;
    }
}
