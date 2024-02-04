package com.royal.products.domain;

import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "laptops")
public class Laptop implements ElectronicProduct {
    @Id
    private String id;
    private String model;
    private String description;
    private DesktopBrand brand;
    private DesktopOS os;
    private float price;
    private int memory;
    private long itemsInStock = 1;
    private byte[] photo;

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
