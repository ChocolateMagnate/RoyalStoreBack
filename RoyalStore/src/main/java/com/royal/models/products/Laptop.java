package com.royal.models.products;

import com.royal.models.products.enumerations.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "laptops")
public class Laptop implements ElectronicProduct {
    @Id
    private String id;
    private float price;
    private int memory;
    private int storage;
    private int weight;
    private Processor processor;
    private BuildMaterial buildMaterial;
    private byte[] photo;
    private String description;
    private DesktopBrand brand;
    private DesktopOS os;
    private GraphicsCard card;
    private Float diagonal;
    private Pair<Integer, Integer> resolution;
    private Set<Connectivity> connectivities;

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> descriptor = new HashMap<>(13);
        descriptor.put("connectivities", connectivities);
        descriptor.put("resolution", resolution);
        descriptor.put("diagonal", diagonal);
        descriptor.put("brand", brand);
        descriptor.put("card", card);
        return descriptor;
    }
}
