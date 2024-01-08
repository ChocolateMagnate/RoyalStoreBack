package com.royal.products.service;

import com.royal.products.domain.Laptop;
import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import com.royal.products.repository.LaptopRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class LaptopServiceTest {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private MongoTemplate template;
    private LaptopService laptopService;

    private final Laptop macbook1 = new Laptop("ghjg", "Macbook 11", "A fine model",
            DesktopBrand.Apple, DesktopOS.MacOS, 11113.8F, 1112, 4, new byte[]{});

    private final Laptop macbook2 = new Laptop("jujk", "Macbook 14", "A finer model", DesktopBrand.Apple,
            DesktopOS.MacOS, 11116763.8F, 1112, 2, new byte[]{});

    private final Laptop acer = new Laptop("gyujhjg", "Acer 11", "A new model", DesktopBrand.Acer,
            DesktopOS.Windows, 4313.8F, 112, 5, new byte[]{});

    private final Laptop hp = new Laptop("ftibhpquqkq", "HP 11", "A sweeter model", DesktopBrand.HP,
            DesktopOS.Windows, 1113.8F, 1412, 40, new byte[]{});
    @BeforeEach
    void setUp() {
        if (this.laptopService == null)
            this.laptopService = new LaptopService(this.laptopRepository, this.template);
        laptopRepository.save(macbook1);
        laptopRepository.save(macbook2);
        laptopRepository.save(acer);
        laptopRepository.save(hp);
    }

    @AfterEach
    void tearDown() {
        laptopRepository.deleteAll();
    }

    @Test
    public void getNoResultsForNonexistingLaptops() {
        String model = "Motorola";
        List<Laptop> motorolas = laptopService.getAllLaptopsByText(model);
        assertTrue(motorolas.isEmpty());
    }

    @Test
    public void testGetAllLaptopsByText() {
        String criteria = "fine";
        List<Laptop> macbooks = laptopService.getAllLaptopsByText(criteria);
        assertFalse(macbooks.isEmpty());
        assertTrue(macbooks.contains(macbook1));
        assertTrue(macbooks.contains(macbook2));
    }
}