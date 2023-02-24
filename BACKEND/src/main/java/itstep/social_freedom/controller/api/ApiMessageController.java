package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiMessageController {


    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/user/messages/send/{id}")
    public String sendMessage(@PathVariable(name = "id") Long id,
                              @RequestParam(value = "userId") Long userId,
                              @RequestParam(value = "text") String text) {
        User userFrom = userService.findUserById(userId);
        User userTo = userService.findUserById(id);
        Message message = new Message();
        Invite invite = new Invite();
        message.setMessage(text);
        invite.setUserFrom(userFrom);
        invite.setUserTo(userTo);
        invite.setStatus(Status.REQUEST);
        message.setInvite(invite);
        if (inviteService.saveInvite(invite)) {
            message.setStatus(Status.ACTIVE);
            if (messageService.saveMessage(message))
                return "OK";
            else
                return "NOT";
        } else
            return "NOT";
    }

    @GetMapping("/user/messages/change/{id}")
    public Message[] changeStatus(@PathVariable(name = "id") Long id) {
        Message message = messageService.findMessageById(id);
        Invite invite = message.getInvite();
        if (invite.getStatus() != Status.VIEWED) {
            invite.setStatus(Status.VIEWED);
            message.setInvite(invite);
            if (inviteService.saveInvite(invite))
                messageService.saveMessage(message);
        }
        return messageService.findAllMessagesUserById(message.getInvite().getUserTo().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .toArray(Message[]::new);
    }
}
