package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.ElectronicProduct;
import com.royal.models.products.Laptop;
import com.royal.models.products.Smartphone;
import com.royal.models.users.AuthenticatedUserDetails;
import com.royal.models.users.LoginUserCredentials;
import com.royal.models.users.PublicUserDetails;
import com.royal.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Log4j2
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public PublicUserDetails register(@RequestBody AuthenticatedUserDetails user) throws HttpException {
        return userService.registerNewUser(user);
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public PublicUserDetails login(@RequestBody LoginUserCredentials credentials) throws HttpException {
        return userService.loginExistingUser(credentials);
    }

    @GetMapping("/get-cart")
    public ArrayList<ElectronicProduct> getAllCartItems(@RequestParam("email") String email) throws HttpException {
        log.info("Requested email: " + email);
        return userService.getAllElementsInCart(email);
    }

    @PutMapping("/add-laptop-to-cart")
    public void addToCart(@RequestParam("email") String email, @RequestBody Laptop product) throws HttpException {
        userService.addProductToCart(email, product);
    }

    @PutMapping("/add-smartphone-to-cart")
    public void addToCart(@RequestParam("email") String email, @RequestBody Smartphone product) throws HttpException {
        userService.addProductToCart(email, product);
    }

    @DeleteMapping("/remove-from-cart")
    public void removeFromCart(@RequestParam("email") String email,
                               @RequestParam("id") String productId) throws HttpException {
        userService.deleteProductFromCart(email, productId);
    }

}
