package itstep.social_freedom.controller;


import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private AlertService alertService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FileService fileService;

    private void CreateModelUser(Model model) {
        giveMainData(model, userService, alertService, messageService);
    }

    //Give main data
    static void giveMainData(Model model, UserService userService, AlertService alertService, MessageService messageService) {
        User user = userService.getCurrentUsername();
        List<Alert> alertList = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .collect(Collectors.toList());
        List<Message> messages = messageService.findAllMessagesUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .collect(Collectors.toList());
        String role = "";
        for (Role r : user.getRoles()) {
            if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                role = r.getName();
        }
        userService.saveRatingUser();
        model.addAttribute("messages", messages);
        model.addAttribute("alerts", alertList);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
        model.addAttribute("user", user);
    }

    @GetMapping("/user/posts/{id}")
    public String index(@PathVariable(name = "id") Long id, Model model) {
        List<Post> posts = postService.allPosts(id).stream()
                .filter(post -> (post.getStatus() == Status.VERIFIED || post.getStatus() == Status.NOT_VERIFIED))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
        CreateModelUser(model);
        model.addAttribute("user_id", id);
        model.addAttribute("posts", posts);
        model.addAttribute("status", Status.values());
        return "user/posts/index";
    }

    @GetMapping("/user/posts/create/{id}")
    public String create(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("userId", id);
        List<Category> categories = categoryService.allCategory().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
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
                        @RequestParam(value = "tag_id") Long[] tag_id) throws IOException {
        Post post = new Post();
        post.setStatus(Status.NOT_VERIFIED);
        post.setLikes(0);
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
                            @RequestParam(value = "tag_id", required = false, defaultValue = "") Long[] tag_id) throws IOException {
        Post post = postService.findPostById(post_id);
        post.setStatus(Status.NOT_VERIFIED);
        CreateModelUser(model);
        return setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post);
    }

    public String setPost(Long user_id, MultipartFile file, String title, String shortDesc, Long category_id,
                          String description, Long[] tag_id, Post post) throws IOException {
        addPost(user_id, file, title, shortDesc, category_id,
                description, tag_id, post, userService, categoryService, tagService, fileService, null);
        if (!postService.savePost(post))
            return "redirect:/user/posts/store";
        return "redirect:/user/posts/" + user_id;
    }

    static void addPost(Long user_id, MultipartFile file, String title, String shortDesc,
                        Long category_id, String description, Long[] tag_id, Post post,
                        UserService userService, CategoryService categoryService, TagService tagService,
                        FileService fileService, String offenses) throws IOException {
        User user = userService.findUserById(user_id);
        post.setUser(user);
        if (offenses != null) {
            user.setOffenses(user.getOffenses() + 100);
            userService.saveEdit(user);
        }
        if (!Objects.equals(title, "")) {
            if (AdminController.checkStringCensorship(title)) {
                user.setOffenses(user.getOffenses() + 100);
                userService.saveEdit(user);
                String temp = AdminController.textCheckWords(title);
                post.setTitle(temp);
            } else
                post.setTitle(title);
        }
        if (!Objects.equals(shortDesc, "")) {
            if (AdminController.checkStringCensorship(shortDesc)) {
                user.setOffenses(user.getOffenses() + 100);
                userService.saveEdit(user);
                String temp = AdminController.textCheckWords(shortDesc);
                post.setShortDescription(temp);
            } else
                post.setShortDescription(shortDesc);
        }
        if (!category_id.toString().equals("0"))
            post.setCategory(categoryService.findCategoryById(category_id));
        if (!Objects.equals(description, "")) {
            if (AdminController.checkStringCensorship(description)) {
                user.setOffenses(user.getOffenses() + 100);
                userService.saveEdit(user);
                String temp = AdminController.textCheckWords(description);
                post.setBody(temp);
            } else
                post.setBody(description);
        }
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
        List<Category> categories = categoryService.allCategory().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
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
