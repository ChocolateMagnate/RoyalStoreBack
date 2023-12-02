package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.Laptop;
import com.royal.services.LaptopService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LaptopController {
    @Autowired
    private LaptopService laptopService;

    @GetMapping("/get-laptops")
    public List<Laptop> getLaptopsWithFields(@RequestBody @NotNull Laptop soughtLaptop) {
        return laptopService.getAllLaptopsByParameters(soughtLaptop.asHashMap());
    }

    @PutMapping("/create-laptop")
    public void createLaptop(@RequestBody Laptop newLaptop) throws HttpException {
        laptopService.createNewLaptop(newLaptop);
    }

    @PostMapping("/update-laptop/{id}")
    public void updateLaptop(@PathVariable String id, @RequestBody Laptop updatedLaptop) throws HttpException {
        laptopService.updateLaptopById(id, updatedLaptop);

    }

    @DeleteMapping("/delete-laptop/{id}")
    public void deleteLaptop(@PathVariable String id) throws HttpException {
        laptopService.deleteLaptopById(id);
    }
}