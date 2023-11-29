package com.royal.models.products;

import com.royal.models.products.enumerations.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Document(collection = "smartphones")
public class Smartphone implements ElectronicProduct {
    @Id
    private String id;
    private float price;
    private int memory;
    private int storage;
    private int weight;
    private Processor processor;
    private BuildMaterial material;
    private byte[] photo;
    private String description;
    private MobileBrand brand;
    private MobileOS os;
    private Float diagonal;
    private Pair<Integer, Integer> resolution;
    private Integer batteryCapacityMAH;
    private ArrayList<Connectivity> connectivities;

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> descriptor = new HashMap<>(13);
        descriptor.put("battery-capacity", batteryCapacityMAH);
        descriptor.put("connectivities", connectivities);
        descriptor.put("description", description);
        descriptor.put("resolution", resolution);
        descriptor.put("processor", processor);
        descriptor.put("diagonal", diagonal);
        descriptor.put("material", material);
        descriptor.put("storage", storage);
        descriptor.put("memory", memory);
        descriptor.put("weight", weight);
        descriptor.put("brand", brand);
        descriptor.put("price", price);
        descriptor.put("photo", photo);
        descriptor.put("os", os);
        descriptor.put("id", id);
        return descriptor;
    }
}
