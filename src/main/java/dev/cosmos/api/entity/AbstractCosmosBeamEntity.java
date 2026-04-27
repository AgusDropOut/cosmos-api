package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCosmosBeamEntity extends Entity implements ICosmosBeam {

    private CosmosBeamState beamState;

    public AbstractCosmosBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.beamState = this.createDefaultState();
    }

    @Override
    public CosmosBeamState getBeamState() {
        return this.beamState;
    }


    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.beamState != null) {
            this.beamState.tickLerp(this.getTargetPosition());
        }
    }

    protected abstract CosmosBeamState createDefaultState();



    public abstract Vec3 getTargetPosition();
}