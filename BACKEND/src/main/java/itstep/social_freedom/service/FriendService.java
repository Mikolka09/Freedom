package itstep.social_freedom.service;

import itstep.social_freedom.entity.Friend;
import itstep.social_freedom.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public List<Friend> allFriend(){
        return friendRepository.findAll();
    }

    public List<Friend> allFriendsByUser(Long id){
        return friendRepository.findAllUserFriendsById(id);
    }

    public void saveFriend(Friend friend){
        friendRepository.save(friend);
    }
}
