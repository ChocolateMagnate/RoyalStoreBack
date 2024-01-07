package com.royal.products.domain;

import com.royal.products.domain.enumerations.MobileBrand;
import com.royal.products.domain.enumerations.MobileOS;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Nullable
@Document(collection = "smartphones")
public class Smartphone implements ElectronicProduct {
    @Id
    private String id;
    private String model;
    private String description;
    private MobileBrand brand;
    private MobileOS os;
    private float price;
    private byte[] photo;
    private int memory;
    private long itemsInStock = 1;

    public String toString() {
        return model + " #" + id + " by " + brand +  " running " + os +  ": " + price + "$, "
                + memory + "MB. Description: " + description;
    }
}
