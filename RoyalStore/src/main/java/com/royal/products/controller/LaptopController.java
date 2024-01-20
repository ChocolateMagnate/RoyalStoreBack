package com.royal.products.controller;

import com.royal.errors.HttpException;
import com.royal.products.domain.Laptop;
import com.royal.products.domain.search.LaptopSearchFilter;
import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import com.royal.products.service.LaptopService;
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
    private final LaptopService laptopService;

    LaptopController(@Autowired LaptopService laptopService) {
        this.laptopService = laptopService;
    }

    @PostMapping("get-laptops-by-text")
    public List<Laptop> getLaptopsByText(@RequestBody String description) {
        return laptopService.getAllLaptopsByText(description);
    }

    @PostMapping("/get-laptops")
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
       Laptop laptop = buildLaptopWith(model, brand, price, photo, os, memory, description);
       laptopService.createNewLaptop(laptop);
       log.info("Created a new laptop: " + laptop);
       return laptop.getId();
    }

    @PostMapping(value = "/update-laptop/{id}", consumes = "multipart/form-data")
    public void updateLaptop(@PathVariable String id, @RequestParam("model") String model,
                             @RequestParam("brand") String brand,
                             @RequestParam("price") float price,
                             @RequestParam("photo") MultipartFile photo,
                             @RequestParam("os") String os,
                             @RequestParam("memory") int memory,
                             @RequestParam("description") String description) throws HttpException {
        Laptop updatedLaptop = buildLaptopWith(model, brand, price, photo, os, memory, description);
        laptopService.updateLaptopById(id, updatedLaptop);

    }

    @DeleteMapping("/delete-laptop/{id}")
    public void deleteLaptop(@PathVariable String id) throws HttpException {
        laptopService.deleteLaptopById(id);
    }

    private Laptop buildLaptopWith(String model, String brand, float price, @NotNull MultipartFile photo,
                                   String os, int memory, String description) throws HttpException {
        try {
            var generatedLaptop = new Laptop();
            generatedLaptop.setModel(model);
            generatedLaptop.setBrand(DesktopBrand.valueOf(brand));
            generatedLaptop.setPrice(price);
            generatedLaptop.setPhoto(photo.getBytes());
            generatedLaptop.setOs(DesktopOS.valueOf(os));
            generatedLaptop.setMemory(memory);
            generatedLaptop.setDescription(description);
            generatedLaptop.setItemsInStock(1);
            return generatedLaptop;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}