package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.util.CosmosTestUtils;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrailDataHandlerTest {

    private final Gson gson = CosmosTestUtils.getGson();
    private TrailDataHandler handler;

    @BeforeEach
    public void setup() {
        handler = new TrailDataHandler();
        handler.clear();
    }

    @Test
    public void testValidTrailJsonParsing() {

        JsonObject jsonObject = CosmosTestUtils.loadJsonFixture("/fixtures/valid_trail.trail.csm.json");
        ResourceLocation id = new ResourceLocation("cosmos", "valid_trail");


        handler.handle(id, jsonObject, gson);


        assertTrue(TrailDataHandler.TRAILS.containsKey(id), "Trail was not registered in the map");

        var definition = TrailDataHandler.TRAILS.get(id);
        assertNotNull(definition, "Definition should not be null");
        assertNotNull(definition.config, "Config should not be null");


        assertEquals(30, definition.config.historySegments, "History segments failed to parse");
        assertEquals("cosmos:valid_trail", definition.config.materialId, "Material ID failed to parse");

        float t = 1.0f;
        float v = 0.0f;


        float expectedWidth = (float) Math.max(Math.abs(Math.sin(t * 5.0f)), 0.2f);


        float expectedX = (float) Math.sin(t * 10.0f) * 1.5f;
        float expectedY = (float) Math.cos(t * 10.0f) * 1.5f;
        float expectedZ = 0.0f * 1.5f; // 0.0


        assertEquals(expectedWidth, definition.compiledWidth.evaluate(t, v), 0.001f, "Width expression failed to compile/evaluate");
        assertEquals(expectedX, definition.compiledOffsetX.evaluate(t, v), 0.001f, "Offset X expression failed to compile/evaluate");
        assertEquals(expectedY, definition.compiledOffsetY.evaluate(t, v), 0.001f, "Offset Y expression failed to compile/evaluate");
        assertEquals(expectedZ, definition.compiledOffsetZ.evaluate(t, v), 0.001f, "Offset Z expression failed to compile/evaluate");
    }


}