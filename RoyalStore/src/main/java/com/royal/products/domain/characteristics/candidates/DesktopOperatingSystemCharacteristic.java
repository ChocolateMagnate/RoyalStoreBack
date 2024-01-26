package com.royal.products.domain.characteristics.candidates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicKey;
import com.royal.products.domain.characteristics.specifiers.DesktopOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = DesktopOperatingSystemCharacteristic.class)
public class DesktopOperatingSystemCharacteristic implements Characteristic<DesktopOS> {
    private CharacteristicKey key = CharacteristicKey.DesktopOperatingSystem;
    private DesktopOS desktopOS = DesktopOS.Windows10;

    public DesktopOperatingSystemCharacteristic(DesktopOS os) {
        this.desktopOS = os;
    }

    @Override
    public DesktopOS getValue() {
        return this.desktopOS;
    }

    @Override
    public void setValue(DesktopOS value) {
        this.desktopOS = value;
    }

    @Override
    public String toJson() {
        return "{\"key\": \"" + key + "\", \"desktopOs\": \"" + desktopOS + "\"},";
    }
}
