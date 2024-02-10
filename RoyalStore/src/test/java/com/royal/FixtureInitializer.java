package com.royal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.royal.products.ProductFixtureManager;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FixtureInitializer  {
    protected ArrayList<LinkedHashMap<String, Object>> getYamlContents(String resourcePath) throws IOException {
        Yaml yaml = new Yaml();
        ClassLoader loader = ProductFixtureManager.class.getClassLoader();
        try (InputStream consumer = loader.getResourceAsStream(resourcePath)) {
            return yaml.load(consumer);
        }
    }

    protected <T> ArrayList<T> loadObjectsFromFixture(String resourcePath, Class<T> objectType) throws IOException {
        var mapper = new ObjectMapper();
        ArrayList<LinkedHashMap<String, Object>> contents = getYamlContents(resourcePath);
        ArrayList<T> objects = new ArrayList<>(contents.size());
        for (LinkedHashMap<String, Object> content : contents)
            objects.add(mapper.convertValue(content, objectType));
        return objects;
    }
}
