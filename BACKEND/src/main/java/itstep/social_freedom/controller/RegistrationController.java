package itstep.social_freedom.controller;

import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.FileService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "register/register";
    }

    @PostMapping("/registration/addUser")
    public String addUser(@ModelAttribute("userForm") @Valid User userForm,
                          BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Not all fields are filled!");
            return "register/register";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("error", "Passwords do not match");
            return "register/register";
        }
        if (!userService.saveUser(userForm)) {
            model.addAttribute("error", "A user with the same name already exists");
            return "register/register";
        }

        return "register/login";
    }

    @PostMapping("/register/edit")
    public String Edit(@ModelAttribute("userForm") @Valid User userForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(value = "id") Long id,
                       @RequestParam(value = "avatar") MultipartFile file,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "email") String email,
                       @RequestParam(value = "passOld") String oldPass,
                       @RequestParam(value = "password") String password,
                       @RequestParam(value = "passwordConfirm") String passwordConfirm) {

        String path = "../registration/edit/" + userForm.getId();

        if (bindingResult.hasErrors()) {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "Not all fields are filled!");
            return "redirect:" + (path);
        }
        userForm.setEmail(email);
        userForm.setUsername(username);

        if (userService.checkPassword(id, oldPass)) {
            if (AdminController.addPassword(userForm, redirectAttributes, passwordConfirm, password, userForm, userService))
                return "redirect:" + (path);
        } else {
            redirectAttributes.getFlashAttributes().clear();
            redirectAttributes.addFlashAttribute("error", "The old password was entered incorrectly!");
            return "redirect:" + (path);
        }

        if (AdminController.addFile(redirectAttributes, file, userForm, fileService, userService))
            return "redirect:" + (path);

        return "redirect:/";
    }

    @GetMapping("registration/edit/{id}")
    public String editUser(@PathVariable(name = "id") Long id, Model model) {

        User user = userService.findUserById(id);
        String url = user.getAvatarUrl();
        user.setAvatarUrl("http://localhost:8080/" + url);
        model.addAttribute("User", user);
        return "/register/edit";
    }

    @GetMapping("registration/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/";
    }

}
