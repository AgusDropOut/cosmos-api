package dev.cosmos.api.entity;

import dev.cosmos.api.data.BeamDefinition;
import dev.cosmos.api.material.CosmosMaterialInstance;
import dev.cosmos.impl.data.handler.BeamDataHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the visual configuration and current interpolation state of a beam entity.
 * Instances of this class are immutable regarding their layer definitions and must be
 * constructed using {@link CosmosBeamState#builder()}.
 */
public class CosmosBeamState {
    public Vec3 clientPreviousEndpoint = Vec3.ZERO;
    public Vec3 clientCurrentEndpoint = Vec3.ZERO;

    private final List<CosmosRenderLayer> layers;
    private final double lerpFactor;

    private CosmosBeamState(List<CosmosRenderLayer> layers, double lerpFactor) {
        this.layers = new ArrayList<>(layers);
        this.lerpFactor = lerpFactor;
    }

    public List<CosmosRenderLayer> getLayers() { return this.layers; }

    public void tickLerp(Vec3 syncedTarget) {
        if (this.clientPreviousEndpoint.equals(Vec3.ZERO)) {
            this.clientPreviousEndpoint = syncedTarget;
            this.clientCurrentEndpoint = syncedTarget;
        } else {
            this.clientPreviousEndpoint = this.clientCurrentEndpoint;
            this.clientCurrentEndpoint = this.clientCurrentEndpoint.lerp(syncedTarget, this.lerpFactor);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs a {@link CosmosBeamState} by assembling render layers and animation parameters.
     */
    public static class Builder extends AbstractLayeredBuilder<Builder, CosmosBeamState> {
        private double lerpFactor = 0.5;

        /**
         * Appends a beam layer using the default material specified in the beam's JSON definition.
         *
         * @param beamId The resource location of the .beam.csm.json definition.
         * @return This builder for method chaining.
         */
        public Builder addBeam(ResourceLocation beamId) { return this.addLayer(beamId); }

        /**
         * Appends a beam layer and overrides the default material specified in the JSON definition
         * with a custom configured material instance.
         *
         * @param beamId           The resource location of the .beam.csm.json definition.
         * @param materialOverride The customized material instance to use for this specific layer.
         * @return This builder for method chaining.
         */
        public Builder addBeam(ResourceLocation beamId, CosmosMaterialInstance materialOverride) { return this.addLayer(beamId, materialOverride); }


        /**
         * Sets the linear interpolation (lerp) factor for the beam's target endpoint.
         * Lower values result in smoother, slower tracking of moving targets.
         *
         * @param lerpFactor A value typically between 0.0 and 1.0. Defaults to 0.5.
         * @return This builder for method chaining.
         */
        public Builder setLerpFactor(double lerpFactor) {
            this.lerpFactor = lerpFactor;
            return this;
        }

        @Override
        protected ResourceLocation autoResolveMaterial(ResourceLocation id) {
            BeamDefinition def = BeamDataHandler.BEAMS.get(id);
            if (def != null && def.config != null && def.config.materialId != null) {
                return new ResourceLocation(def.config.materialId);
            }
            return null;
        }

        @Override
        public CosmosBeamState build() {
            return new CosmosBeamState(this.resolveLayers("Beam"), this.lerpFactor);
        }
    }
}