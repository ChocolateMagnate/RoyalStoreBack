package com.royal.users.service;

import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.products.service.ProductService;
import com.royal.users.domain.details.AuthenticatedUserDetails;
import com.royal.users.domain.details.LoginUserCredentials;
import com.royal.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        if (this.userService == null)
           this.userService = new UserService(this.userRepository, this.productService,
                this.passwordEncoder, this.jwtService);
    }

    @AfterEach
    public void teardown() {
        this.userRepository.deleteAll();
    }

    @Test
    public void invalidEmailsAreRejected() {
        String[] invalidEmails = { "hello", "email with@space", "email_without_at.com" };
        for (String invalidEmail : invalidEmails) {
            boolean verdict = userService.isValidEmail(invalidEmail);
            assertFalse(verdict);
        }
    }

    @Test
    public void registrationUniqueUserWorks() {
        var user = new AuthenticatedUserDetails();
        user.setEmail("valid@email.com");
        user.setPassword("dummy password");
        assertDoesNotThrow(() -> {userService.registerNewUser(user);});
        assertTrue(userRepository.existsByEmail("valid@email.com"));
    }

    @Test
    public void failRegistrationWithInvalidEmail() {
        var invalidEmailUser = new AuthenticatedUserDetails();
        invalidEmailUser.setEmail(" this is not allowed \n");
        assertThrows(HttpException.class, () -> {userService.registerNewUser(invalidEmailUser);});
    }

    @Test
    public void failRegistrationWithBlankPassword() {
        var blankPasswordUser = new AuthenticatedUserDetails();
        blankPasswordUser.setEmail("valid@email.com");
        blankPasswordUser.setPassword("");
        assertThrows(HttpException.class, () -> {userService.registerNewUser(blankPasswordUser);});
    }

    @Test
    public void failRegistrationUserAlreadyExists() {
        var originalUser = new AuthenticatedUserDetails();
        originalUser.setEmail("test@example.com");
        originalUser.setPassword("strong password");

        var duplicateUser = new AuthenticatedUserDetails();
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setPassword("another strong password");

        assertDoesNotThrow(() -> {userService.registerNewUser(originalUser);});
        assertThrows(HttpException.class, () -> {userService.registerNewUser(duplicateUser);});
    }

    @Test
    public void acceptRegistrationWithUniqueEmailAndFilledPassword() {
        var validUser = new AuthenticatedUserDetails();
        validUser.setEmail("decent@email.com");
        validUser.setPassword("arbitrary password");
        assertDoesNotThrow(() -> {userService.registerNewUser(validUser);});
    }

    @Test
    public void failLoginNonexistentUser() {
        var nonexistentUser = new LoginUserCredentials();
        nonexistentUser.setEmail("unknown@hidden.com");
        nonexistentUser.setPassword("idk");

        assertThrows(HttpException.class, () -> {userService.loginExistingUser(nonexistentUser);});
    }

    @Test
    public void failLoginIfPasswordDoesNotMatch() {
        var registeringUser = new AuthenticatedUserDetails();
        registeringUser.setEmail("test@example.com");
        registeringUser.setPassword("strong password");
        assertDoesNotThrow(() -> {userService.registerNewUser(registeringUser);});

        var loggingUser = new LoginUserCredentials();
        loggingUser.setEmail("test@example.com");
        loggingUser.setPassword("weak password");
        assertThrows(HttpException.class, () -> {userService.loginExistingUser(loggingUser);});
    }

    @Test
    public void acceptLoginIfUserExistsAndPasswordMatches() {
        acceptRegistrationWithUniqueEmailAndFilledPassword();
        var validLoginCredentials = new LoginUserCredentials();
        validLoginCredentials.setEmail("decent@email.com");
        validLoginCredentials.setPassword("arbitrary password");
        assertDoesNotThrow(() -> {userService.loginExistingUser(validLoginCredentials);});
    }
}
