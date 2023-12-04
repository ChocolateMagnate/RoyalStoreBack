package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.Smartphone;
import com.royal.models.products.SmartphoneSearchFilter;
import com.royal.models.products.enumerations.MobileBrand;
import com.royal.models.products.enumerations.MobileOS;
import com.royal.services.SmartphoneService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
public class SmartphoneController {
    @Autowired
    SmartphoneService smartphoneService;

    @GetMapping("/get-smartphones")
    public List<Smartphone> getSmartphonesWithFields(@RequestBody SmartphoneSearchFilter soughtSmartphone) {
        return smartphoneService.getAllSmartphonesByParameters(soughtSmartphone);
    }

    @GetMapping("/get-random-smartphones")
    public List<Smartphone> getRandomSmartphones() {
        return smartphoneService.getRandomSmartphones();
    }

    @PostMapping(value = "/create-smartphone", consumes = "multipart/form-data")
    public String createSmartphone(@RequestParam("model") String model,
                                   @RequestParam("brand") String brand,
                                   @RequestParam("price") float price,
                                   @RequestParam("photo") MultipartFile photo,
                                   @RequestParam("os") String os,
                                   @RequestParam("memory") int memory,
                                   @RequestParam("description") String description) throws HttpException {
        try {
            var newSmartphone = new Smartphone();
            newSmartphone.setModel(model);
            newSmartphone.setBrand(MobileBrand.valueOf(brand));
            newSmartphone.setPrice(price);
            newSmartphone.setOs(MobileOS.valueOf(os));
            newSmartphone.setMemory(memory);
            newSmartphone.setDescription(description);
            newSmartphone.setPhoto(photo.getBytes());
            newSmartphone.setItemsInStock(1);
            log.info("Created smartphone: " + newSmartphone);
            smartphoneService.createSmartphone(newSmartphone);
            return newSmartphone.getId();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/update-smartphone/{id}")
    public void updateSmartphone(@PathVariable String id, @RequestBody Smartphone updatedSmartphone) throws HttpException {
        smartphoneService.updateSmartphoneById(id, updatedSmartphone);
    }

    @DeleteMapping("/delete-smartphone/{id}")
    public void deleteSmartphoneById(@PathVariable String id) throws HttpException {
        smartphoneService.deleteSmartphoneById(id);
    }

}
