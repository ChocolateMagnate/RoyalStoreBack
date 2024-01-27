package com.royal.products.domain.characteristics;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class CharacteristicDeserializer extends JsonDeserializer<CharacteristicsSet> {
    @Override
    public CharacteristicsSet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // Object mapper can convert JSON strings into Java objects if they
        // have the same property naming and mirror their structure.
        return new ObjectMapper().readValue(jsonParser, CharacteristicsSet.class);
    }
}
