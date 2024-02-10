package com.royal.products.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.royal.products.ProductFixtureManager;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.GenericProductProperty;
import com.royal.products.domain.characteristics.CharacteristicsSet;
import com.royal.products.domain.characteristics.specifiers.DesktopOS;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.domain.requests.SearchElectronicProductRequest;
import com.royal.products.repository.ElectronicProductRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WithAnonymousUser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class ProductControllerTest {
    @Autowired
    private ElectronicProductRepository electronicProductRepository;
    @Autowired
    private WebApplicationContext context;
    private ProductFixtureManager fixture;
    private MockMvc mvc;

    @BeforeAll
    void setUp() {
       this.mvc = MockMvcBuilders.webAppContextSetup(context)
               .apply(SecurityMockMvcConfigurers.springSecurity())
               .build();
       this.fixture = new ProductFixtureManager(electronicProductRepository);
    }

    @AfterAll
    void tearDown() {
        electronicProductRepository.deleteAll();
    }

    @Test
    void testGetProductsByText() throws Exception {
        String text = "large display";
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-products-by-search")
                        .content(text).contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        assertDoesNotThrow(() -> {
            JSONArray response = buildJsonArray(result);
            assertTrue(response.length() > 0);
        });
    }

    @Test
    public void shouldRejectEmptySearchQuery() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/get-products-by-search")
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGiveEmptyArrayForNonexistentKeywords() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-products-by-search")
                        .content("linux").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertDoesNotThrow(() -> {
            JSONArray response = buildJsonArray(result);
            assertEquals(response.length(), 0);
        });
    }

    @Test
    void testGetLaptopsWithFields() throws Exception {
        var filter = new SearchElectronicProductRequest();
        filter.setUpperPriceBond(20000);
        filter.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Windows10));
        JSONObject body = buildJsonObject(filter);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var products = buildJsonArray(result);
        assertTrue(() -> products.length() > 0);
    }

    @Test
    public void shouldRejectEmptyFilter() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGiveEmptyArrayIfFilterDoesNotMatchAnything() throws Exception {
        var filter = new SearchElectronicProductRequest();
        filter.addCharacteristic(new DesktopOperatingSystemCharacteristic(DesktopOS.Linux));
        String body = buildJsonObject(filter).toString();
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var products = buildJsonArray(result);
        assertEquals(products.length(), 0);
    }

    @Test
    void testGetRandomProducts() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/get-random-products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONArray products = buildJsonArray(result);
        assertTrue(products.length() > 0);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void testCreateProduct() throws Exception {
        ElectronicProduct product = fixture.generateTestingProduct();
        var photo = new MockMultipartFile("photo", "mocked-photo.png",
                MediaType.TEXT_PLAIN_VALUE, product.getPhoto());
        String characteristics = new ObjectMapper().writeValueAsString(product.getCharacteristics());

        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/create-product")
                .file(photo)
                .param("model", product.getModel())
                .param("brand", product.getCharacteristicByKey(GenericProductProperty.Brand).toString())
                .param("price", String.valueOf(product.getPrice()))
                .param("os", product.getCharacteristicByKey(GenericProductProperty.OperatingSystem).toString())
                .param("memory", String.valueOf(product.getMemory()))
                .param("storage", product.getStorage().toString())
                .param("description", product.getDescription())
                .param("category", product.getCategory().toString())
                .param("characteristics", characteristics))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        // Now we want to verify the returned response body points to a valid product in the database.
        String id = result.getResponse().getContentAsString();
        var filter = new SearchElectronicProductRequest();
        filter.setId(id);
        JSONObject body = buildJsonObject(filter);
        mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    public void rejectProductCreationIfAnyFieldIsMissing() throws Exception {
        ElectronicProduct product = fixture.generateTestingProduct();
        product.setPrice(null);
        mvc.perform(MockMvcRequestBuilders.multipart("/create-product")
                .param("model", product.getModel())
                .param("price", String.valueOf(product.getPrice())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void testUpdateProduct() throws Exception {
        ElectronicProduct product = getExistingLaptop();
        var photo = new MockMultipartFile("photo", "mocked-photo.png",
                MediaType.TEXT_PLAIN_VALUE, product.getPhoto());
        var characteristics = new CharacteristicsSet();
        characteristics.add(new DesktopOperatingSystemCharacteristic(DesktopOS.Windows11));
        characteristics.add(product.getCharacteristicByKey(GenericProductProperty.Brand));
        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/update-product")
                .file(photo)
                .param("id", product.getId())
                .param("model", product.getModel() + " Pro Max")
                .param("price", String.valueOf(product.getPrice() + 400))
                .param("memory", String.valueOf(product.getMemory() + 8))
                .param("storage", product.getStorage().toString())
                .param("category", product.getCategory().toString())
                .param("description", product.getDescription())
                .param("characteristics", new ObjectMapper().writeValueAsString(characteristics)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Similarly to the test where we created a product above, we need to make
        // sure the returned id points to a valid product object in the database.
        String id = result.getResponse().getContentAsString();
        var filter = new SearchElectronicProductRequest();
        filter.setId(id);
        JSONObject body = buildJsonObject(filter);
        mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    public void rejectProductUpdateIfAnyFieldsAreMissing() throws Exception {
        ElectronicProduct product = getExistingLaptop();
        product.setPhoto(null);
        mvc.perform(MockMvcRequestBuilders.multipart("/update-product")
                        .param("model", product.getModel())
                        .param("price", String.valueOf(product.getPrice())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testDeleteProduct() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/get-random-products")).andReturn();
        JSONArray products = buildJsonArray(result);
        var target = (JSONObject)products.get(0);
        String deletedId = target.getString("id");
        mvc.perform(MockMvcRequestBuilders.delete("/delete-product/" + deletedId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //Finally, we check if the laptop under the same id still exists after we deleted it.
        var filter = new SearchElectronicProductRequest();
        filter.setId(deletedId);
        JSONObject body = buildJsonObject(filter);
        result = mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        products = buildJsonArray(result);
        for (int index = 0; index < products.length(); ++index) {
            var product = (JSONObject)products.get(index);
            String givenId = product.getString("id");
            assertNotEquals(deletedId, givenId);
        }
    }

    private ElectronicProduct getExistingLaptop() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/get-random-products"))
                .andReturn();
        var mapper = new ObjectMapper();
        JSONArray products = buildJsonArray(result);
        String productJsonString = products.getString(0);
        return mapper.readValue(productJsonString, ElectronicProduct.class);
    }

    @NotNull
    private static JSONObject buildJsonObject(Object filter) throws JsonProcessingException, JSONException {
        var mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String filterJsonString = ow.writeValueAsString(filter);
        return new JSONObject(filterJsonString);
    }

    @Contract("_ -> new")
    private static @NotNull JSONArray buildJsonArray(@NotNull MvcResult result) throws JSONException, UnsupportedEncodingException {
        return new JSONArray(result.getResponse().getContentAsString());
    }
}