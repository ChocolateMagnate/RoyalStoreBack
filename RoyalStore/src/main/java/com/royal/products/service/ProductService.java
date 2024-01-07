package com.royal.services;

import com.royal.products.ElectronicProduct;
import com.royal.products.Laptop;
import com.royal.products.Smartphone;
import com.royal.products.repository.LaptopRepository;
import com.royal.products.repository.SmartphoneRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ProductService {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private SmartphoneRepository smartphoneRepository;
    @Autowired
    private MongoTemplate template;

    public boolean productExistsById(String id) {
        return laptopRepository.existsById(id) || smartphoneRepository.existsById(id);
    }

    public List<ElectronicProduct> getRandomStock() {
        List<Smartphone> smartphones = smartphoneRepository.findAll().stream().limit(12).toList();
        List<Laptop> laptops = laptopRepository.findAll().stream().limit(8).toList();
        List<ElectronicProduct> results = Stream.of(smartphones, laptops).flatMap(Collection::stream)
                .collect(Collectors.toList());
        Collections.shuffle(results);
        return results;
    }

    public List<ElectronicProduct> getProductsByDescription(String description) {
        List<Smartphone> smartphones = getAllSmartphonesByDescription(description);
        List<Laptop> laptops = getAllLaptopsByDescription(description);
        return Stream.of(smartphones, laptops).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<ElectronicProduct> getProductsByParameters(Query query) {
        List<Smartphone> smartphones = template.find(query, Smartphone.class);
        List<Laptop> laptops = template.find(query, Laptop.class);
        return Stream.of(smartphones, laptops).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Optional<ElectronicProduct> retrieveProductById(String id) {
        Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
        if (optionalLaptop.isPresent()) return Optional.of(optionalLaptop.get());
        Optional<Smartphone> optionalSmartphone = smartphoneRepository.findById(id);
        if (optionalSmartphone.isPresent()) return Optional.of(optionalSmartphone.get());
        return Optional.empty();
    }

    public ArrayList<ElectronicProduct> retrieveProductsFromIds(@NotNull ArrayList<String> ids) {
        ArrayList<ElectronicProduct> products = new ArrayList<>(ids.size());
        for (String id : ids) {
            Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
            if (optionalLaptop.isPresent()) {
                products.add(optionalLaptop.get());
                continue;
            }
            Optional<Smartphone> optionalSmartphone = smartphoneRepository.findById(id);
            optionalSmartphone.ifPresent(products::add);
        }
        return products;
    }

    private List<Laptop> getAllLaptopsByDescription(String description) {
        return laptopRepository.findAll().stream()
                .filter(laptop -> laptop.getModel().contains(description)).toList();
    }

    private List<Smartphone> getAllSmartphonesByDescription(String description) {
        return smartphoneRepository.findAll().stream()
                .filter(smartphone -> smartphone.getModel().contains(description)).toList();
    }
}
