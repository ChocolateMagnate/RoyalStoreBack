package com.royal.services;

import com.google.common.collect.Iterables;
import com.nimbusds.jwt.SignedJWT;
import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.models.products.ElectronicProduct;
import com.royal.models.users.AuthenticatedUserDetails;
import com.royal.models.users.LoginUserCredentials;
import com.royal.models.users.PublicUserDetails;
import com.royal.models.users.User;
import com.royal.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
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

    public PublicUserDetails registerNewUser(AuthenticatedUserDetails user) throws HttpException {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new HttpException(HttpStatus.FOUND, "Email " + user.getEmail() + " is already taken.");
        if (!isValidEmail(user.getEmail()))
            throw new HttpException(HttpStatus.BAD_REQUEST, "Email " + user.getEmail() + " is invalid.");
        if (user.getPassword().isBlank())
            throw new HttpException(HttpStatus.BAD_REQUEST, "Blank password.");

        User newResisteredUser = new User();
        newResisteredUser.setEmail(user.getEmail());
        newResisteredUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newResisteredUser.setPhoto(user.getProfilePicture());
        newResisteredUser.setRoles(user.getRoles());
        userRepository.save(newResisteredUser);

        PublicUserDetails details = newResisteredUser.getPublicDetails();
        SignedJWT jwt = jwtService.generateJwtToken(user.getEmail(), user.isRememberMe());
        details.setToken(jwt.serialize());
        return details;
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public PublicUserDetails loginExistingUser(LoginUserCredentials credentials) throws HttpException {
        User userInDatabase = userRepository.findByEmail(credentials.getEmail())
                .orElseThrow(() -> new HttpException(HttpStatus.FOUND, "User by email " + credentials.getEmail() + " already exists."));
        if (!passwordEncoder.matches(credentials.getPassword(), userInDatabase.getPassword()))
            throw new HttpException(HttpStatus.BAD_REQUEST, "Incorrect password for " + userInDatabase.getEmail());
        PublicUserDetails details = userInDatabase.getPublicDetails();
        SignedJWT jwt = jwtService.generateJwtToken(credentials.getEmail(), credentials.isRememberMe());
        details.setToken(jwt.serialize());
        return details;
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User by email " + email + " does not exist."));
    }

    public ArrayList<ElectronicProduct> getAllElementsInCart(String subject) throws HttpException {
        User user = getUserOrThrow(subject);
        return user.getCart();
    }

    public void addProductToCart(String email, @NotNull ElectronicProduct product) throws HttpException {
        User user = getUserOrThrow(email);
        ArrayList<ElectronicProduct> cart = user.getCart();

        if (cart.contains(product)) {
            int index = cart.indexOf(product);
            ElectronicProduct target = cart.get(index);
            target.setItemsInStock(target.getItemsInStock() + 1);
        } else cart.add(product);
        userRepository.save(user);
    }

    public void deleteProductFromCart(String email, String productId) throws HttpException {
        User user = getUserOrThrow(email);
        ArrayList<ElectronicProduct> cart = user.getCart();
        int index = Iterables.indexOf(cart, product -> Objects.equals(product.getId(), productId));
        if (index == -1) throw new HttpException(HttpStatus.NOT_FOUND,
                "No product by id " + productId + " in cart of " + email);
        cart.remove(index);
        userRepository.save(user);
    }

    private User getUserOrThrow(String subject) throws HttpException {
        Optional<User> user = userRepository.findByEmail(subject);
        if (user.isPresent()) return user.get();
        throw new HttpException(HttpStatus.NOT_FOUND, "User by email " + subject + " is not found.");
    }

}