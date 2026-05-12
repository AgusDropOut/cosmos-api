package dev.cosmos.api.entity;

import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.resources.ResourceLocation;

public class CosmosRenderLayer {
    public final ResourceLocation id;
    public final CosmosMaterialInstance material;

    public CosmosRenderLayer(ResourceLocation id, CosmosMaterialInstance material) {
        this.id = id;
        this.material = material;
    }
}