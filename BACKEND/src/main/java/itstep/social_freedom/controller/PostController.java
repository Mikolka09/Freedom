package itstep.social_freedom.controller;


import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


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

    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        model.addAttribute("user", user);
    }

    @GetMapping("/user/posts/{id}")
    public String index(@PathVariable(name = "id") Long id, Model model) {
        List<Post> posts = postService.allPosts(id).stream()
                .filter(post -> (post.getStatus() == Status.VERIFIED || post.getStatus() == Status.NOT_VERIFIED))
                .collect(Collectors.toList());
        CreateModelUser(model);
        model.addAttribute("user_id", id);
        model.addAttribute("posts", posts);
        return "user/posts/index";
    }

    @GetMapping("/user/posts/create/{id}")
    public String create(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("userId", id);
        List<Category> categories = categoryService.allCategory();
        List<Tag> tags = tagService.allTag();
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        CreateModelUser(model);
        return "user/posts/create-post";
    }

    @PostMapping("/user/posts/store")
    public String store(@RequestParam(value = "id") Long user_id, Model model,
                        @RequestParam(value = "file") MultipartFile file,
                        @RequestParam(value = "title") String title,
                        @RequestParam(value = "shortDesc") String shortDesc,
                        @RequestParam(value = "category_id") Long category_id,
                        @RequestParam(value = "description") String description,
                        @RequestParam(value = "tag_id") Long[] tag_id) {
        Post post = new Post();
        post.setStatus(Status.NOT_VERIFIED);
        CreateModelUser(model);
        return setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post);
    }

    @GetMapping("/user/posts/edit/{id}")
    public String editPost(@PathVariable(name = "id") Long post_id, Model model) {
        AdminController.gettingPost(post_id, model, postService, categoryService, tagService);
        CreateModelUser(model);
        return "/user/posts/edit-post";
    }

    @PostMapping("/user/posts/edit-store/{id}")
    public String editStore(@RequestParam(value = "user_id") Long user_id, Model model,
                            @PathVariable(name = "id") Long post_id,
                            @RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "title") String title,
                            @RequestParam(value = "shortDesc") String shortDesc,
                            @RequestParam(value = "category_id", required = false, defaultValue = "0") Long category_id,
                            @RequestParam(value = "description") String description,
                            @RequestParam(value = "tag_id", required = false, defaultValue = "") Long[] tag_id) {
        Post post = postService.findPostById(post_id);
        post.setStatus(Status.NOT_VERIFIED);
        CreateModelUser(model);
        return setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post);
    }

    public String setPost(Long user_id, MultipartFile file, String title, String shortName, Long category_id,
                          String description, Long[] tag_id, Post post) {
        addPost(user_id, file, title, shortName, category_id,
                description, tag_id, post, userService, categoryService, tagService, fileService);
        if (!postService.savePost(post))
            return "redirect:/user/posts/store";
        return "redirect:/user/posts/" + user_id;
    }

    static void addPost(Long user_id, MultipartFile file, String title, String shortName,
                        Long category_id, String description, Long[] tag_id, Post post,
                        UserService userService, CategoryService categoryService, TagService tagService,
                        FileService fileService) {
        post.setUser(userService.findUserById(user_id));
        if (!Objects.equals(title, ""))
            post.setTitle(title);
        if (!Objects.equals(shortName, ""))
            post.setShortDescription(shortName);
        if (!category_id.toString().equals("0"))
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
    }

    //Preview post
    @GetMapping("/user/posts/preview/{id}")
    public String previewPost(@PathVariable(name = "id") Long post_id, Model model) {
        Post post = postService.findPostById(post_id);
        User user = userService.getCurrentUsername();
        String[] bodies = postService.arrayBody(post.getBody());
        List<Category> categories = categoryService.allCategory();
        model.addAttribute("categories", categories);
        model.addAttribute("bodies", bodies);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("status", Status.values());
        return "/user/posts/preview-post";
    }

    @GetMapping("/user/posts/delete/{id}")
    public String deletePost(@PathVariable(name = "id") Long post_id) {
        postService.deletePost(post_id);
        Long user_id = userService.getCurrentUsername().getId();
        return "redirect:/user/posts/" + user_id;
    }

}
