package com.royal.services;

import com.royal.errors.HttpException;
import com.royal.models.products.Smartphone;
import com.royal.models.products.SmartphoneSearchFilter;
import com.royal.repositories.SmartphoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SmartphoneService {
    @Autowired
    private SmartphoneRepository smartphoneRepository;
    @Autowired
    private MongoTemplate template;

    public List<Smartphone> getAllSmartphonesByParameters(SmartphoneSearchFilter filter) {
        Query query = filter.getSearchQuery();
        return template.find(query, Smartphone.class);
    }

    public List<Smartphone> getRandomSmartphones() {
        return smartphoneRepository.findAll().stream().limit(20).toList();
    }

    public void createSmartphone(Smartphone newSmartphone) {
        Optional<Smartphone> targetSmartphone = smartphoneRepository.findOne(Example.of(newSmartphone));
        if (targetSmartphone.isEmpty()) smartphoneRepository.save(newSmartphone);
        else {
            // If the same smartphone already exists in database, we want to pump its number of items.
            Smartphone smartphoneInDatabase = targetSmartphone.get();
            smartphoneInDatabase.setItemsInStock(smartphoneInDatabase.getItemsInStock() + 1);
            smartphoneRepository.save(smartphoneInDatabase);
        }
    }

    public void updateSmartphoneById(String id, Smartphone updatedSmartphone) throws HttpException {
        Optional<Smartphone> smartphoneInDatabase = smartphoneRepository.findById(id);
        if (smartphoneInDatabase.isEmpty())
            throw new HttpException(HttpStatus.NOT_FOUND, "Smartphone by ID " + id + " already exists.");
        Smartphone extractedSmartphone = smartphoneInDatabase.get();
        extractedSmartphone.setOs(updatedSmartphone.getOs());
        extractedSmartphone.setBrand(updatedSmartphone.getBrand());
        extractedSmartphone.setPrice(updatedSmartphone.getPrice());
        extractedSmartphone.setMemory(updatedSmartphone.getMemory());
        extractedSmartphone.setModel(extractedSmartphone.getModel());
        extractedSmartphone.setPhoto(extractedSmartphone.getPhoto());
        extractedSmartphone.setDescription(updatedSmartphone.getDescription());
        extractedSmartphone.setItemsInStock(updatedSmartphone.getItemsInStock());
        smartphoneRepository.save(extractedSmartphone);
    }

    public void deleteSmartphoneById(String id) throws HttpException {
        if (!smartphoneRepository.existsById(id))
            throw new HttpException(HttpStatus.NOT_FOUND, "The smartphone by ID " + id + " doesn't exist.");
        smartphoneRepository.deleteById(id);
    }
}