package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.util.CosmosTestUtils;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialDataHandlerTest {

    private final Gson gson = CosmosTestUtils.getGson();
    private MaterialDataHandler handler;

    @BeforeEach
    public void setup() {
        handler = new MaterialDataHandler();
        handler.clear();
    }

    @Test
    public void testValidMaterialJsonParsing() {
        JsonObject jsonObject = CosmosTestUtils.loadJsonFixture("/fixtures/valid_mat.mat.csm.json");
        ResourceLocation id = new ResourceLocation("cosmos", "valid_mat");

        handler.handle(id, jsonObject, gson);

        // Verify successful registration
        assertTrue(MaterialDataHandler.MATERIALS.containsKey(id), "Material was not registered in the map");

        var definition = MaterialDataHandler.MATERIALS.get(id);
        assertNotNull(definition, "Definition should not be null");
        assertNotNull(definition.config, "Config should not be null");

        // Verify Exposed Parameters (Map)
        assertNotNull(definition.config.exposedParameters, "Exposed parameters should not be null");
        assertEquals(2, definition.config.exposedParameters.size(), "Should have exactly 2 exposed parameters");
        assertEquals("vec3", definition.config.exposedParameters.get("u_color_1"), "u_color_1 type mismatch");
        assertEquals("vec3", definition.config.exposedParameters.get("u_color_2"), "u_color_2 type mismatch");

        // Render State Configuration
        assertNotNull(definition.config.renderState, "Render state should not be null");
        assertEquals("TRANSLUCENT", definition.config.renderState.blendMode, "Blend mode mismatch");
        assertEquals("BACK", definition.config.renderState.cullMode, "Cull mode mismatch");
        assertEquals("ALWAYS", definition.config.renderState.depthTest, "Depth test mismatch");
        assertTrue(definition.config.renderState.depthWrite, "Depth write should be true");
        assertEquals(0.65f, definition.config.renderState.alphaCutoff, 0.001f, "Alpha cutoff mismatch");
    }


}