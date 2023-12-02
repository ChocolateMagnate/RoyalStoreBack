package com.royal.controllers;

import com.royal.errors.http.IllegalUserCredentialsException;
import com.royal.errors.http.IncorrectUserPasswordException;
import com.royal.errors.http.UserAlreadyExistsException;
import com.royal.errors.http.UserDoesNotExistException;
import com.royal.models.products.ElectronicProduct;
import com.royal.models.users.AuthenticatedUserDetails;
import com.royal.models.users.LoginUserCredentials;
import com.royal.models.users.PublicUserDetails;
import com.royal.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@Log4j2
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PublicUserDetails> register(@RequestBody AuthenticatedUserDetails user) throws UserAlreadyExistsException, IllegalUserCredentialsException {
        try {
            PublicUserDetails details = userService.registerNewUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(details);
        } catch (Exception e) {
            log.error("Error from /register: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public PublicUserDetails login(@RequestBody LoginUserCredentials credentials) throws IncorrectUserPasswordException, UserDoesNotExistException {
        return userService.loginExistingUser(credentials);
    }

    @GetMapping("/get-cart/{id}")
    public ArrayList<ElectronicProduct> getAllCartItems(@PathVariable String userId, HttpServletResponse response) throws IOException {
        return userService.getAllElementsInCart(userId, response);
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
