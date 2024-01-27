package com.royal.products;

import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.characteristics.specifiers.DesktopBrand;
import com.royal.products.domain.characteristics.specifiers.DesktopOS;
import com.royal.products.domain.characteristics.candidates.DesktopBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.repository.ElectronicProductRepository;
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
public class ProductFixtureInitializer implements ApplicationRunner {
    private final ElectronicProductRepository electronicProductRepository;

    public ProductFixtureInitializer(@Autowired ElectronicProductRepository electronicProductRepository) {
        this.electronicProductRepository = electronicProductRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<ElectronicProduct> laptops = loadLaptopsFromFixture();
        this.electronicProductRepository.saveAll(laptops);
    }

    public ArrayList<ElectronicProduct> loadLaptopsFromFixture() throws IOException {
        ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents();
        ArrayList<ElectronicProduct> laptops = new ArrayList<>(contents.size());
        for (LinkedHashMap<String, Object> content : contents) {
            laptops.add(new ElectronicProduct(content));
        }
        return laptops;
    }

    public @NotNull ElectronicProduct generateTestingProduct() {
        try {
            ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents();
            var product = new ElectronicProduct(contents.get(0));
            product.setPhoto(RandomStringUtils.random(345).getBytes());
            return product;
        } catch (IOException e) {
            var product = new ElectronicProduct();
            product.setModel("Lenovo IdeaPad Slim 5 - Abyss Blue");
            product.setPrice(593.99F);
            product.setMemory(16);
            product.setPhoto(RandomStringUtils.random(345).getBytes());
            product.addCharacteristic(new DesktopBrandCharacteristic(DesktopBrand.Lenovo));
            product.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Windows11));
            product.setDescription("The IdeaPad Slim 5 is so thin and light that the more you travel, the more you’ll appreciate its slender, rugged, and Mil-SPEC-tested good looks. It’s built for life on the move, with a starting weight of 1.89kg / 4.17lbs, so you can carry it all day without breaking a sweat. It shrugs off knocks and bumps and is the perfect device for working in multiple locations and on business trips.");
            return product;
        }
    }

    private ArrayList<LinkedHashMap<String, Object>> getYamlContents() throws IOException {
        Yaml yaml = new Yaml();
        ClassLoader loader = ProductFixtureInitializer.class.getClassLoader();
        try (InputStream consumer = loader.getResourceAsStream("fixtures/laptops.yaml")) {
            return yaml.load(consumer);
        }
    }
}
