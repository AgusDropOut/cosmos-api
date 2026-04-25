package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCosmosBeamEntity extends Entity implements ICosmosBeam {

    private final CosmosBeamState beamState;

    public AbstractCosmosBeamEntity(EntityType<?> type, Level level, CosmosBeamState beamState) {
        super(type, level);
        this.beamState = beamState;
    }

    @Override
    public CosmosBeamState getBeamState() {
        return this.beamState;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.beamState.tickLerp(this.getTargetPosition());
        }
    }


    public abstract Vec3 getTargetPosition();
}