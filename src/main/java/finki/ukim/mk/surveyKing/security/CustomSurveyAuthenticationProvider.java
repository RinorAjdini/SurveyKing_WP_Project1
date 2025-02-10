package finki.ukim.mk.surveyKing.security;

import finki.ukim.mk.surveyKing.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomSurveyAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public CustomSurveyAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String rawPassword = authentication.getCredentials().toString();

    // Load user by username
    UserDetails user = this.userService.loadUserByUsername(username);
    System.out.println("Loaded user: " + user.getUsername());
    System.out.println("Raw password: " + rawPassword);
    System.out.println("Encoded password from DB: " + user.getPassword());

    // Verify password
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        System.out.println("Password mismatch!");
        throw new BadCredentialsException("Invalid credentials");
    }

    return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
}

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
