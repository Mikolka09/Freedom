package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.CategoryService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    private void CreateModel(Model model){
        User user = userService.getCurrentUsername();
        List<Category> categories = categoryService.allCategory();
        model.addAttribute("User", user);
        model.addAttribute("categories", categories);
        model.addAttribute("status", Status.values());
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
