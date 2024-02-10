package com.royal.products;

import com.royal.FixtureInitializer;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.characteristics.candidates.DesktopBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.domain.characteristics.specifiers.DesktopBrand;
import com.royal.products.domain.characteristics.specifiers.DesktopOS;
import com.royal.products.repository.ElectronicProductRepository;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Component
public class ProductFixtureManager extends FixtureInitializer implements ApplicationRunner {
    private final ElectronicProductRepository electronicProductRepository;

    public ProductFixtureManager(@Autowired ElectronicProductRepository electronicProductRepository) {
        this.electronicProductRepository = electronicProductRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<ElectronicProduct> laptops = loadObjectsFromFixture("fixtures/laptops.yaml", ElectronicProduct.class);
        this.electronicProductRepository.saveAll(laptops);
    }

    @PreDestroy
    public void deleteTestingDatabase() {
        this.electronicProductRepository.deleteAll();
    }

    public @NotNull ElectronicProduct generateTestingProduct() {
        try {
            ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents("fixtures/laptops.yaml");
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

}
