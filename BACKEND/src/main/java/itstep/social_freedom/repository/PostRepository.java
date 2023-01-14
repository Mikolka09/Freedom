package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long> {

}
