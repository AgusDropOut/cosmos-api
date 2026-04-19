package dev.cosmos.api.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface ICosmosDataHandler {
    void clear();
    void handle(ResourceLocation resourceId, JsonObject json, Gson gson);
}