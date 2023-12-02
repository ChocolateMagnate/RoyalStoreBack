package com.royal.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.auth.JwtService;
import com.royal.errors.http.IllegalUserCredentialsException;
import com.royal.errors.http.IncorrectUserPasswordException;
import com.royal.errors.http.UserAlreadyExistsException;
import com.royal.errors.http.UserDoesNotExistException;
import com.royal.models.CartPair;
import com.royal.models.users.AuthenticatedUserDetails;
import com.royal.models.users.LoginUserCredentials;
import com.royal.models.users.PublicUserDetails;
import com.royal.models.users.User;
import com.royal.models.products.ElectronicProduct;
import com.royal.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.AttributeNotFoundException;
import java.io.IOException;
import java.util.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private static final String emailRegularExpression =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public PublicUserDetails registerNewUser(AuthenticatedUserDetails user) throws UserAlreadyExistsException, IllegalUserCredentialsException, Exception {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException(user.getEmail());
        if (!isValidEmail(user.getEmail()))
            throw new IllegalUserCredentialsException(user.getEmail());

        User newResisteredUser = new User();
        newResisteredUser.setEmail(user.getEmail());
        newResisteredUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newResisteredUser.setPhoto(user.getProfilePicture());
        newResisteredUser.setRoles(user.getRoles());
        userRepository.save(newResisteredUser);

        PublicUserDetails details = newResisteredUser.getPublicDetails();
        int durationInMinutes = user.isRememberMe() ? 2 * 64 : 30;
        Date expiration = Date.from(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES));
        SignedJWT jwt = jwtService.generateJwtToken(user.getEmail(), expiration, user.isRememberMe());
        details.setToken(jwt.serialize());
        return details;
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public PublicUserDetails loginExistingUser(LoginUserCredentials credentials) throws IncorrectUserPasswordException, UserDoesNotExistException {
        User userInDatabase = userRepository.findByEmail(credentials.getEmail())
                .orElseThrow(() -> new UserDoesNotExistException(credentials.getEmail()));
        if (!passwordEncoder.matches(credentials.getPassword(), userInDatabase.getPassword()))
            throw new IncorrectUserPasswordException(credentials.getPassword());
        return userInDatabase.getPublicDetails();
    }

    public ArrayList<ElectronicProduct> getAllElementsInCart(String userId, HttpServletResponse response) throws IOException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "The requested user by ID " + userId + " does not exist.");
            return new ArrayList<>();
        }

        try {
            User userInDatabase = user.get();
            return userInDatabase.getCart().getAllElectronicProducts();
        } catch (AttributeNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return new ArrayList<>();
        }
    }

    public int addProductToCart(String userId, String productId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return HttpStatus.NOT_FOUND.value();
        User userFromDatabase = user.get();
        if (userFromDatabase.getCart().contains(productId)) return HttpStatus.FOUND.value();
        userFromDatabase.getCart().add(new CartPair(productId, 0));
        userRepository.save(userFromDatabase);
        return HttpStatus.OK.value();
    }

    public int incrementCartCounter(String userId, String productId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return HttpStatus.NOT_FOUND.value();
        User userFromDatabase = user.get();
        if (!userFromDatabase.getCart().contains(productId)) return HttpStatus.NOT_FOUND.value();
        try {
            int index = userFromDatabase.getCart().whereProductIdEquals(productId);
            CartPair updatedCartPair = userFromDatabase.getCart().get(index);
            updatedCartPair.setCount(updatedCartPair.getCount() + 1);
            userFromDatabase.getCart().set(index, updatedCartPair);
            userRepository.save(userFromDatabase);
            return HttpStatus.OK.value();
        } catch (AttributeNotFoundException e) {
            return HttpStatus.NOT_FOUND.value();
        }
    }
}
