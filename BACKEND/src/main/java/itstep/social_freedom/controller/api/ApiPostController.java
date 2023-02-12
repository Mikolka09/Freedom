package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.Comment;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.repository.CommentRepository;
import itstep.social_freedom.repository.UserRepository;
import itstep.social_freedom.service.CommentService;
import itstep.social_freedom.service.PostService;
import itstep.social_freedom.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

import static java.lang.Integer.*;

@RestController
public class ApiPostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/api/posts/likes/{id}")
    private Post addLikes(@PathVariable long id) {
        Post post = postService.findPostById(id);
        if (post.getLikes() == null)
            post.setLikes(1);
        else
            post.setLikes(post.getLikes() + 1);
        postService.savePost(post);
        return post;
    }

    @GetMapping("/api/posts/comment/{id}")
    private Comment[] addComment(@PathVariable long id,
                                 @RequestParam(value = "userId") Long userId,
                                 @RequestParam(value = "text") String text) {
        Comment comment = new Comment();
        User user = userService.findUserById(userId);
        Post post = postService.findPostById(id);
        if (post != null) {
            comment.setPost(post);
            comment.setUser(user);
            comment.setBody(text);
            comment.setStatus(Status.ACTIVE);
        }
        if (commentService.save(comment)) {
            Comment[] comments = postService.findPostById(id).getComments().stream().filter(x -> x.getStatus() == Status.ACTIVE)
                    .sorted(Comparator.comparing(Comment::getCreatedAt)).toArray(Comment[]::new);
            return comments;
        }
        return null;
    }

}
