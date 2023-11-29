package com.royal.controllers;

import com.royal.errors.http.IllegalUserCredentialsException;
import com.royal.errors.http.IncorrectUserPasswordException;
import com.royal.errors.http.UserAlreadyExistsException;
import com.royal.errors.http.UserDoesNotExistException;
import com.royal.models.products.ElectronicProduct;
import com.royal.models.users.PublicUserDetails;
import com.royal.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/register")
    public ResponseEntity<PublicUserDetails> register(@RequestParam("email") String email,
                                                      @RequestParam("password") String password,
                                                      @RequestParam("remember-me") boolean rememberMe) throws UserAlreadyExistsException, IllegalUserCredentialsException {
        try {
            PublicUserDetails details = userService.registerNewUser(email, password, rememberMe);
            return ResponseEntity.status(HttpStatus.OK).body(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/login")
    public PublicUserDetails login(@RequestParam("email") String email,
                                   @RequestParam("password") String password) throws IncorrectUserPasswordException, UserDoesNotExistException {
        return userService.loginExistingUser(email, password);
    }

    @GetMapping("/get-cart/{id}")
    public ArrayList<ElectronicProduct> getAllCartItems(@PathVariable String userId, HttpServletResponse response) {
        try {
            return userService.getAllElementsInCart(userId, response);
        } catch (IOException e) {
            // This error means we couldn't have sent the response error message
            // correctly, so we can try to send an empty array list instead.
            return new ArrayList<>();
        }
    }

    @PutMapping("post-into-cart")
    public ResponseEntity<Void> putProductIntoCart(@RequestParam("userId") String userId,
                                                   @RequestParam("productId") String productId) {
        int code = userService.addProductToCart(userId, productId);
        return ResponseEntity.status(code).build();
    }

    @PostMapping("/increment-cart-counter")
    public ResponseEntity<Void> incrementCartCounter(@RequestParam("userId") String userId,
                                                     @RequestParam("productId") String productId) {
        int code = userService.incrementCartCounter(userId, productId);
        return ResponseEntity.status(code).build();
    }
}
