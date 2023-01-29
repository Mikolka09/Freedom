package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Role;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.CategoryService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Controller
public class PageController {

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
