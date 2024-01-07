package com.royal.controllers;

import com.royal.products.ElectronicProduct;
import com.royal.products.search.ElectronicProductSearchFilter;
import com.royal.services.ProductService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/get-products")
    public List<ElectronicProduct> getProductsWithParameters(@RequestBody @NotNull ElectronicProductSearchFilter filter) {
        return productService.getProductsByParameters(filter.getSearchQuery());
    }

    @GetMapping("/get-random-products")
    public List<ElectronicProduct> getRandomProducts() {
        return productService.getRandomStock();
    }

    @GetMapping("/search-products-by-search")
    public List<ElectronicProduct> searchProductsBySearch(@RequestParam String search) {
        return productService.getProductsByDescription(search);
    }

}
