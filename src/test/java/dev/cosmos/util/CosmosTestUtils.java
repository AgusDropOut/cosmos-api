package dev.cosmos.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods for the Cosmos test suite.
 */
public class CosmosTestUtils {

    private static final Gson GSON = new Gson();

    /**
     * Loads a JSON fixture from the src/test/resources directory.
     *
     * @param path The absolute path to the resource (e.g., "/fixtures/valid_beam.beam.csm.json")
     * @return The parsed JsonObject
     * @throws RuntimeException if the file is missing or fails to parse
     */
    public static JsonObject loadJsonFixture(String path) {
        InputStream stream = CosmosTestUtils.class.getResourceAsStream(path);

        if (stream == null) {
            throw new RuntimeException("Test fixture not found at path: " + path);
        }

        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON fixture at path: " + path, e);
        }
    }

    /**
     * Provides access to the shared test Gson instance.
     */
    public static Gson getGson() {
        return GSON;
    }
}