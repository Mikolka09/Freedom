package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.entity.dto.MessageDto;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.InviteService;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ApiMessageController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/user/messages/send/{id}")
    public String sendMessage(@PathVariable(name = "id") Long id,
                              @RequestParam(value = "userId") Long userId,
                              @RequestParam(value = "text") String text,
                              @RequestParam(value = "answer") String answer) {
        User userFrom = userService.findUserById(userId);
        User userTo = userService.findUserById(id);
        Message message = new Message();
        Invite invite = new Invite();
        if (Objects.equals(answer, "false")) {
            message.setMessage(text);
        } else {
            String outText = answer + "\n" + text;
            message.setMessage(outText);
        }
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
        if (invite.getStatus() != Status.NOT_VIEWED) {
            invite.setStatus(Status.NOT_VIEWED);
            message.setInvite(invite);
            if (inviteService.saveInvite(invite))
                messageService.saveMessage(message);
        }
        return messageService.findAllMessagesUserById(message.getInvite().getUserTo().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .toArray(Message[]::new);
    }

    @GetMapping("/user/messages/mails/{id}")
    public Message[] printChangeMails(@PathVariable(name = "id") Long id) {
        Message message = messageService.findMessageById(id);
        return messageService.findAllMessagesUserById(message.getInvite().getUserTo().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .toArray(Message[]::new);
    }

    @GetMapping("/user/messages/accepted/{id}")
    public String acceptedMessage(@PathVariable(name = "id") Long id) {
        Message message = messageService.findMessageById(id);
        SimpleDateFormat simpleDateFormatOut =
                new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        if (message != null) {
            Invite invite = message.getInvite();
            if(invite.getStatus()!=Status.VIEWED) {
                invite.setStatus(Status.VIEWED);
                if (inviteService.saveInvite(invite)) {
                    User userFrom = userService.findUserById(invite.getUserFrom().getId());
                    User userTo = userService.findUserById(invite.getUserTo().getId());
                    message.setInvite(invite);
                    messageService.saveMessage(message);
                    String text = " read your message from " + simpleDateFormatOut.format(message.getCreatedAt());
                    sendResponse(userFrom, userTo, text);
                    return "OK";
                }
            }else
                return "OK";
        }
        return "NOT";
    }

    public void sendResponse(User userFrom, User userTo, String answer) {
        Alert alert = new Alert();
        Invite invite = new Invite();
        String text = userTo.getFullName() + answer;
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

    @GetMapping("/user/messages/all-messages/{id}")
    public Message[] allMessagesUser(@PathVariable(name = "id") Long id,
                                     @RequestParam(name="idTo") Long idTo) {
        MessageDto messages = messageService.allUserMessages(idTo);
        return messages.getInMessages().get(id).toArray(Message[]::new);
    }

    @GetMapping("/user/messages/all-senders/{id}")
    public List<List<List<Message>>> allMessagesSenders(@PathVariable(name = "id") Long id) {
        List<List<List<Message>>> list = new ArrayList<>();
        HashMap<Long, List<Message>> map = messageService.allUserMessages(id).getInMessages();
        for(Map.Entry<Long, List<Message>> entry: map.entrySet()){
            List<List<Message>> trans = new ArrayList<>();
            trans.add(entry.getValue());
            list.add(trans);
        }
        return list;
    }
}
