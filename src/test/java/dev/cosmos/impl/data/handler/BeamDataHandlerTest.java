package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.util.CosmosTestUtils;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeamDataHandlerTest {

    private final Gson gson = CosmosTestUtils.getGson();
    private BeamDataHandler handler;

    @BeforeEach
    public void setup() {
        handler = new BeamDataHandler();
        handler.clear();
    }


    @Test
    public void testValidBeamJsonParsing() {

        JsonObject jsonObject = CosmosTestUtils.loadJsonFixture("/fixtures/valid_beam.beam.csm.json");

        ResourceLocation id = new ResourceLocation("cosmos", "valid_beam");

        handler.handle(id, jsonObject, gson);
        assertTrue(BeamDataHandler.BEAMS.containsKey(id), "Beam was not registered in the map");

        var definition = BeamDataHandler.BEAMS.get(id);
        assertNotNull(definition, "Definition should not be null");

        //  Assert basic properties
        assertEquals(8, definition.config.radialSegments, "Radial segments failed to parse");
        assertEquals(40, definition.config.lengthSegments, "Length segments failed to parse"); // Added length segments
        assertEquals("cosmos:valid_mat", definition.config.materialId, "Material ID failed to parse");


        float t = 0.0f;
        float v = 0.1f;

        float expectedRadiusAndX = (float) Math.sin((v * 10.0f) - (t * 5.0f)) * 1.5f;
        float expectedZ = (float) Math.cos((v * 10.0f) - (t * 5.0f)) * 1.5f;


        assertEquals(expectedRadiusAndX, definition.compiledRadius.evaluate(t, v), 0.001f, "Radius expression failed to compile/evaluate");


        assertEquals(expectedRadiusAndX, definition.compiledOffsetX.evaluate(t, v), 0.001f, "Offset X expression failed to compile/evaluate");
        assertEquals(0.0f, definition.compiledOffsetY.evaluate(t, v), 0.001f, "Offset Y should evaluate to exactly 0.0");
        assertEquals(expectedZ, definition.compiledOffsetZ.evaluate(t, v), 0.001f, "Offset Z expression failed to compile/evaluate");
    }
}