package com.royal.products.controller;

import com.royal.errors.HttpException;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.requests.RawElectronicProductRequest;
import com.royal.products.domain.requests.SearchElectronicProductRequest;
import com.royal.products.service.ElectronicProductService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductController {
    private final ElectronicProductService electronicProductService;

    ProductController(@Autowired ElectronicProductService service) {
        this.electronicProductService = service;
    }

    @PostMapping("/get-products")
    public List<ElectronicProduct> getProductsWithParameters(@RequestBody @NotNull SearchElectronicProductRequest filter) {
        return this.electronicProductService.getProductsByParameters(filter.getSearchQuery());
    }

    @GetMapping("/get-random-products")
    public List<ElectronicProduct> getRandomProducts() {
        return this.electronicProductService.getRandomStock();
    }

    @PostMapping("/get-products-by-search")
    public List<ElectronicProduct> getProductsBySearch(@RequestBody String search) {
        return this.electronicProductService.getProductsByDescription(search);
    }

    @PostMapping(value = "/create-product", consumes = "multipart/form-data")
    public String createProduct(@ModelAttribute RawElectronicProductRequest rawRequest,
                                @RequestPart("photo") MultipartFile photo) throws HttpException {
        rawRequest.setPhoto(photo);
        return this.electronicProductService.createNewProduct(rawRequest);
    }

    @PostMapping(value = "/update-product", consumes = "multipart/form-data")
    public void updateProduct(@ModelAttribute RawElectronicProductRequest substitution,
                              @RequestPart("photo")MultipartFile photo) throws HttpException {
        try {
            substitution.setPhoto(photo);
            this.electronicProductService.updateExistingProduct(substitution);
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete-product/{id}")
    public void deleteProduct(@PathVariable String id) throws HttpException {
        this.electronicProductService.deleteProductById(id);
    }

}
