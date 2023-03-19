package itstep.social_freedom.controller;

import itstep.social_freedom.controller.api.EmailController;
import itstep.social_freedom.entity.*;
import itstep.social_freedom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private RoleService roleService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private FileService fileService;


    public static void CreateModelUser(Model model, UserService userService, AlertService alertService, MessageService messageService) {
        User user = userService.getCurrentUsername();
        List<Alert> alertList = alertService.findAllAlertsUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.VIEWED)
                .collect(Collectors.toList());
        List<Message> messages = messageService.findAllMessagesUserById(userService.getCurrentUsername().getId())
                .stream().filter(x -> x.getInvite().getStatus() == Status.REQUEST || x.getInvite().getStatus() == Status.NOT_VIEWED)
                .collect(Collectors.toList());
        List<Message> messageList = messageService.allUserMessages(userService.getCurrentUsername().getId())
                .getInMessages().entrySet().iterator().next().getValue();
        HashMap<Long, List<Message>> list = messageService.allUserMessages(userService.getCurrentUsername().getId()).getInMessages();
        String role = "";
        for (Role r : user.getRoles()) {
            if (Objects.equals(r.getName(), "ROLE_EDITOR"))
                role = r.getName();
        }
        userService.saveRatingUser();
        model.addAttribute("counter", MessageController.counterMessages(messages));
        model.addAttribute("messageList", messageList);
        model.addAttribute("list", list);
        model.addAttribute("messages", messages);
        model.addAttribute("alerts", alertList);
        model.addAttribute("status", Status.values());
        model.addAttribute("role", role);
        model.addAttribute("admin", user);
    }

    private static List<String> baseWords() {
        List<String> base = new ArrayList<>();
        String fileName = "/BACKEND/src/main/resources/static/admin/files/british-swear-words.txt";
        String path = new File("").getAbsolutePath() + fileName;
        try {
            base = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base;
    }

    //Main page AdminDashboard
    @GetMapping("/admin")
    public String index(Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/index";
    }

    @GetMapping("/admin/friends")
    public String friends(Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        User user = userService.getCurrentUsername();
        List<Friend> friends = FriendController.giveListFriends(user);
        model.addAttribute("friends", friends);
        return "admin/friend/index";
    }

    @GetMapping("/admin/friend/break/{id}/{idFriend}")
    public String breakFriend(@PathVariable(name = "id") Long id,
                              @PathVariable(name = "idFriend") Long idFriend) {
        friendService.deleteFriend(id, idFriend);
        return "redirect:/admin/friends";
    }


    //View Profile Friend
    @GetMapping("/admin/friend/view/{id}")
    public String viewProfile(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        int friends = PageController.countFriends(user.getRequestedFriends(), user.getReceivedFriends());
        model.addAttribute("friends", friends);
        model.addAttribute("friend", user);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/friend/view-friend";
    }

    @GetMapping("/admin/friends/all-posts")
    private String giveAllFriendsPosts(Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        User user = userService.getCurrentUsername();
        List<Friend> friends = FriendController.giveListFriends(user);
        getAllFriendsPosts(model, friends, postService);
        return "admin/friend/friends-posts";
    }

    static void getAllFriendsPosts(Model model, List<Friend> friends, PostService postService) {
        List<Post> friendPosts = new ArrayList<>();
        List<Post> posts = postService.posts();
        for (Friend friend : friends) {
            for (Post post : posts) {
                if (Objects.equals(post.getUser().getId(), friend.getFriendReceiver().getId()))
                    friendPosts.add(post);
            }
        }
        model.addAttribute("friendPosts", friendPosts);
    }

    //View Profile User
    @GetMapping("/admin/users/view/{id}")
    public String viewProfileUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        int friends = PageController.countFriends(user.getRequestedFriends(), user.getReceivedFriends());
        model.addAttribute("friends", friends);
        model.addAttribute("user", user);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/view-user";
    }

    @GetMapping("/admin/messages-all-users")
    public String indexAllMessages(Model model) {
        List<Message> messageAllList = messageService.findAllUsersMessages();
        model.addAttribute("messageAllList", messageAllList);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/mail/all-messages";
    }

    @GetMapping("/admin/messages")
    public String indexMail(Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/mail/index";
    }

    @GetMapping("/admin/out-messages")
    public String outMessages(Model model) {
        List<Message> outMessages = messageService.findAllMessagesOutUserById(userService.getCurrentUsername().getId());
        model.addAttribute("outMessages", outMessages);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/mail/index";
    }

    @GetMapping("/admin/deleted-messages")
    public String deletedMessages(Model model) {
        List<Message> deleteMessages = messageService.findAllDeletedMessagesUserById(userService.getCurrentUsername().getId());
        model.addAttribute("deleteMessages", deleteMessages);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/mail/index";
    }

    @GetMapping("/admin/alerts")
    public String indexAlert(Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/alert/index";
    }

    //Get a list of deleted users
    @GetMapping("/admin/users/deleted")
    public String deletedUsers(Model model) {
        List<User> users = userService.allUsers().stream()
                .filter(user -> (user.getStatus() == Status.DELETED || user.getStatus() == Status.NOT_ACTIVE))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/deleted-users";
    }

    //Get a list of posts
    @GetMapping("/admin/posts")
    public String posts(Model model) {
        List<Post> posts = postService.posts().stream()
                .filter(post -> post.getStatus() == Status.VERIFIED)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
        model.addAttribute("posts", posts);
        model.addAttribute("status", Status.values());
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/posts/posts";
    }

    //Create a new post
    @GetMapping("/admin/create-post/{id}")
    public String createPost(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("user_id", id);
        List<Category> categories = categoryService.allCategory().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/posts/create-admin-post";
    }

    //Saving a post
    @PostMapping("/admin/store-post")
    public String store(@RequestParam(value = "id") Long user_id, Model model,
                        @RequestParam(value = "file") MultipartFile file,
                        @RequestParam(value = "title") String title,
                        @RequestParam(value = "shortDesc") String shortDesc,
                        @RequestParam(value = "category_id") Long category_id,
                        @RequestParam(value = "description") String description,
                        @RequestParam(value = "tag_id") Long[] tag_id) {
        Post post = new Post();
        post.setStatus(Status.NOT_VERIFIED);
        post.setLikes(0);
        CreateModelUser(model, userService, alertService, messageService);
        if (setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post, null))
            return "redirect:/admin/posts";
        return "redirect:/admin/create-post/" + user_id;
    }

    //Creating a new user
    @GetMapping("/admin/users/create-user")
    public String createUser(Model model) {
        model.addAttribute("userForm", new User());
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/create-user";
    }

    //Saving a new user
    @PostMapping("/register/new-user")
    public String addNewUser(@ModelAttribute("userForm") @Valid User userForm,
                             BindingResult bindingResult, Model model) {
        CreateModelUser(model, userService, alertService, messageService);
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

    //Post editing
    @GetMapping("/admin/post/edit/{id}")
    public String editPost(@PathVariable(name = "id") Long post_id, Model model) {
        gettingPost(post_id, model, postService, categoryService, tagService);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/posts/posts-edit";
    }

    //Getting post data
    static void gettingPost(@PathVariable(name = "id") Long post_id, Model model,
                            PostService postService,
                            CategoryService categoryService,
                            TagService tagService) {
        Post post = postService.findPostById(post_id);
        List<Category> categories = categoryService.allCategory().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Tag> tags = tagService.allTag().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Status> enums = Arrays.asList(Status.values());
        model.addAttribute("user_id", post.getUser().getId());
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        model.addAttribute("post", post);
        model.addAttribute("status", enums);
    }

    //Post Verification
    @GetMapping("/admin/post/verify/{id}")
    public String verifyPost(@PathVariable(name = "id") Long post_id, Model model) {
        CreateModelUser(model, userService, alertService, messageService);
        gettingPost(post_id, model, postService, categoryService, tagService);
        return "admin/posts/post-verify";
    }

    //Getting a list of unverified posts
    @GetMapping("/admin/posts-verified")
    public String postsVerified(Model model) {
        List<Post> posts = postService.posts().stream().filter(x ->
                        (x.getStatus() == Status.NOT_VERIFIED || x.getStatus() == Status.DELETED))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
        model.addAttribute("posts", posts);
        model.addAttribute("status", Status.values());
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/posts/posts-verified";
    }

    //Getting a list of users
    @GetMapping("/admin/users")
    public String usersList(Model model) {
        List<User> users = userService.allUsers().stream()
                .filter(x -> x.getStatus() == Status.ACTIVE)
                .sorted(Comparator.comparing(User::getUsername))
                .collect(Collectors.toList());
        model.addAttribute("allUsers", users);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/users";
    }

    //Getting a list of comments
    @GetMapping("/admin/comments")
    public String commentsList(Model model) {
        List<Comment> comments = commentService.allComments();
        model.addAttribute("comments", comments);
        model.addAttribute("status", Status.values());
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/comments/comments";
    }

    @PostMapping("/admin/comments/comment-store")
    public String editStore(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "body") String body) {
        Comment comment = commentService.findCommentById(id);
        return checkBodyComment(body, comment);
    }

    @PostMapping("/admin/comments/comment-recovery")
    public String recoveryCategory(@RequestParam(value = "id") Long id,
                                   @RequestParam(value = "body") String body,
                                   @RequestParam(value = "status") String status) {
        Comment comment = commentService.findCommentById(id);
        if (Objects.equals(status, "DELETED") || Objects.equals(status, "ACTIVE"))
            comment.setStatus(Status.valueOf(status));
        return checkBodyComment(body, comment);
    }

    private String checkBodyComment(@RequestParam("body") String body, Comment comment) {
        if (!Objects.equals(body, "")) {
            if (checkStringCensorship(body)) {
                User user = userService.getCurrentUsername();
                user.setOffenses(user.getOffenses() + 100);
                userService.saveEdit(user);
                String tmp = textCheckWords(body);
                comment.setBody(tmp);
            } else
                comment.setBody(body);
        }
        commentService.save(comment);
        return "redirect:/admin/comments";
    }

    //Deleting a comment
    @GetMapping("/admin/comments/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") Long id) {
        commentService.delete(id);
        return "redirect:/admin/comments";
    }

    //Deleting a user
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

    //Editing user data
    @GetMapping("admin/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        List<Role> roles = roleService.allRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/user-edit";
    }

    //Recovering a deleted user
    @GetMapping("admin/users/recovery/{id}")
    public String recoveryUser(@PathVariable(name = "id") Long id, Model model) {
        List<Status> enums = Arrays.asList(Status.values());
        User user = userService.findUserById(id);
        List<Role> roles = roleService.allRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("status", enums);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/user-recovery";
    }

    //Changing the user's password
    @GetMapping("admin/users/newpass/{id}")
    public String newpassUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        CreateModelUser(model, userService, alertService, messageService);
        return "admin/users/user-newpass";
    }

    //Post editing
    @PostMapping("/admin/post/edit-store/{id}")
    public String editStore(@RequestParam(value = "user_id") Long user_id, Model model,
                            @PathVariable(name = "id") Long post_id,
                            @RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "title") String title,
                            @RequestParam(value = "shortDesc") String shortDesc,
                            @RequestParam(value = "category_id", required = false, defaultValue = "0") Long category_id,
                            @RequestParam(value = "description") String description,
                            @RequestParam(value = "tag_id", required = false, defaultValue = "") Long[] tag_id) {
        Post post = postService.findPostById(post_id);
        CreateModelUser(model, userService, alertService, messageService);
        if (setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post, null))
            return "redirect:/admin/posts";
        return "redirect:/admin/post/edit/" + user_id;
    }

    //Post Verification
    @PostMapping("/admin/post/verify-store/{id}")
    public String verifyStore(@RequestParam(value = "user_id") Long user_id,
                              RedirectAttributes redirectAttributes, Model model,
                              @PathVariable(name = "id") Long post_id,
                              @RequestParam(value = "file") MultipartFile file,
                              @RequestParam(value = "title") String title,
                              @RequestParam(value = "shortDesc") String shortDesc,
                              @RequestParam(value = "category_id", required = false, defaultValue = "0") Long category_id,
                              @RequestParam(value = "description") String description,
                              @RequestParam(value = "offenses", required = false) String offenses,
                              @RequestParam(value = "tag_id", required = false, defaultValue = "") Long[] tag_id,
                              @RequestParam(value = "status") String status) {
        CreateModelUser(model, userService, alertService, messageService);
        Post post = postService.findPostById(post_id);
        String path = "/admin/post/verify/" + post_id;
        if (!status.isEmpty()) {
            if (status.equals("VERIFIED") || status.equals("NOT_VERIFIED") || status.equals("DELETED"))
                post.setStatus(Status.valueOf(status));
            else {
                redirectAttributes.getFlashAttributes().clear();
                redirectAttributes.addFlashAttribute("error", "Wrong status selected!");
                return "redirect:" + path;
            }
        }
        if (setPost(user_id, file, title, shortDesc, category_id, description, tag_id, post, offenses)) {
            if (postService.posts().stream().noneMatch(x -> x.getStatus() == Status.NOT_VERIFIED))
                return "redirect:/admin/posts";
            return "redirect:/admin/posts-verified";
        }
        return "redirect:/admin/post/verify/" + user_id;
    }

    //Editing user data
    @PostMapping("/admin/edit-store")
    public String Edit(@ModelAttribute("userForm") @Valid User userForm,
                       BindingResult bindingResult, Model model,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(value = "user_id") Long user_id,
                       @RequestParam(value = "avatar") MultipartFile file,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "fullName") String fullName,
                       @RequestParam(value = "country") String country,
                       @RequestParam(value = "city") String city,
                       @RequestParam(value = "age") String age,
                       @RequestParam(value = "email") String email,
                       @RequestParam(value = "role_id", required = false, defaultValue = "") Long[] role_id) {
        CreateModelUser(model, userService, alertService, messageService);
        String path = "/admin/users/edit/" + user_id;
        User user = userService.findUserById(user_id);
        boolean edit = true;
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + path;
        }
        if (UserController.setDataUser(redirectAttributes, username, fullName, country, city, age, email, user, userService))
            return "redirect:" + path;
        if (addRoles(redirectAttributes, file, role_id, user, edit)) return "redirect:" + path;

        return "redirect:/admin/users";
    }

    //User recovery
    @PostMapping("/admin/recovery")
    public String recovery(@ModelAttribute("userForm") @Valid User userForm,
                           BindingResult bindingResult, Model model,
                           RedirectAttributes redirectAttributes,
                           @RequestParam(value = "user_id") Long user_id,
                           @RequestParam(value = "avatar") MultipartFile file,
                           @RequestParam(value = "username") String username,
                           @RequestParam(value = "fullName") String fullName,
                           @RequestParam(value = "country") String country,
                           @RequestParam(value = "city") String city,
                           @RequestParam(value = "age") String age,
                           @RequestParam(value = "email") String email,
                           @RequestParam(value = "role_id", required = false, defaultValue = "") Long[] role_id,
                           @RequestParam(value = "passwordConfirm") String passwordConfirm,
                           @RequestParam(value = "password") String password,
                           @RequestParam(value = "status") String status) {
        CreateModelUser(model, userService, alertService, messageService);
        String path = "/admin/users/recovery/" + user_id;
        User user = userService.findUserById(user_id);
        boolean edit = true;
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + path;
        }
        if (UserController.setDataUser(redirectAttributes, username, fullName, country, city, age, email, user, userService))
            return "redirect:" + path;

        if (addPassword(userForm, redirectAttributes, passwordConfirm, password, user, userService))
            return "redirect:" + path;

        sendNewPassword(password, user.getEmail(), "YOUR ACCOUNT HAS BEEN RESTORED");

        if (!status.isEmpty()) {
            if (status.equals("VERIFIED") || status.equals("NOT_VERIFIED") || status.equals("REQUEST") ||
                    status.equals("ACCEPTED") || status.equals("DENIED")) {
                redirectAttributes.getFlashAttributes().clear();
                redirectAttributes.addFlashAttribute("error", "Wrong status selected!");
                return "redirect:" + path;
            } else
                user.setStatus(Status.valueOf(status));
        }
        if (addRoles(redirectAttributes, file, role_id, user, edit)) return "redirect:" + path;
        if (userService.allUsers().stream().noneMatch(x -> x.getStatus() == Status.DELETED))
            return "redirect:/admin/users";
        return "redirect:/admin/users/deleted";
    }

    private boolean addRoles(RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile file, @RequestParam(value = "role_id", required = false, defaultValue = "") Long[] role_id, User user, boolean edit) {
        if (role_id != null) {
            if (role_id.length != 0) {
                Set<Role> rolesSet = new HashSet<>();
                for (Long r : role_id) {
                    Role role = roleService.findRoleById(r);
                    rolesSet.add(role);
                }
                user.setRoles(rolesSet);
            }
        }
        return addFile(redirectAttributes, file, user, fileService, userService, edit);
    }

    //New user password
    @PostMapping("/admin/newpassword")
    public String newpassword(@ModelAttribute("userForm") @Valid User userForm,
                              BindingResult bindingResult, Model model,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(value = "user_id") Long user_id,
                              @RequestParam(value = "passwordConfirm") String passwordConfirm,
                              @RequestParam(value = "password") String password) {
        CreateModelUser(model, userService, alertService, messageService);
        String path = "/admin/users/newpass/" + user_id;
        User user = userService.findUserById(user_id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + path;
        }

        if (addPassword(userForm, redirectAttributes, passwordConfirm, password, user, userService))
            return "redirect:" + path;
        sendNewPassword(password, user.getEmail(), "NEW PASSWORD");
        return "redirect:/admin/users";
    }

    //Deleting a post
    @GetMapping("/admin/post/delete/{id}")
    public String deletePost(@PathVariable(name = "id") Long post_id) {
        postService.deletePost(post_id);
        return "redirect:/admin/posts/";
    }

    //Saving a new post
    public boolean setPost(Long user_id, MultipartFile file, String title, String shortDesc, Long category_id,
                           String description, Long[] tag_id, Post post, String offenses) {
        PostController.addPost(user_id, file, title, shortDesc, category_id, description, tag_id, post,
                userService, categoryService, tagService, fileService, offenses);
        return postService.savePost(post);
    }

    //Saving a new password
    static boolean addPassword(@ModelAttribute("userForm") @Valid User userForm, RedirectAttributes redirectAttributes,
                               @RequestParam("passwordConfirm") String passwordConfirm,
                               @RequestParam("password") String password,
                               User user, UserService service) {
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
            if (!service.saveNewPassword(user)) {
                redirectAttributes.getFlashAttributes().clear();
                redirectAttributes.addFlashAttribute("error", "Passwords do not save!");
                return true;
            }
        }
        return false;
    }

    public void sendNewPassword(String pass, String email,String subject){
        String path = "/pages/fragments/email/new-password.html";
        HashMap<String, Object> base = new HashMap<>();
        base.put("password", pass);
        try {
            emailService.sendSimpleEmail(EmailController.createEmailSend(email,subject, base, path));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    //Saving a file
    static boolean addFile(RedirectAttributes redirectAttributes, @RequestParam("avatar") MultipartFile file,
                           User user, FileService fileService, UserService userService, boolean edit) {
        if (file != null) {
            if (!file.isEmpty())
                user.setAvatarUrl(fileService.uploadFile(file, "avatar/"));
        }

        if (edit) {
            if (!userService.saveEdit(user)) {
                redirectAttributes.getFlashAttributes().clear();
                redirectAttributes.addFlashAttribute("error", "A user with the same name already exists!");
                return true;
            }
        } else {
            if (!userService.save(user)) {
                redirectAttributes.getFlashAttributes().clear();
                redirectAttributes.addFlashAttribute("error", "A user with the same name already exists!");
                return true;
            }
        }
        return false;
    }

    public static boolean checkStringCensorship(String text) {
        List<String> base = baseWords();
        for (String st : base) {
            String reg = String.format("\\b%s\\b", st);
            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
                return true;
        }
        return false;
    }


    public static String textCheckWords(String text) {
        List<String> base = baseWords();
        String change = "{censorship!}";
        for (String st : base) {
            String reg = String.format("\\b%s\\b", st);
            Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                text = matcher.replaceAll(change);
            }
        }
        return text;
    }

}
