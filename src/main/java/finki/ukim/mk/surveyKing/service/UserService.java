package finki.ukim.mk.surveyKing.service;

import finki.ukim.mk.surveyKing.model.Role;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.model.exceptions.InvalidArgumentsException;
import finki.ukim.mk.surveyKing.model.exceptions.PasswordsDoNotMatchException;
import finki.ukim.mk.surveyKing.model.exceptions.UsernameAlreadyExistsException;
import finki.ukim.mk.surveyKing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User save(User user) {
        if (!user.getPassword().startsWith("$2a$10$")) { // Check if the password is already encoded
            String rawPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User register(String username, String password, String confirmPassword, Role role,String name,String surname)
            throws InvalidArgumentsException, PasswordsDoNotMatchException, UsernameAlreadyExistsException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if (userRepository.findByUsername(username).isPresent()) {
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        System.out.println("Loaded user: " + user.getUsername());
        return user;
    }
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return this.userRepository.findByUsernameAndPassword(username, password);
    }

}
