package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public class CosmosBeamState {
    public Vec3 clientPreviousEndpoint = Vec3.ZERO;
    public Vec3 clientCurrentEndpoint = Vec3.ZERO;

    private final List<ResourceLocation> beamIds;
    private final double lerpFactor;

    private CosmosBeamState(List<ResourceLocation> beamIds, double lerpFactor) {
        this.beamIds = new ArrayList<>(beamIds);
        this.lerpFactor = lerpFactor;
    }

    public List<ResourceLocation> getBeamIds() { return this.beamIds; }

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
        private final List<ResourceLocation> beamIds = new ArrayList<>();
        private double lerpFactor = 0.5; // Default fallback

        public Builder addBeam(ResourceLocation id) {
            if (!this.beamIds.contains(id)) {
                this.beamIds.add(id);
            }
            return this;
        }

        public Builder setLerpFactor(double lerpFactor) {
            this.lerpFactor = lerpFactor;
            return this;
        }

        public CosmosBeamState build() {
            if (this.beamIds.isEmpty()) {
                throw new IllegalStateException("Cosmos API Error: A Beam Entity was created without a Beam ID!");
            }
            return new CosmosBeamState(this.beamIds, this.lerpFactor);
        }
    }
}