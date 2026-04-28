package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.ICosmosDataHandler;
import dev.cosmos.api.data.MaterialDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MaterialDataHandler implements ICosmosDataHandler {


    public static final Map<ResourceLocation, MaterialDefinition> MATERIALS = new HashMap<>();

    @Override
    public void clear() {
        MATERIALS.clear();
    }

    @Override
    public void handle(ResourceLocation resourceId, JsonObject json, Gson gson) {
        MaterialDefinition def = gson.fromJson(json, MaterialDefinition.class);
        MATERIALS.put(resourceId, def);
        Cosmos.LOGGER.info("Cosmos API: Loaded Material [{}]", resourceId);
    }
}