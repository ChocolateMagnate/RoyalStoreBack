package com.royal.products.domain.characteristics.candidates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicKey;
import com.royal.products.domain.characteristics.specifiers.MobileOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = MobileOperatingSystemCharacteristic.class)
public class MobileOperatingSystemCharacteristic implements Characteristic<MobileOS> {
    private CharacteristicKey key = CharacteristicKey.MobileOperatingSystem;
    private MobileOS mobileOS = MobileOS.Android;

    public MobileOperatingSystemCharacteristic(MobileOS os) {
        this.mobileOS = os;
    }

    @Override
    public MobileOS getValue() {
        return this.mobileOS;
    }

    @Override
    public void setValue(MobileOS value) {
        this.mobileOS = value;
    }

    @Override
    public String toJson() {
        return "{\"key\": \"" + key + "\", \"mobileOS\": \"" + mobileOS + "\"},";
    }
}
