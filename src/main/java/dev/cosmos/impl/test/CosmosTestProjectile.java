package dev.cosmos.impl.test;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;

public class CosmosTestProjectile extends ThrowableProjectile {

    public final Deque<Vec3> history = new ArrayDeque<>();
    public final int maxHistory = 50;

    public CosmosTestProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {}


    @Override
    protected void onHit(HitResult p_37260_) {
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();



        if (this.level().isClientSide) {
            this.history.addFirst(this.position());
            if (this.history.size() > maxHistory) {
                this.history.removeLast();
            }
        }
    }
}