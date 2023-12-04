package com.royal.models.products;

import com.royal.models.products.enumerations.MobileBrand;
import com.royal.models.products.enumerations.MobileOS;
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
    private MobileBrand brand;
    private float price;
    private byte[] photo;
    private MobileOS os;
    private int memory;
    private String description;
    private long itemsInStock = 1;

    public String toString() {
        return model + " #" + id + " by " + brand +  " running " + os +  ": " + price + "$, "
                + memory + "MB. Description: " + description;
    }
}
