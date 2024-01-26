package com.royal.products.domain.characteristics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.royal.products.domain.characteristics.candidates.DesktopBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.DesktopOperatingSystemCharacteristic;
import com.royal.products.domain.characteristics.candidates.MobileBrandCharacteristic;
import com.royal.products.domain.characteristics.candidates.MobileOperatingSystemCharacteristic;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonDeserialize(using = CharacteristicDeserializer.class)
@JsonSubTypes({
        @JsonSubTypes.Type(DesktopBrandCharacteristic.class),
        @JsonSubTypes.Type(DesktopOperatingSystemCharacteristic.class),
        @JsonSubTypes.Type(MobileBrandCharacteristic.class),
        @JsonSubTypes.Type(MobileOperatingSystemCharacteristic.class)
})
public interface Characteristic<T> {
    CharacteristicKey getKey();
    T getValue();

    void setKey(CharacteristicKey key);
    void setValue(T value);

    String toJson();
}
