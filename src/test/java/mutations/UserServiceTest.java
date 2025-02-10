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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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
        user = new User("testUser", "password", Role.USER);
        user.setId(1L);
    }

    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        User result = userService.findByUsername("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
        User result = userService.findByUsername("unknownUser");
        assertNull(result);
    }


    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.findById(99L);
        assertFalse(result.isPresent());
    }


    @Test
    void testSave_EncodesPassword() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.save(user);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testRegister_InvalidUsernameOrPassword() {
        // Test null username
        assertThrows(InvalidArgumentsException.class, () ->
                userService.register(null, "password", "password", Role.USER, "Test", "User"));

        // Test null password
        assertThrows(InvalidArgumentsException.class, () ->
                userService.register("testUser", null, "password", Role.USER, "Test", "User"));

        // Test empty username
        assertThrows(InvalidArgumentsException.class, () ->
                userService.register("", "password", "password", Role.USER, "Test", "User"));

        // Test empty password
        assertThrows(InvalidArgumentsException.class, () ->
                userService.register("testUser", "", "password", Role.USER, "Test", "User"));
    }

    @Test
    void testSave_DoesNotReEncodeAlreadyEncodedPassword() {
        user.setPassword("$2a$10$encodedPassword"); // Already encoded password

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.save(user);
        assertEquals("$2a$10$encodedPassword", user.getPassword()); // Should not be re-encoded
    }


    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class, () ->
                userService.register("testUser", "password", "password", Role.USER, "Test", "User"));
    }

    @Test
    void testRegister_PasswordsDoNotMatch() {
        assertThrows(PasswordsDoNotMatchException.class, () ->
                userService.register("testUser", "password", "wrongPassword", Role.USER, "Test", "User"));
    }

    @Test
    void testRegister_InvalidArguments() {
        assertThrows(InvalidArgumentsException.class, () ->
                userService.register("", "password", "password", Role.USER, "Test", "User"));
    }


    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        assertNotNull(userService.loadUserByUsername("testUser"));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("unknownUser"));
    }

//    @Test
//    void testUpdateProfile() {
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        userService.updateProfile(user, "NewName", "NewSurname");
//        assertEquals("NewName", user.getName());
//        assertEquals("NewSurname", user.getSurname());
//    }

    @Test
    void testUpdatePassword() {
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        userService.updatePassword(user, "newPassword");
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void testUpdatePassword_DoesNotReEncodeAlreadyEncodedPassword() {

        User user = new User();
        user.setId(1L);
        user.setPassword("$2a$10$existingEncodedPassword");  // Pre-existing encoded password


        lenient().when(passwordEncoder.encode("newPassword")).thenReturn("$2a$10$newEncodedPassword");


        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        userService.updatePassword(user, "newPassword");


        assertEquals("$2a$10$newEncodedPassword", user.getPassword(), "Password should be re-encoded");


        verify(userRepository).save(user);
    }




    @Test
    void testFindByUsernameAndPassword_Success() {
        when(userRepository.findByUsernameAndPassword("testUser", "password"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsernameAndPassword("testUser", "password");
        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void testFindByUsernameAndPassword_NotFound() {
        when(userRepository.findByUsernameAndPassword("unknownUser", "password"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsernameAndPassword("unknownUser", "password");
        assertFalse(result.isPresent());
    }
    @Test
    void testSave_ReturnsSavedUser() {
        User user = new User("testUser", "password", Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);
        assertNotNull(savedUser);
        assertEquals(user, savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegister_Success() throws InvalidArgumentsException, PasswordsDoNotMatchException, UsernameAlreadyExistsException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register("testUser", "password", "password", Role.USER, "Test", "User");
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("Test", result.getName()); // Verify name is set
        assertEquals("User", result.getSurname()); // Verify surname is set
    }
    @Test
    void testUpdateProfile() {
        User user = new User("testUser", "password", Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateProfile(user, "NewName", "NewSurname");
        assertEquals("NewName", user.getName()); // Verify name is updated
        assertEquals("NewSurname", user.getSurname()); // Verify surname is updated
        verify(userRepository, times(1)).save(user);
    }
}



//import finki.ukim.mk.surveyKing.model.Role;
//import finki.ukim.mk.surveyKing.model.User;
//import finki.ukim.mk.surveyKing.model.exceptions.InvalidArgumentsException;
//import finki.ukim.mk.surveyKing.model.exceptions.PasswordsDoNotMatchException;
//import finki.ukim.mk.surveyKing.model.exceptions.UsernameAlreadyExistsException;
//import finki.ukim.mk.surveyKing.repository.UserRepository;
//import finki.ukim.mk.surveyKing.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//    private User mockUser;
//
//    @BeforeEach
//    void setUp() {
//        mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testUser");
//        mockUser.setPassword("pass123");
//        mockUser.setRole(Role.USER);
//    }
//
//    @Test
//    void testFindByUsername_WhenUserExists() {
//        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        User user = userService.findByUsername("testUser");
//        assertNotNull(user);
//        assertEquals("testUser", user.getUsername());
//    }
//
//    @Test
//    void testFindByUsername_WhenUserDoesNotExist() {
//        when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());
//        User user = userService.findByUsername("noUser");
//        assertNull(user);
//    }
//
//    @Test
//    void testSaveUser_WhenPasswordIsRaw() {
//        User newUser = new User();
//        newUser.setUsername("newUser");
//        newUser.setPassword("rawPass");
//        when(passwordEncoder.encode("rawPass")).thenReturn("proPass");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        User savedUser = userService.save(newUser);
//        assertNotNull(savedUser);
//        assertEquals("proPass", savedUser.getPassword());
//    }
//
//    @Test
//    void testSaveUser_WhenPasswordIsAlreadyEncoded() {
//        User newUser = new User();
//        newUser.setUsername("newUser");
//        newUser.setPassword("$2a$10$encodedPassword");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        User savedUser = userService.save(newUser);
//        assertNotNull(savedUser);
//        assertEquals("$2a$10$encodedPassword", savedUser.getPassword());
//    }
//
//    @Test
//    void testFindById_WhenUserExists() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
//        Optional<User> user = userService.findById(1L);
//        assertTrue(user.isPresent());
//        assertEquals("testUser", user.get().getUsername());
//    }
//
//    @Test
//    void testFindByIdWhenUserDoesNotExist() {
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//        Optional<User> user = userService.findById(99L);
//        assertFalse(user.isPresent());
//    }
//
//    @Test
//    void testRegisterSuccess() throws PasswordsDoNotMatchException, UsernameAlreadyExistsException, InvalidArgumentsException {
//        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
//        when(passwordEncoder.encode("securePassword")).thenReturn("proPass");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        User registeredUser = userService.register("newUser", "securePassword", "securePassword", Role.USER, "Marija", "Vrzhovska");
//        assertNotNull(registeredUser);
//        assertEquals("newUser", registeredUser.getUsername());
//        assertEquals("proPass", registeredUser.getPassword());
//    }
//
//    @Test
//    void testRegisterFailsWhenPasswordsDoNotMatch() {
//        assertThrows(PasswordsDoNotMatchException.class, () ->
//                userService.register("newUser", "pass1", "pass2", Role.USER, "Marija", "Vrzhovska"));
//    }
//
//    @Test
//    void testRegisterFailsWhenUsernameExists() {
//        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(mockUser));
//        assertThrows(UsernameAlreadyExistsException.class, () ->
//                userService.register("existingUser", "password", "password", Role.USER, "Marija", "Vrzhovska"));
//    }
//
//    @Test
//    void testRegisterFailsWhenInvalidArguments() {
//        assertThrows(InvalidArgumentsException.class, () ->
//                userService.register("", "password", "password", Role.USER, "Marija", "Vrzhovska"));
//        assertThrows(InvalidArgumentsException.class, () ->
//                userService.register("user", "", "", Role.USER, "Marija", "Vrzhovska"));
//    }
//
//    @Test
//    void testUpdateProfile() {
//        userService.updateProfile(mockUser, "NewName", "NewSurname");
//        assertEquals("NewName", mockUser.getName());
//        assertEquals("NewSurname", mockUser.getSurname());
//        verify(userRepository, times(1)).save(mockUser);
//    }
//
//    @Test
//    void testUpdatePassword() {
//        when(passwordEncoder.encode("newPassword")).thenReturn("newProPass");
//        userService.updatePassword(mockUser, "newPassword");
//        assertEquals("newProPass", mockUser.getPassword());
//        verify(userRepository, times(1)).save(mockUser);
//    }
//
//    @Test
//    void testLoadUserByUsername_WhenUserExists() {
//        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        assertDoesNotThrow(() -> userService.loadUserByUsername("testUser"));
//    }
//
//    @Test
//    void testLoadUserByUsername_WhenUserNotFound() {
//        when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());
//        assertThrows(UsernameNotFoundException.class, () ->
//                userService.loadUserByUsername("noUser"));
//    }
//
//    @Test
//    void testFindByUsernameAndPassword() {
//        when(userRepository.findByUsernameAndPassword("testUser", "proPass"))
//                .thenReturn(Optional.of(mockUser));
//        Optional<User> user = userService.findByUsernameAndPassword("testUser", "proPass");
//        assertTrue(user.isPresent());
//        assertEquals("testUser", user.get().getUsername());
//    }
//}
