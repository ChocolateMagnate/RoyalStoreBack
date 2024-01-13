package com.royal.users.service;

import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.products.service.ProductService;
import com.royal.users.domain.details.AuthenticatedUserDetails;
import com.royal.users.domain.details.LoginUserCredentials;
import com.royal.users.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
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
        for (int attempt = 0; attempt < 100; ++attempt) {
            String invalidEmail = RandomStringUtils.random(32, "abeo80711l");
            boolean verdict = userService.isValidEmail(invalidEmail);
            assertFalse(verdict);
        }
    }

    @Test
    public void registrationUniqueUserWorks() {
        for (int attempt = 0; attempt < 100; ++attempt) {
            var user = getMockedValidUser();
            String email = user.getEmail();
            assertDoesNotThrow(() -> {userService.registerNewUser(user);});
            assertTrue(userRepository.existsByEmail(email));
        }
    }

    @Test
    public void failRegistrationWithInvalidEmail() {
        for (int attempt = 0; attempt < 100; ++attempt) {
            var invalidEmailUser = getMockedInvalidUser();
            assertThrows(HttpException.class, () -> {userService.registerNewUser(invalidEmailUser);});
        }
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
        for (int attempt = 0; attempt < 100; ++attempt) {
            var validUser = getMockedValidUser();
            assertDoesNotThrow(() -> {userService.registerNewUser(validUser);});
        }
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
        for (int attempt = 0; attempt < 100; ++attempt) {
            var validUser = getMockedValidUser();
            String email = validUser.getEmail();
            String password = validUser.getPassword();
            assertDoesNotThrow(() -> {userService.registerNewUser(validUser);});

            var validLoginCredentials = new LoginUserCredentials();
            validLoginCredentials.setEmail(email);
            validLoginCredentials.setPassword(password);
            assertDoesNotThrow(() -> {userService.loginExistingUser(validLoginCredentials);});
        }
    }

    private AuthenticatedUserDetails getMockedInvalidUser() {
        var invalidUser = new AuthenticatedUserDetails();
        invalidUser.setEmail(RandomStringUtils.random(20, "qwertyuiopashknman1921"));
        invalidUser.setPassword(RandomStringUtils.randomAlphabetic(32));
        return invalidUser;
    }

    private AuthenticatedUserDetails getMockedValidUser() {
        var validUser = new AuthenticatedUserDetails();
        String username = RandomStringUtils.randomAlphabetic(12);
        String domain = RandomStringUtils.randomAlphabetic(13);
        validUser.setEmail(username + "@" + domain + ".com");
        validUser.setPassword(RandomStringUtils.random(45));
        return validUser;
    }

    private LoginUserCredentials getMockedInvalidCredentials() {
        var invalidUser = new LoginUserCredentials();
        invalidUser.setEmail(RandomStringUtils.random(20, "qwertyuiopashknman1921"));
        invalidUser.setPassword(RandomStringUtils.randomAlphabetic(32));
        return invalidUser;
    }

    private LoginUserCredentials getMockedValidCredentials() {
        var validUser = new LoginUserCredentials();
        String username = RandomStringUtils.randomAlphabetic(12);
        String domain = RandomStringUtils.randomAlphabetic(13);
        validUser.setEmail(username + "@" + domain + ".com");
        validUser.setPassword(RandomStringUtils.random(45));
        return validUser;
    }
}