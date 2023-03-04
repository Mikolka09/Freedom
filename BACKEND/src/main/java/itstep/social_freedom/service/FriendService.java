package itstep.social_freedom.service;

import itstep.social_freedom.entity.Alert;
import itstep.social_freedom.entity.Friend;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public List<Friend> allFriend() {
        return friendRepository.findAll();
    }

    public List<Friend> allFriendsByUser(Long id) {
        return friendRepository.findAllUserFriendsById(id);
    }

    public void saveFriend(Friend friend) {
        Friend friendBD = friendRepository.findFriendById(friend.getId());
        if (friendBD != null)
            return;
        friendRepository.save(friend);
    }

    public void deleteFriend(Long id, Long idFriend){
        List<Friend> friends = friendRepository.findAll().stream()
                .filter(x-> Objects.equals(x.getFriendRequester().getId(), id) && Objects.equals(x.getFriendReceiver().getId(), idFriend) ||
                        Objects.equals(x.getFriendRequester().getId(), idFriend) && Objects.equals(x.getFriendReceiver().getId(), id))
                .collect(Collectors.toList());
        friendRepository.deleteAll(friends);
    }
}
