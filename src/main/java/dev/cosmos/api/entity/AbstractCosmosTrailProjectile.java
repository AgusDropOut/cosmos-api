package dev.cosmos.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public abstract class AbstractCosmosTrailProjectile extends ThrowableProjectile implements ICosmosTrail {

    private final CosmosTrailState trailState;

    public AbstractCosmosTrailProjectile(EntityType<? extends ThrowableProjectile> type, Level level, ResourceLocation trailId, int maxHistory) {
        super(type, level);
        this.trailState = new CosmosTrailState(trailId, maxHistory);
    }

    @Override
    public CosmosTrailState getTrailState() {
        return this.trailState;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.trailState.tickHistory(this.position());
        }
    }
}