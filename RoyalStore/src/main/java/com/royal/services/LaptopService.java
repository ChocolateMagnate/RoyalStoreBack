package com.royal.services;

import com.royal.models.products.Laptop;
import com.royal.repositories.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private MongoTemplate template;

    public List<Laptop> getAllLaptopsByParameters(HashMap<String, Object> soughtLaptop) {
        Criteria criteria = new Criteria();
        for (String key : soughtLaptop.keySet()) {
            Object value = soughtLaptop.get(key);
            if (value != null) criteria.and(key).is(value);
        }
        Query customisedVariableParameterQuery = new Query(criteria);
        return template.find(customisedVariableParameterQuery, Laptop.class);
    }

    public int createNewLaptop(Laptop newLaptop) {
        if (laptopRepository.exists(Example.of(newLaptop))) return HttpStatus.FOUND.value();
        laptopRepository.save(newLaptop);
        return HttpStatus.OK.value();
    }

    public int updateLaptopById(String id, Laptop updatedLaptop) {
        Optional<Laptop> laptopInDatabase = laptopRepository.findById(id);
        if (laptopInDatabase.isEmpty()) return HttpStatus.NOT_FOUND.value();
        Laptop extractedLaptop = laptopInDatabase.get();
        extractedLaptop.setOs(updatedLaptop.getOs());
        extractedLaptop.setCard(updatedLaptop.getCard());
        extractedLaptop.setBrand(updatedLaptop.getBrand());
        extractedLaptop.setPhoto(updatedLaptop.getPhoto());
        extractedLaptop.setPrice(updatedLaptop.getPrice());
        extractedLaptop.setMemory(updatedLaptop.getMemory());
        extractedLaptop.setStorage(updatedLaptop.getStorage());
        extractedLaptop.setProcessor(updatedLaptop.getProcessor());
        extractedLaptop.setResolution(updatedLaptop.getResolution());
        extractedLaptop.setDescription(updatedLaptop.getDescription());
        extractedLaptop.setBuildMaterial(updatedLaptop.getBuildMaterial());
        extractedLaptop.setConnectivities(updatedLaptop.getConnectivities());
        laptopRepository.save(extractedLaptop);
        return HttpStatus.OK.value();
    }

    public int deleteLaptopById(String id) {
        if (!laptopRepository.existsById(id)) return HttpStatus.NOT_FOUND.value();
        laptopRepository.deleteById(id);
        return HttpStatus.OK.value();
    }
}
