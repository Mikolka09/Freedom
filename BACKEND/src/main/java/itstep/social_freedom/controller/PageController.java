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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                .sorted((x1, x2) -> Integer.compare(x2.getComments().size(), x1.getComments().size())).collect(Collectors.toList());
        List<Post> likesPosts = posts.stream()
                .sorted(Comparator.comparingInt(Post::getLikes).reversed()).collect(Collectors.toList());
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        String role = "";
        if (user != null) {
            for (Role r : user.getRoles()) {
                if (Objects.equals(r.getName(), "ROLE_ADMIN"))
                    role = r.getName();
                if (Objects.equals(r.getName(), "ROLE_USER"))
                    role = r.getName();
            }
        }
        userService.saveRatingUser();
        model.addAttribute("trendingPosts", trendingPosts);
        model.addAttribute("likesPosts", likesPosts);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
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
        int pages = (int) Math.ceil(categoryPosts.size() / count);
        model.addAttribute("categoryPosts", categoryPosts);
        model.addAttribute("pages", new int[pages]);
        CreateModel(model);
        return "pages/category";
    }

    @GetMapping("/view-post/{id}")
    public String viewPost(@PathVariable(name = "id") Long post_id, Model model) {
        Post post = postService.findPostById(post_id);
        String myFriend = "";
        if (userService.getCurrentUsername() != null) {
            Long idRead = userService.getCurrentUsername().getId();
            if (post.getUser().getRequestedFriends().stream()
                    .noneMatch(x -> Objects.equals(x.getFriendRequester().getId(), idRead)) &&
                    post.getUser().getReceivedFriends().stream()
                            .noneMatch(x -> Objects.equals(x.getFriendRequester().getId(), idRead)))
                myFriend = "no";
            else
                myFriend = "yes";
        }
        int friends = countFriends(post.getUser().getRequestedFriends(), post.getUser().getReceivedFriends());
        List<Comment> comments = post.getComments().stream().filter(x -> x.getStatus() == Status.ACTIVE)
                .sorted(Comparator.comparing(Comment::getCreatedAt)).collect(Collectors.toList());
        String[] bodies = postService.arrayBody(post.getBody());
        model.addAttribute("bodies", bodies);
        model.addAttribute("comments", comments);
        model.addAttribute("post", post);
        model.addAttribute("myFriend", myFriend);
        model.addAttribute("friends", friends);
        CreateModel(model);
        return "/pages/view-post";
    }

    static public int countFriends(Set<Friend> requestedFriends, Set<Friend> receivedFriends) {
        int count = 0;
        for (Friend friendFrom : requestedFriends) {
            for (Friend friendTo : receivedFriends) {
                if (Objects.equals(friendFrom.getFriendRequester().getId(), friendTo.getFriendReceiver().getId())) {
                    count++;
                    break;
                }

            }
        }
        return count;
    }

    @GetMapping("/search-post-tags/{text}")
    public String searchPostForTag(Model model, @PathVariable(value = "text") String text) {
        return getStringSearch(model, text);
    }

    private String getStringSearch(Model model, @PathVariable("text") String text) {
        List<Post> postList = postService.posts().stream()
                .filter(x -> x.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        String[] arrText = text.split(" ");
        List<Post> result = new ArrayList<>();
        result = searchPostsResult(postList, arrText, result);
        double count = 8.0;
        int pages = 0;
        String answer = "";
        if (result.size() != 0) {
            pages = (int) Math.ceil(result.size() / count);
            answer = "Search Results - \"" + text + "\"";
        } else
            answer = "No results were found for your query - \""
                    + text.toUpperCase() + "\". Try changing your search terms!";
        model.addAttribute("result", result);
        model.addAttribute("pages", new int[pages]);
        model.addAttribute("answer", answer);
        model.addAttribute("text", text);
        CreateModel(model);
        return "/pages/search-result-posts";
    }

    @PostMapping("/search-post")
    public String searchPost(Model model, @RequestParam(value = "text") String text) {
        return getStringSearch(model, text);
    }

    public static List<Post> searchPostsResult(List<Post> posts, String[] arrText, List<Post> result) {
        if (arrText != null) {
            if (arrText.length > 0) {
                for (String s : arrText) {
                    for (Post post : posts) {
                        if (post.getTitle().contains(s) || post.getShortDescription().contains(s) ||
                                searchPostsTags(post, s)) {
                            if (result.size() == 0) {
                                result.add(post);
                            } else {
                                if (result.stream().noneMatch(x -> Objects.equals(x.getId(), post.getId())))
                                    result.add(post);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean searchPostsTags(Post post, String text){
        Tag[] tags = post.getTags().toArray(Tag[]::new);
        for(Tag t:tags){
            if(t.getName().equalsIgnoreCase(text))
                    return true;
        }
        return false;
    }

}
