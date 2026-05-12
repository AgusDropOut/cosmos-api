package dev.cosmos.api.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * The core interface for parsing and registering Cosmos JSON metadata.
 * Modders can implement this interface and register it via {@link dev.cosmos.api.registry.CosmosDataRegistry}
 * to handle completely custom ".csm.json" data types.
 */
public interface ICosmosDataHandler {

    /**
     * Called automatically when resources are reloaded (e.g., F3+T or joining a world).
     * Implementations MUST clear their internal caches/maps here to prevent memory leaks.
     */
    void clear();

    /**
     * Parses the incoming JSON and stores the resulting definition.
     * * @param resourceId The unique ID extracted from the "namespace" and "id" fields of the JSON.
     * @param json The raw JSON object parsed from the ".csm.json" file.
     * @param gson The global Gson instance to map the JSON to your definition class.
     */
    void handle(ResourceLocation resourceId, JsonObject json, Gson gson);
}