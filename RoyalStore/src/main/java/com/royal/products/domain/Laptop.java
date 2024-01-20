package com.royal.products.domain;

import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

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

    @Contract(pure = true)
    public Laptop(@NotNull Laptop other) {
        this.id = other.id;
        this.model = other.model;
        this.description = other.description;
        this.brand = other.brand;
        this.os = other.os;
        this.price = other.price;
        this.memory = other.memory;
        this.itemsInStock = other.itemsInStock;
        this.photo = other.photo;
    }

    public Laptop(@NotNull Map<String, Object> contents) {
        this.id = contents.get("id").toString();
        this.model = contents.get("model").toString();
        this.description = contents.get("description").toString().toLowerCase();
        this.brand = DesktopBrand.valueOf(contents.get("brand").toString());
        this.os = DesktopOS.valueOf(contents.get("os").toString());
        this.price = Float.parseFloat(contents.get("price").toString());
        this.memory = (int) contents.get("memory");
    }

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
