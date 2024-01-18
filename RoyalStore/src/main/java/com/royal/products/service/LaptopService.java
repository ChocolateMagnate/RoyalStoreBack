package com.royal.products.service;

import com.royal.errors.HttpException;
import com.royal.products.domain.Laptop;
import com.royal.products.domain.search.LaptopSearchFilter;
import com.royal.products.repository.LaptopRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {
    private final LaptopRepository laptopRepository;
    private final MongoTemplate template;

    LaptopService(@Autowired LaptopRepository laptopRepository, @Autowired MongoTemplate template) {
        this.laptopRepository = laptopRepository;
        this.template = template;
    }

    public List<Laptop> getAllLaptopsByText(@NotNull String text) {
        var criteria = new Criteria();
        String keywords = text.toLowerCase().replace(" ", "|");
        String pattern = "\\b(?:" + keywords + ")\\b";
        criteria.and("description").regex(pattern);
        return template.find(new Query(criteria), Laptop.class);
    }

    public List<Laptop> getAllLaptopsByParameters(@NotNull LaptopSearchFilter filter) {
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
        extractedLaptop.setModel(updatedLaptop.getModel());
        extractedLaptop.setMemory(updatedLaptop.getMemory());
        extractedLaptop.setDescription(updatedLaptop.getDescription());
        extractedLaptop.setItemsInStock(updatedLaptop.getItemsInStock());
        laptopRepository.save(extractedLaptop);
    }

    public void deleteLaptopById(String id) throws HttpException {
        if (!laptopRepository.existsById(id))
            throw new HttpException(HttpStatus.NOT_FOUND, "Laptop by ID " + id + " does not exist.");
        laptopRepository.deleteById(id);
    }
}
