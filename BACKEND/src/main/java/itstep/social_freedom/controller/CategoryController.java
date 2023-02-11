package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.CategoryService;
import itstep.social_freedom.service.UserService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Controller
public class CategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/admin/categories")
    public String categories(Model model) {
        List<Category> categoryList = categoryService.allCategory();
        model.addAttribute("categories", categoryList);
        User user = userService.getCurrentUsername();
        model.addAttribute("admin", user);
        model.addAttribute("status", Status.values());
        return "/admin/categories/categories";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String editCategory(Model model, @PathVariable(name = "id") Long id) {
        Category category = categoryService.findCategoryById(id);
        model.addAttribute("category", category);
        return "/admin/categories/category-edit";
    }

    @PostMapping("/admin/categories/category-store")
    public String editStore(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "name") String name) {
        Category category = categoryService.findCategoryById(id);
        if (!Objects.equals(name, "")) category.setName(name);
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/category-recovery")
    public String recoveryCategory(@RequestParam(value = "id") Long id,
                                   @RequestParam(value = "status") String status) {
        Category category = categoryService.findCategoryById(id);
        if (Objects.equals(status, "DELETED") || Objects.equals(status, "ACTIVE"))
            category.setStatus(Status.valueOf(status));
        categoryService.save(category);
        return "redirect:/admin/categories";
    }


    @PostMapping("/admin/categories/create")
    public String createStore(@RequestParam(value = "name") String name) {
        Category category = new Category();
        if (!Objects.equals(name, "")) category.setName(name);
        category.setStatus(Status.ACTIVE);
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/category/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
