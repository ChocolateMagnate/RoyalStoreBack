package com.royal.products;

import com.royal.products.domain.Laptop;
import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import com.royal.products.repository.LaptopRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
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

    public ArrayList<Laptop> loadLaptopsFromFixture() throws IOException {
        ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents();
        ArrayList<Laptop> laptops = new ArrayList<>(contents.size());
        for (LinkedHashMap<String, Object> content : contents) {
            laptops.add(new Laptop(content));
        }
        return laptops;
    }

    public @NotNull Laptop generateTestingLaptop() {
        try {
            ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents();
            var laptop = new Laptop(contents.get(0));
            laptop.setPhoto(RandomStringUtils.random(345).getBytes());
            return laptop;
        } catch (IOException e) {
            var laptop = new Laptop();
            laptop.setModel("Lenovo IdeaPad Slim 5 - Abyss Blue");
            laptop.setPrice(593.99F);
            laptop.setBrand(DesktopBrand.Lenovo);
            laptop.setOs(DesktopOS.Windows11);
            laptop.setMemory(16);
            laptop.setPhoto(RandomStringUtils.random(345).getBytes());
            laptop.setDescription("The IdeaPad Slim 5 is so thin and light that the more you travel, the more you’ll appreciate its slender, rugged, and Mil-SPEC-tested good looks. It’s built for life on the move, with a starting weight of 1.89kg / 4.17lbs, so you can carry it all day without breaking a sweat. It shrugs off knocks and bumps and is the perfect device for working in multiple locations and on business trips.");
            return laptop;
        }
    }

    private ArrayList<LinkedHashMap<String, Object>> getYamlContents() throws IOException {
        Yaml yaml = new Yaml();
        ClassLoader loader = LaptopFixtureInitializer.class.getClassLoader();
        try (InputStream consumer = loader.getResourceAsStream("fixtures/laptops.yaml")) {
            return yaml.load(consumer);
        }
    }
}
