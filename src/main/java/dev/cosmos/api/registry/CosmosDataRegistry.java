package dev.cosmos.api.registry;

import dev.cosmos.api.data.ICosmosDataHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CosmosDataRegistry {

    private static final Map<String, ICosmosDataHandler> HANDLERS = new HashMap<>();

    public static void register(String type, ICosmosDataHandler handler) {
        HANDLERS.put(type, handler);
    }

    public static ICosmosDataHandler getHandler(String type) {
        return HANDLERS.get(type);
    }

    public static Collection<ICosmosDataHandler> getAllHandlers() {
        return HANDLERS.values();
    }
}