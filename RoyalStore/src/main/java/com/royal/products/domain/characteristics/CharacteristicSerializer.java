package com.royal.products.domain.characteristics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class CharacteristicSerializer extends JsonSerializer<CharacteristicsSet> {
    @Override
    public void serialize(CharacteristicsSet characteristics, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Characteristic<?> characteristic : characteristics)
            jsonGenerator.writeRawValue(characteristic.toJson());
        jsonGenerator.writeEndArray();
    }
}
