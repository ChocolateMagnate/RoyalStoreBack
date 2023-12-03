package com.royal.controllers;

import com.royal.errors.HttpException;
import com.royal.models.products.Smartphone;
import com.royal.services.SmartphoneService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
public class SmartphoneController {
    @Autowired
    SmartphoneService smartphoneService;

    @GetMapping("/get-smartphones")
    public List<Smartphone> getSmartphonesWithFields(@RequestBody Smartphone soughtSmartphone) {
        return smartphoneService.getAllSmartphonesByParameters(soughtSmartphone.asHashMap());
    }

    @PostMapping("/create-smartphone")
    public void createSmartphone(@RequestBody Smartphone newSmartphone) throws HttpException {
        log.info("Created smartphone: " + newSmartphone.toString());
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
