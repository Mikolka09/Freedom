package itstep.social_freedom.repository;

import itstep.social_freedom.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllUserFriendsById(Long id);

    Friend findFriendById(Long id);
}
