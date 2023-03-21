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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.Objects;

import static itstep.social_freedom.controller.AdminController.addPassword;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FileService fileService;

    private void CreateModelUser(Model model) {
        PostController.giveMainData(model, userService, alertService, messageService);
    }

    //Start index
    @GetMapping("/user")
    public String index(Model model, HttpServletRequest request) {
        User userFrom = userService.allUsers().stream()
                .filter(x-> Objects.equals(x.getUsername(), "ADMIN")).findFirst().orElse(new User());
        User userTo = userService.getCurrentUsername();
        HttpSession session = request.getSession();
        reminderMessage(userFrom, userTo, session);
        CreateModelUser(model);
        return "user/index";
    }

    //Edit user data
    @GetMapping("user/data/edit-data/{id}")
    public String editUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        CreateModelUser(model);
        return "/user/data/user-edit";
    }

    //Edit user data
    @PostMapping("/user/data/user-edit")
    public String Edit(@ModelAttribute("userForm") @Valid User userForm,
                       BindingResult bindingResult, Model model,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(value = "id") Long id,
                       @RequestParam(value = "avatar") MultipartFile file,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "fullName") String fullName,
                       @RequestParam(value = "country") String country,
                       @RequestParam(value = "city") String city,
                       @RequestParam(value = "age") String age,
                       @RequestParam(value = "email", required = false, defaultValue = " ") String email,
                       @RequestParam(value = "passOld") String oldPass) {
        CreateModelUser(model);
        String path = "/user/data/edit-data/" + id;
        User user = userService.findUserById(id);
        boolean edit = false;
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + (path);
        }
        if (AdminController.checkStringCensorship(username) ||
                AdminController.checkStringCensorship(fullName) ||
                AdminController.checkStringCensorship(country) ||
                AdminController.checkStringCensorship(city)) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Using foul language!!");
            return "redirect:" + (path);
        }
        if (!userService.checkPassword(id, oldPass)) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "The old password was entered incorrectly!");
            return "redirect:" + (path);
        }
        if (setDataUser(redirectAttributes, username, fullName, country, city, age, email, user, userService))
            return "redirect:" + path;
        user.setPassword(oldPass);
        if (AdminController.addFile(redirectAttributes, file, user, fileService, userService, edit))
            return "redirect:" + (path);

        return "redirect:/user/data/view/" + id;
    }

    //View Profile User
    @GetMapping("/user/data/view/{id}")
    public String viewProfile(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        int friends = PageController.countFriends(user.getRequestedFriends(), user.getReceivedFriends());
        model.addAttribute("friends", friends);
        model.addAttribute("user", user);
        CreateModelUser(model);
        return "user/data/view-user";
    }

    //Changing the user's password
    @GetMapping("user/data/edit-pass/{id}")
    public String newpassUser(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        CreateModelUser(model);
        return "user/data/user-newpass";
    }

    //New user password
    @PostMapping("/user/newpassword")
    public String newpassword(@ModelAttribute("userForm") @Valid User userForm,
                              BindingResult bindingResult, Model model,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(value = "user_id") Long user_id,
                              @RequestParam(value = "passwordConfirm") String passwordConfirm,
                              @RequestParam(value = "password") String password,
                              @RequestParam(value = "passOld") String passOld) {
        CreateModelUser(model);
        String path = "/user/data/edit-pass/" + user_id;
        User user = userService.findUserById(user_id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + path;
        }

        if (userService.checkPassword(user_id, passOld)) {
            if (addPassword(userForm, redirectAttributes, passwordConfirm, password, user, userService))
                return "redirect:" + path;
        } else {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "The old password was entered incorrectly!");
            return "redirect:" + (path);
        }

        return "redirect:/user";
    }

    //Deleting a user
    @GetMapping("user/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/";
    }


    static boolean setDataUser(RedirectAttributes redirectAttributes,
                               @RequestParam("username") String username,
                               @RequestParam("fullName") String fullName,
                               @RequestParam(value = "country") String country,
                               @RequestParam(value = "city") String city,
                               @RequestParam(value = "age") String age,
                               @RequestParam(value = "email", required = false, defaultValue = " ") String email,
                               User user, UserService userService) {
        if (!Objects.equals(email, "") && email.equals(user.getEmail())) {
            User userBD = userService.findUserByEmail(email);
            if (userBD != null) {
                if (Objects.equals(userBD.getId(), user.getId()))
                    user.setEmail(email);
                else {
                    redirectAttributes.getFlashAttributes().clear();
                    redirectAttributes.addFlashAttribute("error", "User with this email address already exists!");
                    return true;
                }
            } else
                user.setEmail(email);
        }

        if (userService.findUserByUsername(user, username))
            user.setUsername(username);
        else {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "User with the same username already exists!");
            return true;
        }
        if (fullName != null && !fullName.equals("")) {
            user.setFullName(fullName);
        } else {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "FullName not entered!");
            return true;
        }
        if (country == null || country.equals("")) {
            user.setCountry(null);
        } else {
            user.setCountry(country);
        }
        if (city == null || city.equals("")) {
            user.setCity(null);
        } else {
            user.setCity(city);
        }
        if (age == null || age.equals("")) {
            user.setAge(0);
        } else {
            user.setAge(Integer.parseInt(age));
        }
        return false;
    }

    public static void createSendMessage(User userFrom, User userTo, String text, UserService userService,
                                         InviteService inviteService, MessageService messageService) {
        Message message = new Message();
        Invite invite = new Invite();
        if (!Objects.equals(text, "")) {
            if (AdminController.checkStringCensorship(text)) {
                userFrom.setOffenses(userFrom.getOffenses() + 100);
                userService.saveEdit(userFrom);
                text = AdminController.textCheckWords(text);
            }
            invite.setUserFrom(userFrom);
            invite.setUserTo(userTo);
            invite.setStatus(Status.REQUEST);
            message.setMessage(text);
            message.setInvite(invite);
            if (inviteService.saveInvite(invite)) {
                message.setStatus(Status.ACTIVE);
                messageService.saveMessage(message);
            }
        }
    }

    //Reminder Message
    public void reminderMessage(User userFrom, User userTo, HttpSession session){
        String text = "Please confirm your email address in your profile in order to be able to recover your account " +
                "or receive a changed password." +
                "Go to \"View Profile\" and click the activation button next to the email address";
        if (!userTo.isEmailConfirmed())
            if(session.isNew()) {
                session.setAttribute("reminderEmail", true);
                session.setMaxInactiveInterval(-1);
                createSendMessage(userFrom, userTo, text, userService, inviteService, messageService);
            }else{
                Enumeration<String> keys = session.getAttributeNames();
                while(keys.hasMoreElements()){
                    if(!Objects.equals(keys.nextElement(), "reminderEmail")){
                        session.setAttribute("reminderEmail", true);
                        session.setMaxInactiveInterval(-1);
                        createSendMessage(userFrom, userTo, text, userService, inviteService, messageService);
                    }else
                        break;
                }
            }
        else if (userService.checkPassword(userTo.getId(), "Freedom_new23")) {
            text =String.format("Now you can also enter the site through Login: \"%s\" and Password: \"Freedom_new23\". " +
                    "We kindly request you to change your Login and Password during the day. " +
                    "This reminder will come until you change your details. " +
                    "Sincerely, site administration.", userTo.getUsername());
            if(session.isNew()){
                session.setAttribute("reminderPass", true);
                session.setMaxInactiveInterval(-1);
                createSendMessage(userFrom, userTo, text, userService, inviteService, messageService);
            }else{
                Enumeration<String> keys = session.getAttributeNames();
                while(keys.hasMoreElements()){
                    if(!Objects.equals(keys.nextElement(), "reminderPass")){
                        session.setAttribute("reminderPass", true);
                        session.setMaxInactiveInterval(-1);
                        createSendMessage(userFrom, userTo, text, userService, inviteService, messageService);
                    }else
                        break;
                }
            }
        }
    }
}
