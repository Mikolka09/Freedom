package itstep.social_freedom.controller;


import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.Tag;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Controller
public class PostController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Autowired
    private FileService fileService;

    @GetMapping("/post/{id}")
    public String index(@PathVariable(name = "id") Long id, Model model) {
        List<Post> posts = postService.allPosts(id);
        if (!posts.isEmpty()) {
            model.addAttribute("user_id", id);
            model.addAttribute("posts", posts);
            return "post/index";
        } else {
            return "redirect:create/" + id;
        }
    }

    @GetMapping("/post/create/{id}")
    public String create(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("user_id", id);
        List<Category> categories = categoryService.allCategory();
        List<Tag> tags = tagService.allTag();
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        return "post/create-post";
    }

    @PostMapping("/post/store")
    public String store(@RequestParam(value = "id") Long user_id,
                        @RequestParam(value = "file") MultipartFile file,
                        @RequestParam(value = "title") String title,
                        @RequestParam(value = "shortName") String shortName,
                        @RequestParam(value = "category_id") Long category_id,
                        @RequestParam(value = "description") String description,
                        @RequestParam(value = "tag_id") Long[] tag_id) {
        Post post = new Post();
        return setPost(user_id, file, title, shortName, category_id, description, tag_id, post);
    }

    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable(name = "id") Long post_id, Model model) {
        Post post = postService.findPostById(post_id);
        List<Category> categories = categoryService.allCategory();
        List<Tag> tags = tagService.allTag();
        model.addAttribute("user_id", post.getUser().getId());
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        model.addAttribute("post", post);
        return "/post/edit-post";
    }

    @PostMapping("/post/edit-store/{id}")
    public String editStore(@RequestParam(value = "user_id") Long user_id,
                            @PathVariable(name = "id") Long post_id,
                            @RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "title") String title,
                            @RequestParam(value = "shortName") String shortName,
                            @RequestParam(value = "category_id") Long category_id,
                            @RequestParam(value = "description") String description,
                            @RequestParam(value = "tag_id") Long[] tag_id) {
        Post post = postService.findPostById(post_id);
        return setPost(user_id, file, title, shortName, category_id, description, tag_id, post);
    }

    public String setPost(Long user_id, MultipartFile file, String title, String shortName, Long category_id,
                          String description, Long[] tag_id, Post post) {
        post.setUser(userService.findUserById(user_id));
        if (!Objects.equals(title, ""))
            post.setTitle(title);
        if (!Objects.equals(shortName, ""))
            post.setShortName(shortName);
        post.setCategory(categoryService.findCategoryById(category_id));
        if (!Objects.equals(description, ""))
            post.setBody(description);
        if (tag_id != null) {
            if (tag_id.length != 0) {
                Set<Tag> tagSet = new HashSet<>();
                for (Long t : tag_id) {
                    Tag tag = tagService.findTagById(t);
                    tagSet.add(tag);
                }
                post.setTags(tagSet);
            }
        }

        if (file != null) {
            if (!file.isEmpty())
                post.setImgUrl(fileService.uploadFile(file, ""));
        }
        if (!postService.savePost(post))
            return "redirect:/post/store";
        return "redirect:/post/" + user_id;
    }

    @GetMapping("/post/preview/{id}")
    public String previewPost(@PathVariable(name = "id") Long post_id, Model model) {
        Post post = postService.findPostById(post_id);
        String[] bodies = postService.arrayBody(post.getBody());
        List<Category> categories = categoryService.allCategory();
        model.addAttribute("categories", categories);
        model.addAttribute("bodies", bodies);
        model.addAttribute("post", post);
        return "/post/preview-post";
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable(name = "id") Long post_id) {
        postService.deletePost(post_id);
        Long user_id = userService.getCurrentUsername().getId();
        return "redirect:../../post/" + user_id;
    }

}
