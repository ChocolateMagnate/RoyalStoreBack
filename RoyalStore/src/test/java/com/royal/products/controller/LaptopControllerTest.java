package com.royal.products.controller;

import com.royal.products.domain.Laptop;
import com.royal.products.repository.LaptopRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class LaptopControllerTest {
    @Autowired
    private LaptopRepository laptopRepository;
    private WebTestClient client;
    private final ServerProperties  properties;


    public LaptopControllerTest(@Autowired ServerProperties properties) {
        this.properties = properties;
    }

    @BeforeAll
    void setUp() {
        String protocol = this.properties.getSsl() != null && this.properties.getSsl().isEnabled() ? "https" : "http";
        String host = this.properties.getAddress().getHostAddress();
        Integer port = this.properties.getPort();
        String serverAddress = protocol + "://" + host + ":" + port;
        this.client = WebTestClient.bindToServer().baseUrl(serverAddress).build();
    }

    @AfterAll
    void tearDown() {
        laptopRepository.deleteAll();
    }

    @Test
    void testGetLaptopsByText() {
        int size = Objects.requireNonNull(client.post().uri("/get-laptops")
                .bodyValue("gaming").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Laptop.class).returnResult().getResponseBody()).size();
        assertTrue(size > 0);
    }

    @Test
    void testGetLaptopsWithFields() {

    }

    @Test
    void getRandomLaptops() {
    }

    @Test
    void createLaptop() {
    }

    @Test
    void updateLaptop() {
    }

    @Test
    void deleteLaptop() {
    }
}