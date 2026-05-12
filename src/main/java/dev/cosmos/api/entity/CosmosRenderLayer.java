package dev.cosmos.api.entity;

import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a finalized, renderable layer pairing a structural definition with a material instance.
 */
public class CosmosRenderLayer {
    public final ResourceLocation id;
    public final CosmosMaterialInstance material;

    public CosmosRenderLayer(ResourceLocation id, CosmosMaterialInstance material) {
        this.id = id;
        this.material = material;
    }
}