package dev.cosmos.api.entity;

import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts the common behavior for building layered Cosmos VFX entities.
 * Handles the accumulation of structural layers, optional material overrides,
 * and automatic material resolution from JSON metadata.
 *
 * @param <B> The concrete builder type for fluent method chaining.
 * @param <S> The final state object produced by the builder.
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


    /**
     * Queues a visual layer to be added to the entity, utilizing the default material.
     *
     * @param id The resource location of the visual definition (e.g., beam or trail).
     * @return The builder instance.
     */
    public B addLayer(ResourceLocation id) {
        this.pendingLayers.add(new PendingLayer(id, null));
        return self();
    }


    /**
     * Queues a visual layer to be added to the entity with a custom material override.
     *
     * @param id               The resource location of the visual definition.
     * @param materialOverride The configured material instance to apply to this layer.
     * @return The builder instance.
     */
    public B addLayer(ResourceLocation id, CosmosMaterialInstance materialOverride) {
        this.pendingLayers.add(new PendingLayer(id, materialOverride));
        return self();
    }

    /**
     * Transforms pending layers into finalized render layers during the build process.
     * Skips auto-resolution if a material override was explicitly provided.
     *
     * @param entityType Used for contextual error logging (e.g., "Beam" or "Trail").
     * @return A finalized list of layers ready for rendering.
     * @throws IllegalStateException If no layers were added, or if a default material could not be resolved.
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
    public abstract ResourceLocation autoResolveMaterial(ResourceLocation id);


    /**
     * Finalizes the configuration and produces the immutable state object.
     */
    public abstract S build();
}