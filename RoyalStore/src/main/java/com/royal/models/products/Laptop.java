package com.royal.models.products;

import com.royal.models.products.enumerations.DesktopBrand;
import com.royal.models.products.enumerations.DesktopOS;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Getter
@Setter
@Document(collection = "laptops")
public class Laptop implements ElectronicProduct {
    @Id
    private String id;
    private String model;
    private DesktopBrand brand;
    private float price;
    private byte[] photo;
    private DesktopOS os;
    private int memory;
    private String description;
    private long itemsInStock = 1;

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> descriptor = new HashMap<>(13);
        descriptor.put("id", id);
        descriptor.put("model", model);
        descriptor.put("brand", brand);
        descriptor.put("price", price);
        descriptor.put("photo", photo);
        descriptor.put("os", os);
        descriptor.put("memory", memory);
        descriptor.put("description", description);
        descriptor.put("itemsInStock", itemsInStock);
        return descriptor;
    }
}
