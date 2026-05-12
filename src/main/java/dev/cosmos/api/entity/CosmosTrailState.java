package dev.cosmos.api.entity;

import dev.cosmos.api.data.TrailDefinition;
import dev.cosmos.api.material.CosmosMaterialInstance;
import dev.cosmos.impl.data.handler.TrailDataHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CosmosTrailState {

    private final Deque<Vec3> history = new ArrayDeque<>();
    private final int maxHistory;
    private final List<CosmosRenderLayer> layers;

    private CosmosTrailState(int maxHistory, List<CosmosRenderLayer> layers) {
        this.maxHistory = maxHistory;
        this.layers = new ArrayList<>(layers);
    }

    public Deque<Vec3> getHistory() { return this.history; }
    public List<CosmosRenderLayer> getLayers() { return this.layers; }

    public void tickHistory(Vec3 currentPos) {
        this.history.addFirst(currentPos);
        if (this.history.size() > this.maxHistory) {
            this.history.removeLast();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractLayeredBuilder<Builder, CosmosTrailState> {
        private int maxHistory = 20;

        public Builder setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
            return this;
        }

        public Builder addTrail(ResourceLocation trailId) { return this.addLayer(trailId); }
        public Builder addTrail(ResourceLocation trailId, CosmosMaterialInstance materialOverride) { return this.addLayer(trailId, materialOverride); }

        @Override
        protected ResourceLocation autoResolveMaterial(ResourceLocation id) {
            TrailDefinition def = TrailDataHandler.TRAILS.get(id);
            if (def != null && def.config != null && def.config.materialId != null) {
                return new ResourceLocation(def.config.materialId);
            }
            return null;
        }

        @Override
        public CosmosTrailState build() {
            return new CosmosTrailState(this.maxHistory, this.resolveLayers("Trail"));
        }
    }
}