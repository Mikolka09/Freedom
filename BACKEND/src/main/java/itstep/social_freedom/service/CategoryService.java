package itstep.social_freedom.service;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> allCategory() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList());
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(new Category());
    }

    public List<Category> findAllCategoryById(Long id) {
        return categoryRepository.findAllById(id);
    }

    public boolean save(Category category) {
        Category categoryDB = categoryRepository.findCategoriesByName(category.getName());
        if (categoryDB != null)
            if (!Objects.equals(categoryDB.getStatus(), category.getStatus()))
                return false;
        categoryRepository.save(category);
        return true;
    }

    public void delete(Long id) {
        if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findById(id).orElse(new Category());
            category.setStatus(Status.DELETED);
            categoryRepository.save(category);
        }
    }
}
