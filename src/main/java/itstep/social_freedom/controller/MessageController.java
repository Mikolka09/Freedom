package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.entity.dto.MessageDto;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        List<Message> messages = messageService.findAllMessagesUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .collect(Collectors.toList());
        List<Alert> alerts = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .collect(Collectors.toList());
        if (messageService.allUserMessages(userService.getCurrentUsername().getId()).getInMessages().size() != 0) {
            List<Message> messageList = messageService.allUserMessages(userService.getCurrentUsername().getId())
                    .getInMessages().entrySet().iterator().next().getValue();
            HashMap<Long, List<Message>> list = messageService.allUserMessages(userService.getCurrentUsername().getId()).getInMessages();
            model.addAttribute("messageList", messageList);
            model.addAttribute("list", list);
        }else{
            model.addAttribute("messageList", new ArrayList<>());
            model.addAttribute("list", new HashMap<>());
        }
        String role = "";
        for (Role r : user.getRoles()) {
            if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                role = r.getName();
        }
        model.addAttribute("alerts", alerts);
        model.addAttribute("counter", counterMessages(messages));
        model.addAttribute("messages", messages);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
        model.addAttribute("user", user);
    }

    public static HashMap<Long, Integer> counterMessages(List<Message> messages) {
        HashMap<Long, Integer> counter = new HashMap<>();
        for (Message mess : messages) {
            int count = 0;
            for (Message msg : messages) {
                if (Objects.equals(msg.getInvite().getUserFrom().getId(), mess.getInvite().getUserFrom().getId()))
                    count++;
            }
            counter.put(mess.getInvite().getUserFrom().getId(), count);
        }
        return counter;
    }

    @GetMapping("/user/messages")
    public String index(Model model) {
        CreateModelUser(model);
        return "mail/index";
    }

    @GetMapping("/user/out-messages")
    public String outMessages(Model model){
        List<Message> outMessages = messageService.findAllMessagesOutUserById(userService.getCurrentUsername().getId());
        model.addAttribute("outMessages", outMessages);
        CreateModelUser(model);
        return "mail/index";
    }

    @GetMapping("/user/deleted-messages")
    public String deletedMessages(Model model){
        List<Message> deleteMessages = messageService.findAllDeletedMessagesUserById(userService.getCurrentUsername().getId());
        model.addAttribute("deleteMessages", deleteMessages);
        CreateModelUser(model);
        return "mail/index";
    }
}
