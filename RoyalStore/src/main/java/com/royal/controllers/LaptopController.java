package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.Laptop;
import com.royal.models.products.LaptopSearchFilter;
import com.royal.models.products.enumerations.DesktopBrand;
import com.royal.models.products.enumerations.DesktopOS;
import com.royal.services.LaptopService;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
public class LaptopController {
    @Autowired
    private LaptopService laptopService;

    @GetMapping("/get-laptops")
    public List<Laptop> getLaptopsWithFields(@RequestBody @NotNull LaptopSearchFilter soughtLaptop) {
        return laptopService.getAllLaptopsByParameters(soughtLaptop);
    }

    @GetMapping("/get-random-laptops")
    public List<Laptop> getRandomLaptops() {
        return laptopService.getRandomLaptops();
    }

    @PostMapping(value = "/create-laptop", consumes = "multipart/form-data")
    public String createLaptop(@RequestParam("model") String model,
                               @RequestParam("brand") String brand,
                               @RequestParam("price") float price,
                               @RequestParam("photo") MultipartFile photo,
                               @RequestParam("os") String os,
                               @RequestParam("memory") int memory,
                               @RequestParam("description") String description) throws HttpException {
        try {
            var newLaptop = new Laptop();
            newLaptop.setModel(model);
            newLaptop.setBrand(DesktopBrand.valueOf(brand));
            newLaptop.setPrice(price);
            newLaptop.setPhoto(photo.getBytes());
            newLaptop.setOs(DesktopOS.valueOf(os));
            newLaptop.setMemory(memory);
            newLaptop.setDescription(description);
            newLaptop.setItemsInStock(1);
            laptopService.createNewLaptop(newLaptop);
            log.info("Created a new laptop: " + newLaptop);
            return newLaptop.getId();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
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