package com.royal.users.service;

import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.products.service.ElectronicProductService;
import com.royal.users.domain.details.AuthenticatedUserDetails;
import com.royal.users.domain.details.LoginUserCredentials;
import com.royal.users.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

// Test instance per class lifecycle allows us to make setup and teardown methods non-static.
// See: https://stackoverflow.com/a/63258521
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElectronicProductService electronicProductService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private UserService userService;

    @BeforeAll
    public void setup() {
        this.userService = new UserService(this.userRepository, this.electronicProductService, this.passwordEncoder, this.jwtService);
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
            assertDoesNotThrow(() -> userService.registerNewUser(user));
            assertTrue(userRepository.existsByEmail(email));
        }
    }

    @Test
    public void failRegistrationWithInvalidEmail() {
        for (int attempt = 0; attempt < 100; ++attempt) {
            var invalidEmailUser = getMockedInvalidUser();
            assertThrows(HttpException.class, () -> userService.registerNewUser(invalidEmailUser));
        }
    }

    @Test
    public void failRegistrationWithBlankPassword() {
        var blankPasswordUser = new AuthenticatedUserDetails();
        blankPasswordUser.setEmail("valid@email.com");
        blankPasswordUser.setPassword("");
        assertThrows(HttpException.class, () -> userService.registerNewUser(blankPasswordUser));
    }

    @Test
    public void failRegistrationUserAlreadyExists() {
        var originalUser = new AuthenticatedUserDetails();
        originalUser.setEmail("test3@example.com");
        originalUser.setPassword("strong password");

        var duplicateUser = new AuthenticatedUserDetails();
        duplicateUser.setEmail("test3@example.com");
        duplicateUser.setPassword("another strong password");

        assertDoesNotThrow(() -> userService.registerNewUser(originalUser));
        assertThrows(HttpException.class, () -> userService.registerNewUser(duplicateUser));
    }

    @Test
    public void acceptRegistrationWithUniqueEmailAndFilledPassword() {
        for (int attempt = 0; attempt < 100; ++attempt) {
            var validUser = getMockedValidUser();
            assertDoesNotThrow(() -> userService.registerNewUser(validUser));
        }
    }

    @Test
    public void failLoginNonexistentUser() {
        var nonexistentUser = new LoginUserCredentials();
        nonexistentUser.setEmail("unknown@hidden.com");
        nonexistentUser.setPassword("idk");

        assertThrows(HttpException.class, () -> userService.loginExistingUser(nonexistentUser));
    }

    @Test
    public void failLoginIfPasswordDoesNotMatch() {
        var registeringUser = new AuthenticatedUserDetails();
        registeringUser.setEmail("test@example.com");
        registeringUser.setPassword("strong password");
        assertDoesNotThrow(() -> userService.registerNewUser(registeringUser));

        var loggingUser = new LoginUserCredentials();
        loggingUser.setEmail("test@example.com");
        loggingUser.setPassword("weak password");
        assertThrows(HttpException.class, () -> userService.loginExistingUser(loggingUser));
    }

    @Test
    public void acceptLoginIfUserExistsAndPasswordMatches() {
        for (int attempt = 0; attempt < 100; ++attempt) {
            var validUser = getMockedValidUser();
            String email = validUser.getEmail();
            String password = validUser.getPassword();
            assertDoesNotThrow(() -> userService.registerNewUser(validUser));

            var validLoginCredentials = new LoginUserCredentials();
            validLoginCredentials.setEmail(email);
            validLoginCredentials.setPassword(password);
            assertDoesNotThrow(() -> userService.loginExistingUser(validLoginCredentials));
        }
    }

    private @NotNull AuthenticatedUserDetails getMockedInvalidUser() {
        var invalidUser = new AuthenticatedUserDetails();
        invalidUser.setEmail(RandomStringUtils.random(20, "qwertyuiopashknman1921"));
        invalidUser.setPassword(RandomStringUtils.randomAlphabetic(32));
        return invalidUser;
    }

    private @NotNull AuthenticatedUserDetails getMockedValidUser() {
        var validUser = new AuthenticatedUserDetails();
        String username = RandomStringUtils.randomAlphabetic(12);
        String domain = RandomStringUtils.randomAlphabetic(13);
        validUser.setEmail(username + "@" + domain + ".com");
        validUser.setPassword(RandomStringUtils.random(45));
        return validUser;
    }

    private @NotNull LoginUserCredentials getMockedInvalidCredentials() {
        var invalidUser = new LoginUserCredentials();
        invalidUser.setEmail(RandomStringUtils.random(20, "qwertyuiopashknman1921"));
        invalidUser.setPassword(RandomStringUtils.randomAlphabetic(32));
        return invalidUser;
    }

    private @NotNull LoginUserCredentials getMockedValidCredentials() {
        var validUser = new LoginUserCredentials();
        String username = RandomStringUtils.randomAlphabetic(12);
        String domain = RandomStringUtils.randomAlphabetic(13);
        validUser.setEmail(username + "@" + domain + ".com");
        validUser.setPassword(RandomStringUtils.random(45));
        return validUser;
    }
}
