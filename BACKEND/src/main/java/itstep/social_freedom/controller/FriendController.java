package itstep.social_freedom.controller;

import itstep.social_freedom.controller.api.ApiMessageController;
import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private MessageService messageService;

    public static List<Friend> giveListFriends(User user) {
        List<Friend> friends = new ArrayList<>();
        for (Friend friendFrom : user.getRequestedFriends()) {
            for (Friend friendTo : user.getReceivedFriends()) {
                if (Objects.equals(friendFrom.getFriendRequester().getId(), friendTo.getFriendReceiver().getId())) {
                    friends.add(friendFrom);
                    break;
                }

            }
        }
        return friends.stream().sorted(Comparator.comparing(x -> x.getFriendReceiver().getUsername()))
                .collect(Collectors.toList());
    }

    @GetMapping("/user/friends")
    public String friends(Model model) {
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

    @GetMapping("/user/friend/break/{id}/{idFriend}")
    public String breakFriend(@PathVariable(name = "id") Long id,
                              @PathVariable(name = "idFriend") Long idFriend) {
        User userFrom = userService.findUserById(id);
        User userTo = userService.findUserById(idFriend);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
        String text = userFrom.getFullName() + " broke off friendly relations with swami from "
                + LocalDateTime.now().format(formatter);
        Alert alert = new Alert();
        Invite invite = new Invite();
        ApiMessageController.createAlert(userTo, userFrom, alert, invite, text, inviteService, alertService);
        friendService.deleteFriend(id, idFriend);
        return "redirect:/user/friends";
    }

    @GetMapping("/user/friends/all-posts")
    private String giveAllFriendsPosts(Model model){
        PostController.giveMainData(model, userService, alertService, messageService);
        User user = userService.getCurrentUsername();
        List<Friend> friends = giveListFriends(user);
        AdminController.getAllFriendsPosts(model, friends, postService);
        return "friend/friends-posts";
    }
}
