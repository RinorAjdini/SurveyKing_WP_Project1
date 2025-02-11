package mutations;

import finki.ukim.mk.surveyKing.model.Role;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.model.exceptions.InvalidArgumentsException;
import finki.ukim.mk.surveyKing.model.exceptions.PasswordsDoNotMatchException;
import finki.ukim.mk.surveyKing.model.exceptions.UsernameAlreadyExistsException;
import finki.ukim.mk.surveyKing.repository.UserRepository;
import finki.ukim.mk.surveyKing.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "encodedPassword", Role.USER);
    }

    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        assertEquals(user, userService.findByUsername("testUser"));
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")) .thenReturn(Optional.empty());
        assertNull(userService.findByUsername("unknown"));
    }

    @Test
    void testSave_NewPasswordEncoded() {
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        user.setPassword("rawPassword");
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userService.save(user);
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user), userService.findById(1L));
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), userService.findById(99L));
    }

    @Test
    void testRegister_Success() {
        try {
            when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User registeredUser = userService.register("newUser", "password", "password", Role.USER, "John", "Doe");

            assertNotNull(registeredUser);
            assertEquals("encodedPassword", registeredUser.getPassword());
        } catch (InvalidArgumentsException | PasswordsDoNotMatchException | UsernameAlreadyExistsException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class, () ->
                userService.register("testUser", "password", "password", Role.USER, "John", "Doe"));
    }

    @Test
    void testRegister_PasswordsDoNotMatch() {
        assertThrows(PasswordsDoNotMatchException.class, () ->
                userService.register("newUser", "password", "wrongPassword", Role.USER, "John", "Doe"));
    }

    @Test
    void testRegister_InvalidArguments() {
        assertThrows(InvalidArgumentsException.class, () -> userService.register("", "password", "password", Role.USER, "John", "Doe"));
    }

    @Test
    void testUpdateProfile() {
        userService.updateProfile(user, "NewName", "NewSurname");
        assertEquals("NewName", user.getName());
        assertEquals("NewSurname", user.getSurname());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdatePassword() {
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        userService.updatePassword(user, "newPassword");
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername("testUser");
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    void testFindByUsernameAndPassword_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
        Optional<User> result = userService.findByUsernameAndPassword("testUser", "password");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUsernameAndPassword_InvalidPassword() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);
        Optional<User> result = userService.findByUsernameAndPassword("testUser", "wrongPassword");
        assertFalse(result.isPresent());
    }
    @Test
    void testRegister_UserHasCorrectNameAndSurname() throws InvalidArgumentsException, PasswordsDoNotMatchException, UsernameAlreadyExistsException {
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.register("newUser", "password", "password", Role.USER, "John", "Doe");

        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getName());
        assertEquals("Doe", registeredUser.getSurname());
    }
    @Test
    void testUpdateProfile_UpdatesNameAndSurname() {
        User user = new User("existingUser", "encodedPassword", Role.USER);
        user.setName("OldName");
        user.setSurname("OldSurname");

        userService.updateProfile(user, "NewName", "NewSurname");

        assertEquals("NewName", user.getName());
        assertEquals("NewSurname", user.getSurname());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testLoadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonExistentUser");
        });
    }

}
