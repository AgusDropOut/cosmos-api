package dev.cosmos.api.material;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.impl.data.handler.MaterialDataHandler;
import dev.cosmos.util.CosmosTestUtils;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CosmosMaterialInstanceTest {
    private final Gson gson = CosmosTestUtils.getGson();
    private MaterialDataHandler handler;

    @BeforeEach
    public void setup() {
        handler = new MaterialDataHandler();
        handler.clear();
    }

    @Test
    public void nonValidResourceLocation_shouldThrowException(){

        assertThrows(IllegalArgumentException.class, () ->{
            CosmosMaterialInstance mat = new CosmosMaterialInstance(new ResourceLocation(Cosmos.MODID, "invalid_mat"));
        });
    }


    @Test
    public void validExporsedParamters_shouldHaveThemAll(){


        JsonObject json = CosmosTestUtils.loadJsonFixture("/fixtures/valid_mat.mat.csm.json");
        ResourceLocation id =new ResourceLocation(Cosmos.MODID, "valid_mat");
        handler.handle(id, json,gson);
        CosmosMaterialInstance mat = new CosmosMaterialInstance(new ResourceLocation("cosmos", "valid_mat"));


        mat.setVec3("u_color_1" , 0.5F , 0.5F, 0.5F);
        mat.setVec3("u_color_2" , 0.1F , 0.1F, 0.1F);

        Map<String, Object> expectedResult = Map.of("u_color_1" , new Vector3f(0.5F,0.5F,0.5F), "u_color_2" , new Vector3f(0.1F,0.1F,0.1F) );

        Map<String, Object> result = mat.getValues();

        assertTrue(result.equals(expectedResult), "The exposed parameters were no saved properly");
    }
}
