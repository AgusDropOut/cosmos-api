package dev.cosmos.impl.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.MaterialDefinition;
import dev.cosmos.api.data.TrailDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class CosmosDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "cosmos_data";

    public static final Map<ResourceLocation, MaterialDefinition> MATERIALS = new HashMap<>();
    public static final Map<ResourceLocation, TrailDefinition> TRAILS = new HashMap<>();

    public CosmosDataManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        MATERIALS.clear();
        TRAILS.clear();

        elements.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (!jsonObject.has("type")) return;

                String type = jsonObject.get("type").getAsString();

                if (type.equals("cosmos:material")) {
                    MaterialDefinition mat = GSON.fromJson(jsonElement, MaterialDefinition.class);
                    MATERIALS.put(location, mat);
                } else if (type.equals("cosmos:trail_system")) {
                    TrailDefinition trail = GSON.fromJson(jsonElement, TrailDefinition.class);
                    TRAILS.put(location, trail);
                }
            } catch (Exception e) {
                Cosmos.LOGGER.error("Failed to parse Cosmos data file: {}", location, e);
            }
        });

        Cosmos.LOGGER.info("Loaded {} Cosmos Materials and {} Trail Systems.", MATERIALS.size(), TRAILS.size());
    }
}