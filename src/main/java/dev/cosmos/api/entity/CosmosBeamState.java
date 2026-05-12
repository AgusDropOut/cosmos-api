package dev.cosmos.api.entity;

import dev.cosmos.api.data.BeamDefinition;
import dev.cosmos.api.material.CosmosMaterialInstance;
import dev.cosmos.impl.data.handler.BeamDataHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

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

    public static class Builder extends AbstractLayeredBuilder<Builder, CosmosBeamState> {
        private double lerpFactor = 0.5;

        public Builder addBeam(ResourceLocation beamId) { return this.addLayer(beamId); }
        public Builder addBeam(ResourceLocation beamId, CosmosMaterialInstance materialOverride) { return this.addLayer(beamId, materialOverride); }

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