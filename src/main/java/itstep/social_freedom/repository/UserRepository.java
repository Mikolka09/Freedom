package itstep.social_freedom.repository;


import itstep.social_freedom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserByEmail(String email);

    User findByGoogleUsername(String googleUsername);

    User findByGoogleName(String googleName);
}
