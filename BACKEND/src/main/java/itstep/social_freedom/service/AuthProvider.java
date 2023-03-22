package itstep.social_freedom.service;

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
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.Date;

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = (User) userService.loadUserByUsername(username);

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
                throw new BadCredentialsException("Your account is temporarily blocked!");
            } else {
                Period period = Period.between(dateNow, user.getLockDate());
                if (period.getDays() < 2) {
                    throw new BadCredentialsException("Your account is temporarily blocked!");
                } else {
                    user.setOffenses(0);
                    user.setLockDate(null);
                }
            }
        }
    }
}
