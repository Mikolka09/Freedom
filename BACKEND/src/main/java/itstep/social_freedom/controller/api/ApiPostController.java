package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.Comment;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.repository.CommentRepository;
import itstep.social_freedom.repository.UserRepository;
import itstep.social_freedom.service.PostService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

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
    private int addComment(@PathVariable long id,
                           @RequestParam(value = "userId") String userId,
                           @RequestParam(value = "text") String text) {
        Comment comment = new Comment();
        User user = userRepository.findById((long) Integer.parseInt(userId)).orElse(new User());
        Post post = postService.findPostById(id);
        if (post != null) {
            comment.setPost(post);
            comment.setUser(user);
            comment.setBody(text);
        }
        commentRepository.save(comment);
        return Response.SC_CREATED;
    }

}
