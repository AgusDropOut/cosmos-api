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

    public static class BeamLayer {
        public final ResourceLocation beamId;
        public final CosmosMaterialInstance material;

        public BeamLayer(ResourceLocation beamId, CosmosMaterialInstance material) {
            this.beamId = beamId;
            this.material = material;
        }
    }

    private final List<BeamLayer> layers;
    private final double lerpFactor;

    private CosmosBeamState(List<BeamLayer> layers, double lerpFactor) {
        this.layers = new ArrayList<>(layers);
        this.lerpFactor = lerpFactor;
    }

    public List<BeamLayer> getLayers() { return this.layers; }

    public void tickLerp(Vec3 syncedTarget) {
        if (this.clientPreviousEndpoint.equals(Vec3.ZERO)) {
            this.clientPreviousEndpoint = syncedTarget;
            this.clientCurrentEndpoint = syncedTarget;
        } else {
            this.clientPreviousEndpoint = this.clientCurrentEndpoint;
            this.clientCurrentEndpoint = this.clientCurrentEndpoint.lerp(syncedTarget, this.lerpFactor);
        }
    }

    // BUILDER PATTERN
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BeamLayer> pendingLayers = new ArrayList<>();
        private double lerpFactor = 0.5; // Default fallback

        public Builder addBeam(ResourceLocation beamId) {
            this.pendingLayers.add(new BeamLayer(beamId, null));
            return this;
        }

        public Builder addBeam(ResourceLocation beamId, CosmosMaterialInstance materialOverride) {
            this.pendingLayers.add(new BeamLayer(beamId, materialOverride));
            return this;
        }

        public Builder setLerpFactor(double lerpFactor) {
            this.lerpFactor = lerpFactor;
            return this;
        }

        public CosmosBeamState build() {
            if (this.pendingLayers.isEmpty()) {
                throw new IllegalStateException("Cosmos API Error: A Beam Entity was created without any beams!");
            }

            List<BeamLayer> finalizedLayers = new ArrayList<>();

            for (BeamLayer layer : this.pendingLayers) {
                if (layer.material != null) {
                    finalizedLayers.add(layer);
                } else {
                    BeamDefinition def = BeamDataHandler.BEAMS.get(layer.beamId);
                    if (def != null && def.config != null && def.config.materialId != null) {
                        CosmosMaterialInstance autoMat = new CosmosMaterialInstance(new ResourceLocation(def.config.materialId));
                        finalizedLayers.add(new BeamLayer(layer.beamId, autoMat));
                    } else {
                        throw new IllegalStateException("Cosmos API: Cannot auto-resolve material for beam '" + layer.beamId + "'");
                    }
                }
            }

            return new CosmosBeamState(finalizedLayers, this.lerpFactor);
        }
    }
}