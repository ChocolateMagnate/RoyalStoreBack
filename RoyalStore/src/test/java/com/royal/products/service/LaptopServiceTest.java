package com.royal.products.service;

import com.royal.products.domain.Laptop;
import com.royal.products.domain.enumerations.DesktopOS;
import com.royal.products.domain.search.LaptopSearchFilter;
import com.royal.products.repository.LaptopRepository;
import org.junit.jupiter.api.AfterAll;
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
class LaptopServiceTest {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private MongoTemplate template;
    private LaptopService laptopService;

    @BeforeAll
    public void setUp() {
        this.laptopService = new LaptopService(this.laptopRepository, this.template);
    }

    @AfterAll
    public void tearDown() {
        laptopRepository.deleteAll();
    }

    @Test
    public void noIdDuplicatesInDatabase() {
        ArrayList<String> ids = new ArrayList<>((int) (laptopRepository.count() + 1));
        Laptop originalLaptop = laptopService.getRandomLaptops().get(3);
        Laptop modifiedLaptop = new Laptop(originalLaptop);
        assertDoesNotThrow(() -> laptopService.updateLaptopById(originalLaptop.getId(), modifiedLaptop));
        for (Laptop laptop : laptopRepository.findAll()) {
            assertFalse(ids.contains(laptop.getId()));
            ids.add(laptop.getId());
        }
    }

    @Test
    public void getNoResultsForNonexistingLaptops() {
        String model = "Motorola";
        List<Laptop> motorolas = laptopService.getAllLaptopsByText(model);
        assertTrue(motorolas.isEmpty());
    }

    @Test
    public void testGetAllLaptopsByText() {
        String keyword = "budget";
        List<Laptop> budgetLaptops = laptopService.getAllLaptopsByText(keyword);
        assertFalse(budgetLaptops.isEmpty());
        for (Laptop laptop : budgetLaptops) assertTrue(laptop.getDescription().contains(keyword));
    }

    @Test
    public void testGetRandomLaptops() {
        List<Laptop> randomLaptops = laptopService.getRandomLaptops();
        assertFalse(randomLaptops.isEmpty());
    }

    @Test
    public void getLaptopsCheaper() {
        int upperPriceBond = 11116763;
        var filter = new LaptopSearchFilter();
        filter.setUpperPriceBond(upperPriceBond);
        List<Laptop> laptops = laptopService.getAllLaptopsByParameters(filter);
        for (Laptop laptop : laptops) assertTrue(laptop.getPrice() < upperPriceBond);
    }

    @Test
    public void getNoLaptopsForBadCriteria() {
        var filter = new LaptopSearchFilter();
        filter.setOs(DesktopOS.Linux);
        List<Laptop> laptops = laptopService.getAllLaptopsByParameters(filter);
        assertTrue(laptops.isEmpty());
    }

    @Test
    public void testUpdateLaptopById() {
        LaptopSearchFilter windows10OnlyLaptops = new LaptopSearchFilter();
        windows10OnlyLaptops.setOs(DesktopOS.Windows10);
        Laptop originalLaptop = laptopService.getAllLaptopsByParameters(windows10OnlyLaptops).get(0);
        Laptop replacedLaptop = new Laptop(originalLaptop);

        replacedLaptop.setPrice(originalLaptop.getPrice() + 100);
        replacedLaptop.setItemsInStock(originalLaptop.getItemsInStock() + 30);
        replacedLaptop.setOs(DesktopOS.Windows11);
        assertDoesNotThrow(() -> laptopService.updateLaptopById(originalLaptop.getId(), replacedLaptop));

        Optional<Laptop> targetLaptop = laptopRepository.findById(originalLaptop.getId());
        assertTrue(targetLaptop.isPresent());
        Laptop updatedLaptop = targetLaptop.get();
        assertEquals(updatedLaptop.getPrice(), originalLaptop.getPrice() + 100);
        assertEquals(updatedLaptop.getItemsInStock(), originalLaptop.getItemsInStock() + 30);
        assertEquals(updatedLaptop.getOs(), DesktopOS.Windows11);
    }

    @Test
    public void testDeleteLaptopById() {
        Laptop laptop = laptopService.getRandomLaptops().get(0);
        assertDoesNotThrow(() -> laptopService.deleteLaptopById(laptop.getId()));
        assertFalse(laptopRepository.existsById(laptop.getId()));
    }
}