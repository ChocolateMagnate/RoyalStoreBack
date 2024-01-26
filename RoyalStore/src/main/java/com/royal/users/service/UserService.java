package com.royal.users.service;

import com.google.common.collect.Iterables;
import com.nimbusds.jwt.SignedJWT;
import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.characteristics.specifiers.ProductStorage;
import com.royal.products.service.ElectronicProductService;
import com.royal.users.domain.User;
import com.royal.users.domain.details.AuthenticatedUserDetails;
import com.royal.users.domain.details.LoginUserCredentials;
import com.royal.users.domain.details.PublicUserDetails;
import com.royal.users.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
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

@Log4j2
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ElectronicProductService productService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, ElectronicProductService productService,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private static final String emailRegularExpression =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public PublicUserDetails registerNewUser(@NotNull AuthenticatedUserDetails user) throws HttpException {
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

    public boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegularExpression);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public PublicUserDetails loginExistingUser(@NotNull LoginUserCredentials credentials) throws HttpException {
        User userInDatabase = userRepository.findByEmail(credentials.getEmail())
                .orElseThrow(() -> new HttpException(HttpStatus.FOUND,
                        "User by email " + credentials.getEmail() + " already exists."));
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

    public ArrayList<ElectronicProduct> getProducts(String email, ProductStorage storage) throws HttpException {
        User user = getUserOrThrow404(email);
        ArrayList<String> ids = getIdsFromUser(storage, user);
        return productService.retrieveProductsFromIds(ids);
    }

    public void insertProduct(String email, String productId, ProductStorage storage) throws HttpException {
        User user = getUserOrThrow404(email);
        if (!productService.productExistsById(productId))
            throw new HttpException(HttpStatus.NOT_FOUND, "No product under id " + productId);
        ArrayList<String> ids = getIdsFromUser(storage, user);
        if (!ids.contains(productId)) ids.add(productId);
        userRepository.save(user);
    }

    public void deleteProduct(String email, String deletedProductId, ProductStorage storage) throws HttpException {
        User user = getUserOrThrow404(email);
        ArrayList<String> ids = getIdsFromUser(storage, user);
        int index = Iterables.indexOf(ids, productId -> Objects.equals(productId, deletedProductId));
        if (index == -1) throw new HttpException(HttpStatus.NOT_FOUND,
                "No product by id " + deletedProductId + " is associated with " + email);
        ids.remove(index);
        userRepository.save(user);
    }

    public void purchase(String email, String productId) throws HttpException {
        User user = getUserOrThrow404(email);
        ArrayList<String> purchased = user.getPurchased(), cart = user.getCart();
        if (!purchased.contains(productId)) purchased.add(productId);
        cart.remove(productId);
        user.setPurchased(purchased);
        user.setCart(cart);
        userRepository.save(user);
    }

    private @NotNull User getUserOrThrow404(String email) throws HttpException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) return user.get();
        log.trace("User not found: " + email);
        throw new HttpException(HttpStatus.NOT_FOUND, "User by email " + email + " is not found.");
    }

    @Contract(pure = true)
    private ArrayList<String> getIdsFromUser(@NotNull ProductStorage storage, User user) {
        return switch (storage) {
            case Cart -> user.getCart();
            case Liked -> user.getLiked();
            case Purchased -> user.getPurchased();
        };
    }

}