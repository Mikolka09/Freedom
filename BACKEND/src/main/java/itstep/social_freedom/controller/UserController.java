package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Role;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.FileService;
import itstep.social_freedom.service.PostService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Objects;

import static itstep.social_freedom.controller.AdminController.addPassword;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FileService fileService;

    private void CreateModelUser(Model model) {
        User user = userService.getCurrentUsername();
        model.addAttribute("user", user);
    }

    //Start index
    @GetMapping("/user")
    public String index(Model model) {
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
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "email") String email,
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
        if (!userService.checkPassword(id, oldPass)) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "The old password was entered incorrectly!");
            return "redirect:" + (path);
        }
        if (setDataUser(redirectAttributes, username, name, email, user)) return "redirect:" + path;
        user.setPassword(oldPass);
        if (AdminController.addFile(redirectAttributes, file, user, fileService, userService, edit))
            return "redirect:" + (path);

        return "redirect:/user/data/view/" + id;
    }

    //View Profile User
    @GetMapping("/user/data/view/{id}")
    public String viewProfile(@PathVariable(name = "id") Long id, Model model) {
        User user = userService.findUserById(id);
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
                              @RequestParam(value = "username") String username,
                              @RequestParam(value = "email") String email,
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
                               @RequestParam("name") String name,
                               @RequestParam("email") String email, User user) {
        if (!Objects.equals(email, "") && email.equals(user.getEmail()))
            user.setEmail(email);
        else {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Email cannot be changed!");
            return true;
        }
        user.setUsername(username);
        if (name == null || name.equals("")) {
            user.setName(null);
        } else {
            user.setName(name);
        }
        return false;
    }

}
