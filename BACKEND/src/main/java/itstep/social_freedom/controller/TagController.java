package itstep.social_freedom.controller;

import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.Tag;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.service.TagService;
import itstep.social_freedom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class TagController {

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @GetMapping("/admin/tags")
    public String tags(Model model) {
        List<Tag> tagList = tagService.allTag();
        model.addAttribute("tags", tagList);
        User user = userService.getCurrentUsername();
        model.addAttribute("admin", user);
        model.addAttribute("status", Status.values());
        return "/admin/tags/tags";
    }

    @GetMapping("/admin/tags/edit/{id}")
    public String editTag(Model model, @PathVariable(name = "id") Long id) {
        Tag tag = tagService.findTagById(id);
        model.addAttribute("tag", tag);
        return "/admin/tags/tag-edit";
    }

    @PostMapping("/admin/tags/tag-store")
    public String editStore(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "name") String name) {
        Tag tag = tagService.findTagById(id);
        if (!Objects.equals(name, "")) tag.setName(name);
        tagService.save(tag);
        return "redirect:/admin/tags";
    }

    @PostMapping("/admin/tags/tag-recovery")
    public String recoveryTag(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "status") String status) {
        Tag tag = tagService.findTagById(id);
        if (Objects.equals(status, "DELETED") || Objects.equals(status, "ACTIVE"))
            tag.setStatus(Status.valueOf(status));
        tagService.save(tag);
        return "redirect:/admin/tags";
    }


    @PostMapping("/admin/tags/create")
    public String createStore(@RequestParam(value = "name") String name) {
        Tag tag = new Tag();
        if (!Objects.equals(name, "")) tag.setName(name);
        tag.setStatus(Status.ACTIVE);
        tagService.save(tag);
        return "redirect:/admin/tags";
    }

    @GetMapping("admin/tag/delete/{id}")
    public String deleteTag(@PathVariable(name = "id") Long id) {
        tagService.delete(id);
        return "redirect:/admin/tags";
    }

}
