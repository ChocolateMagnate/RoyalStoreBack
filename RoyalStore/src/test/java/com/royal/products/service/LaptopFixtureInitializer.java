package com.royal.products.service;

import com.royal.products.domain.Laptop;
import com.royal.products.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Component
public class LaptopFixtureInitializer implements ApplicationRunner {
    private final LaptopRepository laptopRepository;

    public LaptopFixtureInitializer(@Autowired LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<Laptop> laptops = loadLaptopsFromFixture();
        this.laptopRepository.saveAll(laptops);
    }

    private ArrayList<Laptop> loadLaptopsFromFixture() throws IOException {
        Yaml yaml = new Yaml();
        ClassLoader loader = LaptopFixtureInitializer.class.getClassLoader();
        try (InputStream consumer = loader.getResourceAsStream("fixtures/laptops.yaml")) {
            ArrayList<LinkedHashMap<String, Object>> contents = yaml.load(consumer);
            ArrayList<Laptop> laptops = new ArrayList<>(contents.size());
            for (LinkedHashMap<String, Object> content : contents) {
                laptops.add(new Laptop(content));
            }
            return laptops;
        }
    }
}
