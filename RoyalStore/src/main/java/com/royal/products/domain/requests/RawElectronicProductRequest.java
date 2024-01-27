package com.royal.products.domain.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.products.domain.ProductCategory;
import com.royal.products.domain.characteristics.CharacteristicsSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawElectronicProductRequest {
    private String id;
    private String model;
    private Float price;
    private MultipartFile photo;
    private Integer memory;
    private Integer storage;
    private String description;
    private ProductCategory category;
    private String characteristics;

    public PatchedElectronicProductRequest toPatchedRequest() throws IOException {
        var patchedRequest = new PatchedElectronicProductRequest();
        patchedRequest.setId(this.id);
        patchedRequest.setModel(this.model);
        patchedRequest.setPrice(this.price);
        patchedRequest.setPhoto(this.photo.getBytes());
        patchedRequest.setMemory(this.memory);
        patchedRequest.setStorage(this.storage);
        patchedRequest.setCategory(this.category);
        patchedRequest.setDescription(this.description);
        patchedRequest.setCharacteristics(new ObjectMapper()
                .readValue(this.characteristics, CharacteristicsSet.class));
        return patchedRequest;
    }


    public boolean containsNullFields() {
        return model == null || price == null || photo == null || memory == null || storage == null
                || description == null || category == null || characteristics == null;
    }

    public ArrayList<String> getNullFields() {
        ArrayList<String> nullFields = new ArrayList<>(10);
        if (this.model == null) nullFields.add("model");
        if (this.price == null) nullFields.add("price");
        if (this.photo == null) nullFields.add("photo");
        if (this.memory == null) nullFields.add("memory");
        if (this.storage == null) nullFields.add("storage");
        if (this.description == null) nullFields.add("description");
        if (this.category == null) nullFields.add("category");
        if (this.characteristics == null) nullFields.add("characteristics");
        return nullFields;
    }

}
