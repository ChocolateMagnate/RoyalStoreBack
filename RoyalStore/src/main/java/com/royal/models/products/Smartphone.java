package com.royal.models.products;

import com.royal.models.products.enumerations.MobileBrand;
import com.royal.models.products.enumerations.MobileOS;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Getter
@Setter
@Nullable
@Document(collection = "smartphones")
public class Smartphone implements ElectronicProduct {
    @Id
    private String id;
    private String model;
    private MobileBrand brand;
    private float price;
    private byte[] photo;
    private MobileOS os;
    private int memory;
    private String description;

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> descriptor = new HashMap<>(13);
        descriptor.put("description", description);
        descriptor.put("memory", memory);
        descriptor.put("brand", brand);
        descriptor.put("price", price);
        descriptor.put("photo", photo);
        descriptor.put("os", os);
        descriptor.put("id", id);
        return descriptor;
    }

    public String toString() {
        return model + " #" + id + " by " + brand +  " running " + os +  ": " + price + "$, "
                + memory + "MB. Description: " + description;
    }
}
