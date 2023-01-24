package itstep.social_freedom.service;

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

    public User getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        String st = auth.getName();
        Object obj = auth.getPrincipal();
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
        user.setAvatarUrl("avatar/user.png");
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return true;
    }

    public void saveNewPassword(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if(userFromDB != null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    public boolean checkPassword(Long id, String pass) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String oldPass = userRepository.findById(id).orElse(new User()).getPassword();
        return encoder.matches(pass, oldPass);
    }

    public boolean save(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB == null) {
            userFromDB = userRepository.findById(user.getId()).orElse(new User());
            userFromDB.setUsername(user.getUsername());
            userFromDB.setEmail(user.getEmail());
            if (user.getAvatarUrl() != null)
                userFromDB.setAvatarUrl(user.getAvatarUrl());
            userFromDB.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userFromDB);
            return true;
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
