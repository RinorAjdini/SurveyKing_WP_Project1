package finki.ukim.mk.surveyKing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import finki.ukim.mk.surveyKing.model.Role;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.model.exceptions.InvalidArgumentsException;
import finki.ukim.mk.surveyKing.model.exceptions.PasswordsDoNotMatchException;
import finki.ukim.mk.surveyKing.model.exceptions.UsernameAlreadyExistsException;
import finki.ukim.mk.surveyKing.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User save(User user) {
        if (!user.getPassword().startsWith("$2a$")) { // Основна проверка за енкрипција
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User register(String username, String password, String confirmPassword, Role role, String name, String surname)
            throws InvalidArgumentsException, PasswordsDoNotMatchException, UsernameAlreadyExistsException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Invalid registration attempt: Missing username or password");
            throw new InvalidArgumentsException();
        }

        if (!password.equals(confirmPassword)) {
            logger.warn("Passwords do not match for user: {}", username);
            throw new PasswordsDoNotMatchException();
        }

        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("Attempted to register with existing username: {}", username);
            throw new UsernameAlreadyExistsException();
        }

        User user = new User(username, passwordEncoder.encode(password), role);
        user.setName(name);
        user.setSurname(surname);
        return userRepository.save(user);
    }

    public void updateProfile(User user, String name, String surname) {
        user.setName(name);
        user.setSurname(surname);
        userRepository.save(user);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }
        return Optional.empty();
    }
}
