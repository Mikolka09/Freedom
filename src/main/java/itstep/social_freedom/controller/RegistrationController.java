package itstep.social_freedom.controller;

import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;


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
        if (AdminController.checkStringCensorship(userForm.getUsername())) {
            model.addAttribute("error", "Using foul language!");
            return "register/register";
        }
        if (userService.findUserByEmail(userForm.getEmail()) != null) {
            model.addAttribute("error", "User with this email address already exists!");
            return "register/register";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("error", "Passwords do not match!");
            return "register/register";
        }
        if (!userService.saveUser(userForm)) {
            model.addAttribute("error", "User with the same name already exists!");
            return "register/register";
        }

        return "register/login";
    }


}
