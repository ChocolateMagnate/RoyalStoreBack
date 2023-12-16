package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.ElectronicProduct;
import com.royal.models.products.enumerations.ProductStorage;
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
    public ArrayList<ElectronicProduct> getCart(@RequestParam("email") String email) throws HttpException {
        return userService.getProducts(email, ProductStorage.Cart);
    }

    @GetMapping("/get-liked")
    public ArrayList<ElectronicProduct> getLiked(@RequestParam("email") String email) throws HttpException {
        return userService.getProducts(email, ProductStorage.Liked);
    }

    @GetMapping("/get-purchased")
    public ArrayList<ElectronicProduct> getPurchased(@RequestParam("email") String email) throws HttpException {
        return userService.getProducts(email, ProductStorage.Purchased);
    }

    @PutMapping("/add-product-to-cart")
    public void addToCart(@RequestParam("email") String email, @RequestParam("id") String productId) throws HttpException {
        userService.insertProduct(email, productId, ProductStorage.Cart);
    }

    @PutMapping("/add-product-to-liked")
    public void addToLied(@RequestParam("email") String email, @RequestParam("id") String productId) throws HttpException {
        userService.insertProduct(email, productId, ProductStorage.Liked);
    }

    @PutMapping("/purchase")
    public void addToPurchased(@RequestParam("email") String email, @RequestParam("id") String productId) throws HttpException {
        userService.purchase(email, productId);
    }

    @DeleteMapping("/remove-product-from-cart")
    public void removeProductFromCart(@RequestParam("email") String email,
                                      @RequestParam("id") String productId) throws HttpException {
        userService.deleteProduct(email, productId, ProductStorage.Cart);
    }

    @DeleteMapping("/remove-product-from-liked")
    public void removeProductFromLiked(@RequestParam("email") String email,
                                       @RequestParam("id") String productId) throws HttpException {
        userService.deleteProduct(email, productId, ProductStorage.Liked);
    }

    @DeleteMapping("/remove-product-from-purchased")
    public void removeProductFromPurchased(@RequestParam("email") String email,
                                           @RequestParam("id") String productId) throws HttpException {
        userService.deleteProduct(email, productId, ProductStorage.Purchased);
    }
}
