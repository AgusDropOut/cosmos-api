package dev.cosmos.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCosmosBeamEntity extends Entity implements ICosmosBeam {

    private CosmosBeamState beamState;

    public AbstractCosmosBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    protected abstract CosmosBeamState createDefaultState();

    @Override
    public CosmosBeamState getBeamState() {
        if (this.beamState == null) {
            this.beamState = this.createDefaultState();
        }
        return this.beamState;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.getBeamState().tickLerp(this.getTargetPosition());
        }
    }

    public abstract Vec3 getTargetPosition();
}