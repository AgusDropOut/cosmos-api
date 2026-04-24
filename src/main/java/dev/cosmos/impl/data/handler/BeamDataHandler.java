package dev.cosmos.impl.data.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.BeamDefinition;
import dev.cosmos.api.data.ICosmosDataHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BeamDataHandler implements ICosmosDataHandler {

    public static final Map<ResourceLocation, BeamDefinition> BEAMS = new HashMap<>();

    @Override
    public void clear() {
        BEAMS.clear();
    }

    @Override
    public void handle(ResourceLocation resourceId, JsonObject json, Gson gson) {
        BeamDefinition beamDef = gson.fromJson(json, BeamDefinition.class);
        beamDef.compileExpressions();

        BEAMS.put(resourceId, beamDef);
        Cosmos.LOGGER.info("Cosmos API: Loaded Beam [{}]", resourceId);
    }
}