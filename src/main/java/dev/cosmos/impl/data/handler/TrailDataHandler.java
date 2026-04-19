package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.ICosmosDataHandler;
import dev.cosmos.api.data.TrailDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TrailDataHandler implements ICosmosDataHandler {


    public static final Map<ResourceLocation, TrailDefinition> TRAILS = new HashMap<>();

    @Override
    public void clear() {
        TRAILS.clear();
    }

    @Override
    public void handle(ResourceLocation resourceId, JsonObject json, Gson gson) {
        TrailDefinition trailDef = gson.fromJson(json, TrailDefinition.class);
        trailDef.compileExpressions();

        TRAILS.put(resourceId, trailDef);
        Cosmos.LOGGER.info("Cosmos API: Loaded Trail [{}]", resourceId);
    }
}