package com.royal.products.domain.characteristics.candidates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicKey;
import com.royal.products.domain.characteristics.specifiers.MobileBrand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = MobileBrandCharacteristic.class)
public class MobileBrandCharacteristic implements Characteristic<MobileBrand> {
    private CharacteristicKey key = CharacteristicKey.MobileBrand;
    private MobileBrand mobileBrand = MobileBrand.Samsung;

    public MobileBrandCharacteristic(MobileBrand brand) {
        this.mobileBrand = brand;
    }

    @Override
    public MobileBrand getValue() {
        return this.mobileBrand;
    }

    @Override
    public void setValue(MobileBrand value) {
        this.mobileBrand = value;
    }

    @Override
    public String toJson() {
        return "{\"key\": \"" + key + "\", \"mobileBrand\": \"" + mobileBrand + "\"},";
    }
}
