package com.payment.processing.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TestUtil {
    private static final ClassLoader classLoader = TestUtil.class.getClassLoader();

    public static <T> T readJson(String fiLePath, Class<T> clazz) {
        try {
            return createObjectMapper().readValue(getResource(fiLePath), clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T readJsonList(String filePath, Class<T> listElementClazz) {
        try {
            final CollectionType collectionType = createObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, listElementClazz);
            return createObjectMapper().readValue(getResource(filePath), collectionType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeJson(Object object, String filePath) {
        try {
            final String json = createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
            final Path path = Path.of("src/test/resources/" + filePath);
            Files.write(path, json.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    private static URL getResource(String filePath) {
        return classLoader.getResource(filePath);
    }

    private TestUtil() {}
}
