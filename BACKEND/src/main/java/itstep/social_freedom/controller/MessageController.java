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
        List<Message> messageList = messageService.allUserMessages(userService.getCurrentUsername().getId())
                .getInMessages().entrySet().iterator().next().getValue();
        HashMap<Long, List<Message>> list = messageService.allUserMessages(userService.getCurrentUsername().getId()).getInMessages();
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                    role = r.getName();
            }
        }

        model.addAttribute("alerts", alerts);
        model.addAttribute("counter", counterMessages(messages));
        model.addAttribute("messageList", messageList);
        model.addAttribute("list", list);
        model.addAttribute("messages", messages);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
        model.addAttribute("user", user);
    }

    public HashMap<Long, Integer> counterMessages(List<Message> messages){
        HashMap<Long, Integer> counter = new HashMap<>();
        for(Message mess:messages) {
            int count = 0;
            for (Message msg : messages) {
                if(Objects.equals(msg.getInvite().getUserFrom().getId(), mess.getInvite().getUserFrom().getId()))
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
}
