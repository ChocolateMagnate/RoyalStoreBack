package com.royal.products.domain.requests;

import com.royal.products.domain.ProductCategory;
import com.royal.products.domain.characteristics.CharacteristicsSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchedElectronicProductRequest {
    private String id;
    private String model;
    private Float price;
    private byte[] photo;
    private Integer memory;
    private Integer storage;
    private String description;
    private ProductCategory category;
    private CharacteristicsSet characteristics;
}
