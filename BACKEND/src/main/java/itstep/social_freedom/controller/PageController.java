package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.CategoryService;
import itstep.social_freedom.service.PostService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    private void CreateModel(Model model){
        User user = userService.getCurrentUsername();
        List<Category> categories = categoryService.allCategory();
        String role = "";
        if(user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_ADMIN"))
                    role = r.getName();
                if (Objects.equals(r.getName(), "ROLE_USER"))
                    role = r.getName();
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Post> posts = postService.posts()
                .stream().filter(post -> post.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        List<Post> busPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Business")).collect(Collectors.toList());
        List<Post> celPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Celebrity")).collect(Collectors.toList());
        List<Post> culPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Culture")).collect(Collectors.toList());
        List<Post> foodPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Food")).collect(Collectors.toList());
        List<Post> lifePosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Lifestyle")).collect(Collectors.toList());
        List<Post> polPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Politics")).collect(Collectors.toList());
        List<Post> sportPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Sport")).collect(Collectors.toList());
        List<Post> starPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Startups")).collect(Collectors.toList());
        List<Post> travelPosts = posts.stream()
                .filter(post-> Objects.equals(post.getCategory().getName(), "Travel")).collect(Collectors.toList());
        model.addAttribute("busPosts", busPosts);
        model.addAttribute("celPosts", celPosts);
        model.addAttribute("culPosts", culPosts);
        model.addAttribute("foodPosts", foodPosts);
        model.addAttribute("lifePosts", lifePosts);
        model.addAttribute("polPosts", polPosts);
        model.addAttribute("sportPosts", sportPosts);
        model.addAttribute("starPosts", starPosts);
        model.addAttribute("travelPosts", travelPosts);
        CreateModel(model);
        return "pages/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        CreateModel(model);
        return "pages/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        CreateModel(model);
        return "pages/contact";
    }

    @GetMapping("/category")
    public String category(Model model) {
        CreateModel(model);
        return "pages/category";
    }

}
