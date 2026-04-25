package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class CosmosBeamState {
    public Vec3 clientPreviousEndpoint = Vec3.ZERO;
    public Vec3 clientCurrentEndpoint = Vec3.ZERO;
    private final ResourceLocation beamId;

    public CosmosBeamState(ResourceLocation beamId) {
        this.beamId = beamId;
    }

    public ResourceLocation getBeamId() {
        return this.beamId;
    }


    public void tickLerp(Vec3 syncedTarget) {
        if (this.clientPreviousEndpoint.equals(Vec3.ZERO)) {
            this.clientPreviousEndpoint = syncedTarget;
            this.clientCurrentEndpoint = syncedTarget;
        } else {
            this.clientPreviousEndpoint = this.clientCurrentEndpoint;
            this.clientCurrentEndpoint = this.clientCurrentEndpoint.lerp(syncedTarget, 0.5);
        }
    }
}