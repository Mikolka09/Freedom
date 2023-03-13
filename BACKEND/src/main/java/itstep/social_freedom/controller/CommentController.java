package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.AlertService;
import itstep.social_freedom.service.CommentService;
import itstep.social_freedom.service.MessageService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    private void CreateModelUser(Model model) {
        PostController.giveMainData(model, userService, alertService, messageService);
    }

    //Getting a list of comments
    @GetMapping("/user/comments/{id}")
    public String commentsList(@PathVariable(name = "id") Long id, Model model) {
        List<Comment> comments = commentService.allCommentsUser(id).stream()
                .filter(x->x.getStatus()==Status.ACTIVE)
                .sorted(Comparator.comparing(x->x.getPost().getUser().getUsername())).collect(Collectors.toList());
        model.addAttribute("comments", comments);
        model.addAttribute("status", Status.values());
        CreateModelUser(model);
        return "user/comments/comments";
    }

    @PostMapping("/user/comments/comment-store")
    public String editStore(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "body") String body) {
        Comment comment = commentService.findCommentById(id);
        if (!Objects.equals(body, "")){
            if (AdminController.checkStringCensorship(body)) {
                User user = userService.getCurrentUsername();
                user.setOffenses(user.getOffenses() + 100);
                userService.saveEdit(user);
                String tmp =AdminController.textCheckWords(body);
                comment.setBody(tmp);
            } else
                comment.setBody(body);
        }
        commentService.save(comment);
        return "redirect:" + createPath();
    }

    //Deleting a comment
    @GetMapping("/user/comments/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") Long id) {
        commentService.delete(id);
        return "redirect:" + createPath();
    }

    private String createPath() {
        Long userId = userService.getCurrentUsername().getId();
        return "/user/comments/" + userId;
    }
}
