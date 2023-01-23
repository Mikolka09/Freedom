package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Autowired
    private FileService fileService;


    @GetMapping("/admin")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/admin/users/deleted")
    public String deletedUsers(Model model) {
        List<User> users = userService.allUsers().stream()
                .filter(user -> user.getStatus() == Status.DELETED)
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admin/users/deleted-users";
    }

    @GetMapping("/admin/posts")
    public String posts(Model model) {
        List<Post> posts = postService.posts();
        model.addAttribute("posts", posts);
        return "admin/posts/posts";
    }

    @GetMapping("/admin/users/create-user")
    public String createUser(Model model) {
        model.addAttribute("userForm", new User());
        return "admin/users/create-user";
    }

    @PostMapping("/register/new-user")
    public String addNewUser(@ModelAttribute("userForm") @Valid User userForm,
                          BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Not all fields are filled!");
            return "admin/users/create-user";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("error", "Passwords do not match");
            return "admin/users/create-user";
        }
        if (!userService.saveUser(userForm)) {
            model.addAttribute("error", "A user with the same name already exists");
            return "admin/users/create-user";
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/post/edit/{id}")
    public String editPost(@PathVariable(name = "id") Long post_id, Model model) {
        gettingPost(post_id, model, postService, categoryService, tagService);
        return "admin/posts/posts-edit";
    }

    static void gettingPost(@PathVariable(name = "id") Long post_id, Model model, PostService postService, CategoryService categoryService, TagService tagService) {
        Post post = postService.findPostById(post_id);
        List<Category> categories = categoryService.allCategory();
        List<Tag> tags = tagService.allTag();
        model.addAttribute("user_id", post.getUser().getId());
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        model.addAttribute("post", post);
    }

    @GetMapping("/admin/post/verify/{id}")
    public String verifyPost(@PathVariable(name = "id") Long post_id, Model model) {
        gettingPost(post_id, model, postService, categoryService, tagService);
        return "admin/posts/post-verify";
    }

    @GetMapping("/admin/posts-verified")
    public String postsVerified(Model model) {
        List<Post> posts = postService.posts().stream().filter(x -> x.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        model.addAttribute("posts", posts);
        return "admin/posts/posts-verified";
    }

    @GetMapping("/admin/users")
    public String usersList(Model model) {
        List<User> users = userService.allUsers().stream().filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        model.addAttribute("allUsers", users);
        return "admin/users/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/gt/{userId}")
    public String gtUser(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        return "admin";
    }

    @GetMapping("admin/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("User", user);
        return "admin/users/user-edit";
    }

    @GetMapping("admin/users/recovery/{id}")
    public String recoveryUser(@PathVariable(name = "id") Long id, Model model) {
        List<Status> enums = Arrays.asList(Status.values());
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("status", enums);
        return "admin/users/user-recovery";
    }

    @GetMapping("admin/users/newpass/{id}")
    public String newpassUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "admin/users/user-newpass";
    }

    @PostMapping("/admin/post/edit-store/{id}")
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

    @PostMapping("/admin/post/verify-store/{id}")
    public String verifyStore(@RequestParam(value = "user_id") Long user_id,
                              @PathVariable(name = "id") Long post_id,
                              @RequestParam(value = "file") MultipartFile file,
                              @RequestParam(value = "title") String title,
                              @RequestParam(value = "shortName") String shortName,
                              @RequestParam(value = "category_id") Long category_id,
                              @RequestParam(value = "description") String description,
                              @RequestParam(value = "tag_id") Long[] tag_id,
                              @RequestParam(value = "verify") Boolean verify) {
        Post post = postService.findPostById(post_id);

        if (verify) post.setStatus(Status.VERIFIED);
        return setPost(user_id, file, title, shortName, category_id, description, tag_id, post);
    }


    @PostMapping("/admin/edit-store")
    public String Edit(@ModelAttribute("userForm") @Valid User userForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(value = "user_id") Long user_id,
                       @RequestParam(value = "avatar") MultipartFile file,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "email") String email) {

        String path = "../admin/users/edit/" + user_id;
        User user = userService.findUserById(user_id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + (path);
        }
        user.setEmail(email);
        user.setUsername(username);

        if (addFile(redirectAttributes, file, user, fileService, userService)) return "redirect:" + (path);

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/recovery")
    public String recovery(@ModelAttribute("userForm") @Valid User userForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           @RequestParam(value = "user_id") Long user_id,
                           @RequestParam(value = "avatar") MultipartFile file,
                           @RequestParam(value = "username") String username,
                           @RequestParam(value = "email") String email,
                           @RequestParam(value = "passwordConfirm") String passwordConfirm,
                           @RequestParam(value = "password") String password,
                           @RequestParam(value = "status") String status) {

        String path = "/admin/users/recovery/" + user_id;
        User user = userService.findUserById(user_id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + (path);
        }
        user.setEmail(email);
        user.setUsername(username);

        if (addPassword(userForm, redirectAttributes, passwordConfirm, password, user)) return "redirect:" + (path);

        if (!status.isEmpty()) user.setStatus(Status.valueOf(status));

        if (addFile(redirectAttributes, file, user, fileService, userService)) return "redirect:" + (path);

        return "redirect:/admin/users/deleted";
    }


    @PostMapping("/admin/newpassword")
    public String newpassword(@ModelAttribute("userForm") @Valid User userForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(value = "user_id") Long user_id,
                              @RequestParam(value = "username") String username,
                              @RequestParam(value = "email") String email,
                              @RequestParam(value = "passwordConfirm") String passwordConfirm,
                              @RequestParam(value = "password") String password) {

        String path = "../admin/users/newpass/" + user_id;
        User user = userService.findUserById(user_id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + (path);
        }

        if (addPassword(userForm, redirectAttributes, passwordConfirm, password, user)) return "redirect:" + (path);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/post/delete/{id}")
    public String deletePost(@PathVariable(name = "id") Long post_id) {
        postService.deletePost(post_id);
        return "redirect:/admin/posts/";
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
            return "redirect:/admin/post/edit/ + user_id";
        return "redirect:/admin/posts";
    }

    static boolean addPassword(@ModelAttribute("userForm") @Valid User userForm, RedirectAttributes redirectAttributes,
                               @RequestParam("passwordConfirm") String passwordConfirm,
                               @RequestParam("password") String password,
                               User user) {
        if (Objects.equals(password, "") && Objects.equals(passwordConfirm, "")) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return true;
        } else if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return true;
        } else {
            user.setPassword(password);
            user.setPasswordConfirm(passwordConfirm);
        }
        return false;
    }

    static boolean addFile(RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile file,
                           User user, FileService fileService, UserService userService) {
        if (file != null) {
            if (!file.isEmpty())
                user.setAvatarUrl(fileService.uploadFile(file, "avatar/"));
        }

        if (!userService.save(user)) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "A user with the same name already exists!");
            return true;
        }
        return false;
    }


}
