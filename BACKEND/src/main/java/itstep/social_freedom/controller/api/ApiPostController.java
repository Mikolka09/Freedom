package itstep.social_freedom.controller.api;

import itstep.social_freedom.entity.Post;
import itstep.social_freedom.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    @Autowired
    private PostService postService;

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
}
