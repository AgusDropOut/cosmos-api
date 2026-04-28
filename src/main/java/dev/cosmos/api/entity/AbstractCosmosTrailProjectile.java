package dev.cosmos.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public abstract class AbstractCosmosTrailProjectile extends ThrowableProjectile implements ICosmosTrail {

    private CosmosTrailState trailState;

    public AbstractCosmosTrailProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    protected abstract CosmosTrailState createDefaultState();

    @Override
    public CosmosTrailState getTrailState() {
        if (this.trailState == null) {
            this.trailState = this.createDefaultState();
        }
        return this.trailState;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.getTrailState().tickHistory(this.position());
        }
    }
}