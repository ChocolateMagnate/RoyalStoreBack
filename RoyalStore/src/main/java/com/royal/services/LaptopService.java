package com.royal.services;

import com.royal.errors.HttpException;
import com.royal.models.products.Laptop;
import com.royal.models.products.LaptopSearchFilter;
import com.royal.repositories.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private MongoTemplate template;

    public List<Laptop> getAllLaptopsByParameters(LaptopSearchFilter filter) {
        Query query = filter.getSearchQuery();
        return template.find(query, Laptop.class);
    }

    public List<Laptop> getRandomLaptops() {
        return laptopRepository.findAll().stream().limit(20).toList();
    }

    public void createNewLaptop(Laptop newLaptop) {
        Optional<Laptop> targetLaptop = laptopRepository.findOne(Example.of(newLaptop));
        if (targetLaptop.isEmpty()) laptopRepository.save(newLaptop);
        else {
            Laptop updatedLaptop = targetLaptop.get();
            updatedLaptop.setItemsInStock(updatedLaptop.getItemsInStock() + 1);
            laptopRepository.save(updatedLaptop);
        }
    }

    public void updateLaptopById(String id, Laptop updatedLaptop) throws HttpException {
        Optional<Laptop> laptopInDatabase = laptopRepository.findById(id);
        if (laptopInDatabase.isEmpty())
            throw new HttpException(HttpStatus.NOT_FOUND, "The laptop by ID " + id + " does not exist.");
        Laptop extractedLaptop = laptopInDatabase.get();
        extractedLaptop.setOs(updatedLaptop.getOs());
        extractedLaptop.setBrand(updatedLaptop.getBrand());
        extractedLaptop.setPhoto(updatedLaptop.getPhoto());
        extractedLaptop.setPrice(updatedLaptop.getPrice());
        extractedLaptop.setMemory(updatedLaptop.getMemory());
        extractedLaptop.setDescription(updatedLaptop.getDescription());
        laptopRepository.save(extractedLaptop);
    }

    public void deleteLaptopById(String id) throws HttpException {
        if (!laptopRepository.existsById(id))
            throw new HttpException(HttpStatus.NOT_FOUND, "Laotptop by ID " + id + " does not exist.");
        laptopRepository.deleteById(id);
    }
}
