package itstep.social_freedom.repository;


import itstep.social_freedom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserByEmail(String email);

    User findByFullName(String name);

    User findByGoogleUsername(String googleUsername);

    User findByGoogleName(String googleName);
}
