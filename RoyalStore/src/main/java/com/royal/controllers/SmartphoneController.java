package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.Smartphone;
import com.royal.services.SmartphoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SmartphoneController {
    @Autowired
    SmartphoneService smartphoneService;

    @GetMapping("/get-smartphones")
    public List<Smartphone> getSmartphonesWithFields(@RequestBody Smartphone soughtSmartphone) {
        return smartphoneService.getAllSmartphonesByParameters(soughtSmartphone.asHashMap());
    }

    @PutMapping("/create-smartphone")
    public void createSmartphone(@RequestBody Smartphone newSmartphone) throws HttpException {
        smartphoneService.createSmartphone(newSmartphone);
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
