package com.royal.controllers;

import com.royal.models.products.Laptop;
import com.royal.services.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LaptopController {
    @Autowired
    private LaptopService laptopService;

    @GetMapping("/get-laptops")
    public List<Laptop> getLaptopsWithFields(@RequestBody Laptop soughtLaptop) {
        return laptopService.getAllLaptopsByParameters(soughtLaptop.asHashMap());
    }

    @PutMapping("/create-laptop")
    public ResponseEntity<Void> createLaptop(@RequestBody Laptop newLaptop) {
        int code = laptopService.createNewLaptop(newLaptop);
        return ResponseEntity.status(code).build();
    }

    @PostMapping("/update-laptop/{id}")
    public ResponseEntity<Void> updateLaptop(@PathVariable String id, @RequestBody Laptop updatedLaptop) {
        int code = laptopService.updateLaptopById(id, updatedLaptop);
        return ResponseEntity.status(code).build();
    }

    @DeleteMapping("/delete-laptop/{id}")
    public ResponseEntity<Void> deleteLaptop(@PathVariable String id) {
        int code = laptopService.deleteLaptopById(id);
        return ResponseEntity.status(code).build();
    }
}