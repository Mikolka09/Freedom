package itstep.social_freedom.service;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> allCategory() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(new Category());
    }

    public boolean save(Category category) {
        Category categoryDB = categoryRepository.findCategoriesByName(category.getName());
        if (categoryDB != null)
            return false;
        categoryRepository.save(category);
        return true;
    }

    public void delete(Long id) {
        if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findById(id).orElse(new Category());
            categoryRepository.delete(category);
        }
    }
}
