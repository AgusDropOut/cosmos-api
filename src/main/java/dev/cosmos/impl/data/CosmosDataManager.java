package dev.cosmos.impl.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.TrailDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CosmosDataManager implements ResourceManagerReloadListener {

    public static final Map<ResourceLocation, TrailDefinition> TRAILS = new HashMap<>();
    private static final Gson GSON = new Gson();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        TRAILS.clear();

        Map<ResourceLocation, Resource> trailFiles = resourceManager.listResources("cosmos_data",
                location -> location.getPath().endsWith(".trail.csm.json"));

        trailFiles.forEach((location, resource) -> {
            try (InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                if (json.has("type") && json.get("type").getAsString().equals("cosmos:trail_system")) {
                    String namespace = json.get("namespace").getAsString();
                    String id = json.get("id").getAsString();
                    ResourceLocation trailId = new ResourceLocation(namespace, id);

                    TrailDefinition def = GSON.fromJson(json, TrailDefinition.class);
                    TRAILS.put(trailId, def);

                    Cosmos.LOGGER.info("Cosmos API: Loaded Trail Definition [{}]", trailId);
                }
            } catch (Exception e) {
                Cosmos.LOGGER.error("Cosmos API: Failed to parse trail definition at {}", location, e);
            }
        });
    }
}