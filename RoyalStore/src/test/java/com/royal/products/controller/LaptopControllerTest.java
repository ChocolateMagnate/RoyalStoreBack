package com.royal.products.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.royal.products.LaptopFixtureInitializer;
import com.royal.products.domain.Laptop;
import com.royal.products.domain.enumerations.DesktopOS;
import com.royal.products.domain.search.ElectronicProductSearchFilter;
import com.royal.products.domain.search.LaptopSearchFilter;
import com.royal.products.repository.LaptopRepository;
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

@WithAnonymousUser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
class LaptopControllerTest {
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private WebApplicationContext context;
    private LaptopFixtureInitializer fixture;
    private MockMvc mvc;

    @BeforeAll
    void setUp() {
       this.mvc = MockMvcBuilders.webAppContextSetup(context)
               .apply(SecurityMockMvcConfigurers.springSecurity())
               .build();
       this.fixture = new LaptopFixtureInitializer(laptopRepository);
    }

    @AfterAll
    void tearDown() {
        laptopRepository.deleteAll();
    }

    @Test
    void testGetLaptopsByText() throws Exception {
        String text = "large display";
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post("/get-laptops-by-text").contentType(MediaType.APPLICATION_JSON)
                        .content(text)
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        assertDoesNotThrow(() -> {
            JSONArray response = buildJsonArray(result);
            assertTrue(response.length() > 0);
            ObjectMapper mapper = new ObjectMapper().registerModule(new JsonOrgModule());
            Laptop laptop = mapper.convertValue(response.get(0), Laptop.class);
        });
    }

    @Test
    public void shouldRejectEmptySearchQuery() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/get-laptops-by-text")
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGiveEmptyArrayForNonexistentKeywords() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-laptops-by-text")
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
        var filter = new LaptopSearchFilter();
        filter.setUpperPriceBond(2000);
        filter.setOs(DesktopOS.Windows10);
        JSONObject body = buildJsonObject(filter);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-laptops")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var laptops = buildJsonArray(result);
        assertTrue(() -> laptops.length() > 0);
    }

    @Test
    public void shouldRejectEmptyFilter() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/get-laptops")
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGiveEmptyArrayIfFilterDoesNotMatchAnything() throws Exception {
        var filter = new LaptopSearchFilter();
        filter.setOs(DesktopOS.Linux);
        JSONObject body = buildJsonObject(filter);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/get-laptops")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var laptops = buildJsonArray(result);
        assertEquals(laptops.length(), 0);
    }

    @Test
    void testGetRandomLaptops() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/get-random-laptops"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONArray laptops = buildJsonArray(result);
        assertTrue(laptops.length() > 0);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void testCreateLaptop() throws Exception {
        Laptop laptop = fixture.generateTestingLaptop();
        var photo = new MockMultipartFile("photo", "mocked-photo.png",
                MediaType.TEXT_PLAIN_VALUE, laptop.getPhoto());


        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/create-laptop")
                .file(photo)
                .param("model", laptop.getModel())
                .param("brand", laptop.getBrand().toString())
                .param("price", String.valueOf(laptop.getPrice()))
                .param("os", laptop.getOs().toString())
                .param("memory", String.valueOf(laptop.getMemory()))
                .param("description", laptop.getDescription()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        // Now we want to verify the returned response body points to a valid product in the database.
        String id = result.getResponse().getContentAsString();
        var filter = new ElectronicProductSearchFilter();
        filter.setId(id);
        JSONObject body = buildJsonObject(filter);
        mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    public void rejectLaptopCreationIfAnyFieldIsMissing() throws Exception {
        Laptop laptop = fixture.generateTestingLaptop();
        laptop.setPhoto(null);
        laptop.setOs(null);
        mvc.perform(MockMvcRequestBuilders.multipart("/create-laptop")
                .param("model", laptop.getModel())
                .param("price", String.valueOf(laptop.getPrice())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void testUpdateLaptop() throws Exception {
        Laptop laptop = getExistingLaptop();
        var photo = new MockMultipartFile("photo", "mocked-photo.png",
                MediaType.TEXT_PLAIN_VALUE, laptop.getPhoto());
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .multipart("/update-laptop/" + laptop.getId())
                .file(photo)
                .param("model", laptop.getModel() + " Pro Max")
                .param("brand", String.valueOf(laptop.getBrand()))
                .param("price", String.valueOf(laptop.getPrice() + 400))
                .param("os", String.valueOf(DesktopOS.Windows11))
                .param("memory", String.valueOf(laptop.getMemory() + 8))
                .param("description", laptop.getDescription()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Similarly to the test where we created a laptop above, we need to make
        // sure the returned id points to a valid laptop object in the database.
        String id = result.getResponse().getContentAsString();
        var filter = new ElectronicProductSearchFilter();
        filter.setId(id);
        JSONObject body = buildJsonObject(filter);
        mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    public void rejectLaptopUpdateIfAnyFieldsAreMissing() throws Exception {
        Laptop laptop = getExistingLaptop();
        laptop.setPhoto(null);
        laptop.setOs(null);
        mvc.perform(MockMvcRequestBuilders.multipart("/update-laptop/" + laptop.getId())
                        .param("model", laptop.getModel())
                        .param("price", String.valueOf(laptop.getPrice())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testDeleteLaptop() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/get-random-laptops")).andReturn();
        JSONArray laptops = buildJsonArray(result);
        var target = (JSONObject)laptops.get(0);
        String deletedId = target.getString("id");
        mvc.perform(MockMvcRequestBuilders.delete("/delete-laptop/" + deletedId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        //Finally, we check if the laptop under the same id still exists after we deleted it.
        var filter = new ElectronicProductSearchFilter();
        filter.setId(deletedId);
        JSONObject body = buildJsonObject(filter);
        result = mvc.perform(MockMvcRequestBuilders.post("/get-products")
                .content(body.toString()).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        laptops = buildJsonArray(result);
        for (int index = 0; index < laptops.length(); ++index) {
            var laptop = (JSONObject)laptops.get(index);
            String givenId = laptop.getString("id");
            assertNotEquals(deletedId, givenId);
        }
    }

    private Laptop getExistingLaptop() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/get-random-laptops"))
                .andReturn();
        var mapper = new ObjectMapper();
        JSONArray laptops = buildJsonArray(result);
        String laptopJsonString = laptops.getString(0);
        return mapper.readValue(laptopJsonString, Laptop.class);
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