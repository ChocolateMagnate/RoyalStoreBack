package com.royal.products.domain;

import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicKey;
import com.royal.products.domain.characteristics.CharacteristicsSet;
import com.royal.products.domain.characteristics.candidates.DesktopBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.domain.characteristics.candidates.MobileBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.MobileOperatingSystemCharacteristic;
import com.royal.products.domain.requests.RawElectronicProductRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class ElectronicProduct {
    @Id
    private String id;
    private Float price;
    private String model;
    private String description;
    private Integer itemsInStock = 1;
    private Integer storage;
    private Integer memory;
    private byte[] photo;
    private ProductCategory category;
    private CharacteristicsSet characteristics = new CharacteristicsSet();

    public ElectronicProduct(@NotNull ElectronicProduct other) {
        this.id = other.id;
        this.price = other.price;
        this.model = other.model;
        this.description = other.description;
        this.itemsInStock = other.itemsInStock;
        this.storage = other.storage;
        this.memory = other.memory;
        this.photo = other.photo;
        this.category = other.category;
        this.characteristics = other.characteristics;
    }

    public ElectronicProduct(@NotNull LinkedHashMap<String, Object> content) {
        this.id = content.get("id").toString();
        this.price = Float.parseFloat(content.get("price").toString());
        this.model = content.get("model").toString();
        this.description = content.get("description").toString();
        this.storage = Integer.parseInt(content.get("storage").toString());
        this.memory = Integer.parseInt(content.get("memory").toString());
        this.photo = (byte[]) content.get("photo");
        this.category = ProductCategory.valueOf(content.get("category").toString());
        Characteristic<?> brand = (this.category == ProductCategory.Laptop)
                ? new DesktopBrandCharacteristic()
                : new MobileBrandCharacteristic();
        Characteristic<?> os = (this.category == ProductCategory.Laptop)
                ? new DesktopOperatingSystemCharacteristic()
                : new MobileOperatingSystemCharacteristic();
        this.characteristics.add(brand);
        this.characteristics.add(os);
    }

    public static @NotNull ElectronicProduct build(@NotNull RawElectronicProductRequest representation) throws IOException, IllegalArgumentException {
        if (representation.containsNullFields()) throw new IllegalArgumentException(
                "Electric product requests cannot contain null fields: " + representation.getNullFields());

        var patchedRequest = representation.toPatchedRequest();
        var product = new ElectronicProduct();
        product.setId(patchedRequest.getId());
        product.setPrice(patchedRequest.getPrice());
        product.setModel(patchedRequest.getModel());
        product.setDescription(patchedRequest.getDescription());
        product.setStorage(patchedRequest.getStorage());
        product.setMemory(patchedRequest.getMemory());
        product.setPhoto(patchedRequest.getPhoto());
        product.setCategory(patchedRequest.getCategory());
        product.setCharacteristics(patchedRequest.getCharacteristics());
        return product;
    }

    public static @NotNull ElectronicProduct yieldValidProductUsing(@NotNull ElectronicProduct newer, @NotNull ElectronicProduct older) {
        Float price = newer.getPrice() != null ? newer.getPrice() : older.getPrice();
        String model = newer.getModel() != null ? newer.getModel() : older.getModel();
        String description = newer.getDescription() != null ? newer.getDescription() : older.getDescription();
        Integer storage = newer.getStorage() != null ? newer.getStorage() : older.getStorage();
        Integer memory = newer.getMemory() != null ? newer.getMemory() : older.getMemory();
        byte[] photo = newer.getPhoto() != null ? newer.getPhoto() : older.getPhoto();
        Integer itemsInStock = newer.getItemsInStock() != null ? newer.getItemsInStock() : older.getItemsInStock();
        ProductCategory category = newer.getCategory() != null ? newer.getCategory() : older.getCategory();
        CharacteristicsSet characteristics = newer.getCharacteristics() != null
                ? newer.getCharacteristics() : older.getCharacteristics();

        var validProduct = new ElectronicProduct();
        validProduct.setId(older.getId());
        validProduct.setPrice(price);
        validProduct.setModel(model);
        validProduct.setDescription(description);
        validProduct.setStorage(storage);
        validProduct.setMemory(memory);
        validProduct.setPhoto(photo);
        validProduct.setCategory(category);
        validProduct.setItemsInStock(itemsInStock);
        validProduct.setCharacteristics(characteristics);
        return validProduct;
    }

    public Characteristic<?> getCharacteristicByKey(@NotNull GenericProductProperty category) throws NoSuchElementException {
        CharacteristicKey concreteKey = switch (category) {
            case Brand -> (this.category == ProductCategory.Laptop) ? CharacteristicKey.DesktopBrand
                    : CharacteristicKey.MobileBrand;
            case OperatingSystem -> (this.category == ProductCategory.Laptop)
                    ? CharacteristicKey.DesktopOperatingSystem : CharacteristicKey.MobileOperatingSystem;
        };
        return getCharacteristicByKey(concreteKey);
    }

    @Contract(pure = true)
    public Characteristic<?> getCharacteristicByKey(CharacteristicKey key) throws NoSuchElementException {
        for (Characteristic<?> characteristic : this.characteristics)
            if (characteristic.getKey() == key) return characteristic;
        throw new NoSuchElementException("No characteristic under the key " + key);
    }

    public void addCharacteristic(Characteristic<?> characteristic) {
        this.characteristics.add(characteristic);
    }

    public void removeCharacteristic(Characteristic<?> characteristic) {
        this.characteristics.remove(characteristic);
    }
}
