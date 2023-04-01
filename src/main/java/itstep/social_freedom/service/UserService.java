package itstep.social_freedom.service;

import itstep.social_freedom.controller.FriendController;
import itstep.social_freedom.entity.Post;
import itstep.social_freedom.entity.Role;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.User;
import itstep.social_freedom.repository.RoleRepository;
import itstep.social_freedom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null || user.getStatus() == Status.DELETED) {
            throw new UsernameNotFoundException("User not found!");
        }

        return user;
    }

    public int countRating(User user) {
        List<Post> postsList = user.getPosts().stream()
                .filter(x -> x.getStatus() == Status.VERIFIED).collect(Collectors.toList());
        int posts = postsList.size();
        int likes = 0;
        for (Post post : postsList) {
            likes += post.getLikes();
        }
        int comments = (int) user.getComments().stream().filter(x -> x.getStatus() == Status.ACTIVE).count();
        int friends = FriendController.giveListFriends(user).size();
        int offenses = user.getOffenses();
        int summa = posts + comments + likes + friends;
        double users = allUsers().size();
        if (summa == 0 || (int) users == 0) {
            return 0;
        } else {
            return Math.max(((int) ((summa / users) * 100) - offenses), 0);
        }
    }

    public void saveRatingUser() {
        List<User> users = allUsers();
        for (User user : users) {
            int count = countRating(user);
            if(count!=0) {
                user.setRating(count);
                saveEdit(user);
            }
        }
    }

    public User getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (Objects.equals(auth.getName(), "anonymousUser")) {
            return userRepository.findUserByEmail(user.getEmail());
        } else if (Objects.equals(auth.getName(), auth.getPrincipal().toString()) &&
                !Objects.equals(auth.getName(), "anonymousUser")) {
            return userRepository.findByGoogleName(auth.getName());
        } else {
            user = (User) auth.getPrincipal();
            return userRepository.findUserByEmail(user.getEmail());
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public boolean findUserByUsername(User user, String username) {
        User userBD = userRepository.findByUsername(username);
        if (userBD != null) {
            return Objects.equals(userBD.getId(), user.getId());
        }
        return false;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }
        user.setUsername(user.getUsername().toUpperCase());
        user.setAvatarUrl("/img/user.png");
        if (user.getRoles()==null) {
            Role role = roleRepository.findByName("ROLE_USER");
            user.setRoles(Collections.singleton(role));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.ACTIVE);
        if (!user.isEmailConfirmed())
            user.setEmailConfirmed(false);
        userRepository.save(user);
        return true;
    }

    public boolean saveNewPassword(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean checkPassword(Long id, String pass) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String oldPass = userRepository.findById(id).orElse(new User()).getPassword();
        return encoder.matches(pass, oldPass);
    }

    public boolean save(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB == null) {
            return saveNewUser(user);
        } else if (Objects.equals(userFromDB.getId(), user.getId())) {
            userFromDB.setEmail(user.getEmail());
            if (user.getAvatarUrl() != null)
                userFromDB.setAvatarUrl(user.getAvatarUrl());
            userFromDB.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userFromDB);
            return true;
        } else {
            return false;
        }
    }

    private boolean saveNewUser(User user) {
        User userFromDB;
        userFromDB = userRepository.findById(user.getId()).orElse(new User());
        userFromDB.setUsername(user.getUsername());
        userFromDB.setEmail(user.getEmail());
        if (user.getAvatarUrl() != null)
            userFromDB.setAvatarUrl(user.getAvatarUrl());
        userFromDB.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userFromDB);
        return true;
    }

    public boolean saveEdit(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB == null) {
            return saveNewUser(user);
        } else if (Objects.equals(userFromDB.getId(), user.getId())) {
            userFromDB.setEmail(user.getEmail());
            userFromDB.setLockDate(user.getLockDate());
            userFromDB.setOffenses(user.getOffenses());
            if (user.getAvatarUrl() != null)
                userFromDB.setAvatarUrl(user.getAvatarUrl());
            userRepository.save(userFromDB);
            return true;
        } else {
            return false;
        }
    }

    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).orElse(new User());
            user.setStatus(Status.DELETED);
            userRepository.save(user);
        }
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }


}
