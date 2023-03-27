package itstep.social_freedom.service;

import itstep.social_freedom.controller.UserController;
import itstep.social_freedom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Objects;

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private MessageService messageService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = (User) userService.loadUserByUsername(username.toUpperCase());

        if (user != null && (user.getUsername().equalsIgnoreCase(username) || user.getFullName().equalsIgnoreCase(username))) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Wrong password!");
            }
            checkOnOffenses(user);
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else {
            throw new BadCredentialsException("Username not found");
        }
    }

    public boolean supports(Class<?> arg) {
        return true;
    }

    public void checkOnOffenses(User user) {
        if (user.getOffenses() >= 1000) {
            LocalDate dateNow = LocalDate.now();
            if (user.getLockDate() == null) {
                user.setLockDate(dateNow);
                userService.saveEdit(user);
                throw new BadCredentialsException("Your account is temporarily blocked!");
            } else {
                Period period = Period.between(user.getLockDate(), dateNow);
                if (period.getDays() < 2) {
                    throw new BadCredentialsException("Your account is temporarily blocked!");
                } else {
                    user.setOffenses(0);
                    user.setLockDate(null);
                    userService.saveEdit(user);
                    String text = "Your account has been banned for a couple of days. Please be more careful in the " +
                            "future and do not violate the rules of censorship! Sincerely, Administration.";
                    User userFrom = userService.allUsers().stream()
                            .filter(x-> Objects.equals(x.getUsername(), "ADMIN")).findFirst().orElse(new User());
                    UserController.createSendMessage(userFrom, user, text, userService, inviteService, messageService);
                }
            }
        }
    }
}
