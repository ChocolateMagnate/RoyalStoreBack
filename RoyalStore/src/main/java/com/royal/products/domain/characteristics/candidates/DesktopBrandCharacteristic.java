package com.royal.products.domain.characteristics.candidates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicKey;
import com.royal.products.domain.characteristics.specifiers.DesktopBrand;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonDeserialize(as = DesktopBrandCharacteristic.class)
public class DesktopBrandCharacteristic implements Characteristic<DesktopBrand> {
    private CharacteristicKey key = CharacteristicKey.DesktopBrand;
    private DesktopBrand desktopBrand = DesktopBrand.Lenovo;

    public DesktopBrandCharacteristic(DesktopBrand brand) {
        this.desktopBrand = brand;
    }

    @Override
    public DesktopBrand getValue() {
        return this.desktopBrand;
    }

    @Override
    public void setValue(DesktopBrand value) {
        this.desktopBrand = value;
    }

    @Override
    public String toJson() {
        return "{\"key\": \"" + key + "\", \"desktopBrand\": \"" + desktopBrand + "\"},";
    }

}
