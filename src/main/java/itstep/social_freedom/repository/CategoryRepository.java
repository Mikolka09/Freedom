package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoriesByName(String name);

    List<Category> findAllById(Long id);
}
