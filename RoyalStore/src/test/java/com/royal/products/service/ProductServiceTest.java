package com.royal.products.service;

import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.domain.characteristics.specifiers.DesktopOS;
import com.royal.products.domain.requests.SearchElectronicProductRequest;
import com.royal.products.repository.ElectronicProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class ProductServiceTest {
    @Autowired
    private ElectronicProductRepository electronicProductRepository;
    @Autowired
    private MongoTemplate template;
    private ElectronicProductService electronicProductService;

    @BeforeAll
    public void setUp() {
        this.electronicProductService = new ElectronicProductService(this.electronicProductRepository, this.template);
    }

    @Test
    public void noIdDuplicatesInDatabase() {
        ArrayList<String> ids = new ArrayList<>((int) (this.electronicProductRepository.count() + 1));
        ElectronicProduct originalProduct = this.electronicProductService.getRandomStock().get(3);
        ElectronicProduct modifiedProduct = new ElectronicProduct(originalProduct);
        assertDoesNotThrow(() -> this.electronicProductService.updateExistingProduct(modifiedProduct));
        for (ElectronicProduct laptop : this.electronicProductRepository.findAll()) {
            assertFalse(ids.contains(laptop.getId()));
            ids.add(laptop.getId());
        }
    }

    @Test
    public void getNoResultsForNonexistingLaptops() {
        String model = "Motorola";
        List<ElectronicProduct> motorolas = electronicProductService.getProductsByDescription(model);
        assertTrue(motorolas.isEmpty());
    }

    @Test
    public void testGetAllLaptopsByText() {
        String keyword = "Budget";
        List<ElectronicProduct> productsByDescription = electronicProductService.getProductsByDescription(keyword);
        assertFalse(productsByDescription.isEmpty());
        for (ElectronicProduct laptop : productsByDescription)
            assertTrue(laptop.getDescription().contains(keyword));
    }

    @Test
    public void testGetRandomLaptops() {
        List<ElectronicProduct> randomLaptops = electronicProductService.getRandomStock();
        assertFalse(randomLaptops.isEmpty());
    }

    @Test
    public void getLaptopsCheaper() {
        int upperPriceBond = 11116763;
        var filter = new SearchElectronicProductRequest();
        filter.setUpperPriceBond(upperPriceBond);
        List<ElectronicProduct> laptops = electronicProductService.getProductsByParameters(filter.getSearchQuery());
        for (ElectronicProduct laptop : laptops) assertTrue(laptop.getPrice() < upperPriceBond);
    }

    @Test
    public void getNoLaptopsForBadCriteria() {
        var filter = new SearchElectronicProductRequest();
        filter.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Linux));
        List<ElectronicProduct> laptops = electronicProductService.getProductsByParameters(filter.getSearchQuery());
        assertTrue(laptops.isEmpty());
    }

    @Test
    public void testUpdateLaptopById() {
        var windows10OnlyLaptops = new SearchElectronicProductRequest();
        windows10OnlyLaptops.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Windows10));
        ElectronicProduct originalProduct = electronicProductService.getProductsByParameters(
                windows10OnlyLaptops.getSearchQuery()).get(0);
        var replacedProduct = new ElectronicProduct(originalProduct);

        replacedProduct.setPrice(originalProduct.getPrice() + 100);
        replacedProduct.setItemsInStock(originalProduct.getItemsInStock() + 30);
        replacedProduct.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Windows11));
        assertDoesNotThrow(() -> electronicProductService.updateExistingProduct(replacedProduct));

        Optional<ElectronicProduct> targetLaptop = electronicProductRepository.findById(originalProduct.getId());
        assertTrue(targetLaptop.isPresent());
        ElectronicProduct updatedLaptop = targetLaptop.get();
        assertEquals(updatedLaptop.getPrice(), originalProduct.getPrice() + 100);
        assertEquals(updatedLaptop.getItemsInStock(), originalProduct.getItemsInStock() + 30);
        assertTrue(updatedLaptop.getCharacteristics().contains(
                new DesktopOperatingSystemCharacteristic(DesktopOS.Windows11)));
    }

    @Test
    public void testDeleteLaptopById() {
        ElectronicProduct product = electronicProductService.getRandomStock().get(0);
        assertDoesNotThrow(() -> electronicProductService.deleteProductById(product.getId()));
        assertFalse(electronicProductRepository.existsById(product.getId()));
    }
}