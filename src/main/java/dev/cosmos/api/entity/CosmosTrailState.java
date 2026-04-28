package dev.cosmos.api.entity;

import dev.cosmos.api.material.CosmosMaterialInstance;
import dev.cosmos.impl.data.handler.TrailDataHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CosmosTrailState {

    public static class TrailLayer {
        public final ResourceLocation trailId;
        public final CosmosMaterialInstance material;

        public TrailLayer(ResourceLocation trailId, CosmosMaterialInstance material) {
            this.trailId = trailId;
            this.material = material;
        }
    }

    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory;
    private final List<TrailLayer> layers;

    private CosmosTrailState(int maxHistory, List<TrailLayer> layers) {
        this.maxHistory = maxHistory;
        this.layers = new ArrayList<>(layers);
    }

    public Deque<Vec3> getHistory() { return this.history; }
    public List<TrailLayer> getLayers() { return this.layers; }

    public void tickHistory(Vec3 currentPos) {
        this.history.addFirst(currentPos);
        if (this.history.size() > this.maxHistory) {
            this.history.removeLast();
        }
    }

    // BUILDER PATTERN
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxHistory = 20; // Default fallback
        private final List<TrailLayer> pendingLayers = new ArrayList<>();

        public Builder setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
            return this;
        }

        /* Two addTrail methods are provided for convenience:
           - The first allows adding a trail by ID, with the material auto-resolved from the trail definition.
           - The second allows specifying a material override directly, bypassing auto-resolution. */
        public Builder addTrail(ResourceLocation trailId) {
            this.pendingLayers.add(new TrailLayer(trailId, null));
            return this;
        }
        public Builder addTrail(ResourceLocation trailId, CosmosMaterialInstance materialOverride) {
            this.pendingLayers.add(new TrailLayer(trailId, materialOverride));
            return this;
        }

        public CosmosTrailState build() {
            if (this.pendingLayers.isEmpty()) {
                throw new IllegalStateException("Cosmos API Error: A Trail Entity was created without any trails!");
            }

            List<TrailLayer> finalizedLayers = new ArrayList<>();

            for (TrailLayer layer : this.pendingLayers) {
                if (layer.material != null) {
                    finalizedLayers.add(layer);
                } else {
                    dev.cosmos.api.data.TrailDefinition def = TrailDataHandler.TRAILS.get(layer.trailId);
                    if (def != null && def.config != null && def.config.materialId != null) {
                        CosmosMaterialInstance autoMat = new CosmosMaterialInstance(new ResourceLocation(def.config.materialId));
                        finalizedLayers.add(new TrailLayer(layer.trailId, autoMat));
                    } else {
                        throw new IllegalStateException("Cosmos API: Cannot auto-resolve material for trail '" + layer.trailId + "'");
                    }
                }
            }

            return new CosmosTrailState(this.maxHistory, finalizedLayers);
        }
    }
}