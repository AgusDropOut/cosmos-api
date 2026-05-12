package dev.cosmos.api.entity;

import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts the common behavior for building layered Cosmos VFX entities.
 * Handles adding layers, material overrides, and auto-resolution from JSON definitions.
 */
public abstract class AbstractLayeredBuilder<B extends AbstractLayeredBuilder<B, S>, S> {
    protected final List<PendingLayer> pendingLayers = new ArrayList<>();

    protected static class PendingLayer {
        public final ResourceLocation id;
        public final CosmosMaterialInstance materialOverride;

        public PendingLayer(ResourceLocation id, CosmosMaterialInstance materialOverride) {
            this.id = id;
            this.materialOverride = materialOverride;
        }
    }



    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    public B addLayer(ResourceLocation id) {
        this.pendingLayers.add(new PendingLayer(id, null));
        return self();
    }

    public B addLayer(ResourceLocation id, CosmosMaterialInstance materialOverride) {
        this.pendingLayers.add(new PendingLayer(id, materialOverride));
        return self();
    }

    /**
     * Transforms pending layers into finalized render layers.
     * Skips auto-resolution if a material override is present.
     */
    protected List<CosmosRenderLayer> resolveLayers(String entityType) {
        if (this.pendingLayers.isEmpty()) {
            throw new IllegalStateException("Cosmos API Error: A " + entityType + " Entity was created without any layers!");
        }

        List<CosmosRenderLayer> finalizedLayers = new ArrayList<>();

        for (PendingLayer layer : this.pendingLayers) {
            if (layer.materialOverride != null) {
                finalizedLayers.add(new CosmosRenderLayer(layer.id, layer.materialOverride));
            } else {
                ResourceLocation autoMatId = this.autoResolveMaterial(layer.id);
                if (autoMatId != null) {
                    CosmosMaterialInstance autoMat = new CosmosMaterialInstance(autoMatId);
                    finalizedLayers.add(new CosmosRenderLayer(layer.id, autoMat));
                } else {
                    throw new IllegalStateException("Cosmos API: Cannot auto-resolve material for " + entityType + " '" + layer.id + "'");
                }
            }
        }

        return finalizedLayers;
    }

    /**
     * Resolves the Material ID specified in the JSON definition.
     */
    protected abstract ResourceLocation autoResolveMaterial(ResourceLocation id);

    public abstract S build();
}