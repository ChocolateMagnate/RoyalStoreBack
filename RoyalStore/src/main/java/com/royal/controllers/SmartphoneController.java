package com.royal.controllers;

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
    public ResponseEntity<Void> createSmartphone(@RequestBody Smartphone newSmartphone) {
        int code = smartphoneService.createSmartphone(newSmartphone);
        return ResponseEntity.status(code).build();
    }

    @PostMapping("/update-smartphone/{id}")
    public ResponseEntity<Void> updateSmartphone(@PathVariable String id, @RequestBody Smartphone updatedSmartphone) {
        int code = smartphoneService.updateSmartphoneById(id, updatedSmartphone);
        return ResponseEntity.status(code).build();
    }

    @DeleteMapping("/delete-smartphone/{id}")
    public ResponseEntity<Void> deleteSmartphoneById(@PathVariable String id) {
        int code = smartphoneService.deleteSmartphoneById(id);
        return ResponseEntity.status(code).build();
    }

}
