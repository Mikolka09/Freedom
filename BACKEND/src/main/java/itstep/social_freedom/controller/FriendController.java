package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Friend;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.FriendService;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private MessageService messageService;

    public static List<Friend> giveListFriends(User user){
        List<Friend> friends = new ArrayList<>();
        for (Friend friendFrom : user.getRequestedFriends()) {
            for (Friend friendTo : user.getReceivedFriends()) {
                if (Objects.equals(friendFrom.getFriendRequester().getId(), friendTo.getFriendReceiver().getId())) {
                    friends.add(friendFrom);
                    break;
                }

            }
        }
        return friends;
    }

    @GetMapping("/user/friends")
    public String friends(Model model){
        PostController.giveMainData(model, userService, alertService, messageService);
        User user = userService.getCurrentUsername();
        List<Friend> friends = giveListFriends(user);
        model.addAttribute("friends", friends);
        return "friend/index";
    }

    @GetMapping("/user/friend/view/{id}")
    public String viewProfile(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        int friends = PageController.countFriends(user.getRequestedFriends(), user.getReceivedFriends());
        model.addAttribute("friends", friends);
        model.addAttribute("friend", user);
        PostController.giveMainData(model, userService, alertService, messageService);
        return "friend/view-friend";
    }
}
