package dev.cosmos.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public abstract class AbstractCosmosTrailProjectile extends ThrowableProjectile implements ICosmosTrail {


    private CosmosTrailState trailState;

    public AbstractCosmosTrailProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
        this.trailState = this.createDefaultState();
    }


    protected abstract CosmosTrailState createDefaultState();


    @Override
    public CosmosTrailState getTrailState() {
        return this.trailState;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && this.trailState != null) {
            this.trailState.tickHistory(this.position());
        }
    }
}