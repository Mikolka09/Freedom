package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.Comment;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.entity.helpers.HelperComment;
import itstep.social_freedom.repository.CommentRepository;
import itstep.social_freedom.repository.UserRepository;
import itstep.social_freedom.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor.DATE_FORMAT;

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
    private HelperComment addComment(@PathVariable long id,
                                     @RequestParam(value = "userId") String userId,
                                     @RequestParam(value = "text") String text) {
        HelperComment helperComment = new HelperComment();
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Comment comment = new Comment();
        User user = userRepository.findById((long) Integer.parseInt(userId)).orElse(new User());
        Post post = postService.findPostById(id);
        String date = formatter.format(new Date().getTime());
        if (post != null) {
            helperComment.setAvatarUrl(user.getAvatarUrl());
            helperComment.setFullName(user.getFullName());
            helperComment.setComments(post.getComments().size() + 1);
            helperComment.setCreateDate(date);
            helperComment.setBody(text);

            comment.setPost(post);
            comment.setUser(user);
            comment.setBody(text);
        }
        commentRepository.save(comment);
        return helperComment;
    }

}
