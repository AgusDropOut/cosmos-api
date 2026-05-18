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


/**
 * Encapsulates the visual configuration and positional history of a trail entity.
 * Instances of this class are constructed using {@link CosmosTrailState#builder()}.
 */
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


    /**
     * Constructs a {@link CosmosTrailState} by assembling render layers and history parameters.
     */
    public static class Builder extends AbstractLayeredBuilder<Builder, CosmosTrailState> {
        private int maxHistory = 20;


        /**
         * Sets the maximum number of positional segments the trail will remember.
         * This determines the physical length and duration of the trail left behind the entity.
         *
         * @param maxHistory The maximum number of vertices to store. Defaults to 20.
         * @return This builder for method chaining.
         */
        public Builder setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
            return this;
        }


        /**
         * Appends a trail layer using the default material specified in the trail's JSON definition.
         *
         * @param trailId The resource location of the .trail.csm.json definition.
         * @return This builder for method chaining.
         */
        public Builder addTrail(ResourceLocation trailId) { return this.addLayer(trailId); }

        /**
         * Appends a trail layer and overrides the default material specified in the JSON definition
         * with a custom configured material instance.
         *
         * @param trailId          The resource location of the .trail.csm.json definition.
         * @param materialOverride The customized material instance to use for this specific layer.
         * @return This builder for method chaining.
         */
        public Builder addTrail(ResourceLocation trailId, CosmosMaterialInstance materialOverride) { return this.addLayer(trailId, materialOverride); }

        @Override
        public ResourceLocation autoResolveMaterial(ResourceLocation id) {
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