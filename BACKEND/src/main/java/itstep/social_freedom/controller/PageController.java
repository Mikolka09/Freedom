package itstep.social_freedom.controller;

import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.CategoryService;
import itstep.social_freedom.service.PostService;
import itstep.social_freedom.service.TagService;
import itstep.social_freedom.service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    private void CreateModel(Model model) {
        User user = userService.getCurrentUsername();
        List<Post> posts = postService.posts()
                .stream().filter(post -> post.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        List<Category> categoriesAll = categoryService.allCategory().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Post> trendingPosts = posts.stream()
                .sorted((x1, x2)->Integer.compare(x2.getComments().size(), x1.getComments().size())).collect(Collectors.toList());
        List<Post> likesPosts = posts.stream()
                .sorted(Comparator.comparingInt(Post::getLikes).reversed()).collect(Collectors.toList());
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_ADMIN"))
                    role = r.getName();
                if (Objects.equals(r.getName(), "ROLE_USER"))
                    role = r.getName();
            }
        }
        model.addAttribute("trendingPosts", trendingPosts);
        model.addAttribute("likesPosts", likesPosts);
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("categoriesAll", categoriesAll);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Post> posts = postService.posts()
                .stream().filter(post -> post.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        List<Post> busPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Business")).collect(Collectors.toList());
        List<Post> celPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Celebrity")).collect(Collectors.toList());
        List<Post> culPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Culture")).collect(Collectors.toList());
        List<Post> foodPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Food")).collect(Collectors.toList());
        List<Post> lifePosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Lifestyle")).collect(Collectors.toList());
        List<Post> polPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Politics")).collect(Collectors.toList());
        List<Post> sportPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Sport")).collect(Collectors.toList());
        List<Post> starPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Startups")).collect(Collectors.toList());
        List<Post> travelPosts = posts.stream()
                .filter(post -> Objects.equals(post.getCategory().getName(), "Travel")).collect(Collectors.toList());
        model.addAttribute("posts", posts);
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

    @GetMapping("/categories")
    public String categories(Model model) {
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        CreateModel(model);
        model.addAttribute("tags", tags);
        return "pages/categories";
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

    @GetMapping("/category/{id}")
    public String category(Model model, @PathVariable Long id) {
        List<Post> categoryPosts = postService.posts().stream().filter(x -> x.getStatus() == Status.VERIFIED)
                .filter(x -> Objects.equals(x.getCategory().getId(), id)).collect(Collectors.toList());
        if (categoryPosts.size() == 0)
            return "redirect:/";
        double count = 8.0;
        int pages = (int)Math.ceil(categoryPosts.size()/count);
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        model.addAttribute("categoryPosts", categoryPosts);
        model.addAttribute("tags", tags);
        model.addAttribute("pages", new int[pages]);
        CreateModel(model);
        return "pages/category";
    }

    @GetMapping("/view-post/{id}")
    public String viewPost(@PathVariable(name = "id") Long post_id, Model model) {
        Post post = postService.findPostById(post_id);
        Long idRead = post.getUser().getId();
        String myFriend = "";
        if(post.getUser().getRequestedFriends().stream()
                .noneMatch(x -> Objects.equals(x.getFriendRequester().getId(), idRead)))
            myFriend = "no";
        else
            myFriend="yes";
        List<Comment> comments = post.getComments().stream().filter(x -> x.getStatus() == Status.ACTIVE)
                .sorted(Comparator.comparing(Comment::getCreatedAt)).collect(Collectors.toList());
        String[] bodies = postService.arrayBody(post.getBody());
        model.addAttribute("bodies", bodies);
        model.addAttribute("comments", comments);
        model.addAttribute("post", post);
        model.addAttribute("myFriend", myFriend);
        CreateModel(model);
        return "/pages/view-post";
    }

}
