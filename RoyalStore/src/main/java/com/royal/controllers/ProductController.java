package com.royal.controllers;

import com.royal.models.products.ElectronicProduct;
import com.royal.models.products.ElectronicProductSearchFilter;
import com.royal.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/get-products")
    public List<ElectronicProduct> getProductsWithParameters(@RequestBody ElectronicProductSearchFilter filter) {
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
