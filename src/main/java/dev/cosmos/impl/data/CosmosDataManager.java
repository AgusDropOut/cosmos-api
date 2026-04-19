package dev.cosmos.impl.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cosmos.Cosmos;
import dev.cosmos.api.data.ICosmosDataHandler;
import dev.cosmos.api.registry.CosmosDataRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CosmosDataManager implements ResourceManagerReloadListener {

    private static final Gson GSON = new Gson();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        // Clear all registered handlers dynamically
        CosmosDataRegistry.getAllHandlers().forEach(ICosmosDataHandler::clear);

        Map<ResourceLocation, Resource> files = resourceManager.listResources("cosmos_data",
                location -> location.getPath().endsWith(".csm.json"));

        files.forEach((location, resource) -> {
            try (InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                if (!json.has("type")) return;

                String type = json.get("type").getAsString();
                String namespace = json.has("namespace") ? json.get("namespace").getAsString() : Cosmos.MODID;
                String id = json.get("id").getAsString();
                ResourceLocation resourceId = new ResourceLocation(namespace, id);

                // Fetch the handler from our new Registry
                ICosmosDataHandler handler = CosmosDataRegistry.getHandler(type);

                if (handler != null) {
                    handler.handle(resourceId, json, GSON);
                } else {
                    Cosmos.LOGGER.warn("Cosmos API: Unknown data type '{}' in file {}", type, location);
                }

            } catch (Exception e) {
                Cosmos.LOGGER.error("Cosmos API: Failed to parse definition at {}", location, e);
            }
        });
    }
}