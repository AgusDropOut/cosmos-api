package dev.cosmos.impl.test;

import dev.cosmos.api.entity.AbstractCosmosTrailProjectile;
import dev.cosmos.api.entity.CosmosTrailState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayDeque;
import java.util.Deque;

public class CosmosTestProjectile extends AbstractCosmosTrailProjectile {

    public CosmosTestProjectile(EntityType<? extends CosmosTestProjectile> type, Level level) {
        // Look at this beautiful fluent API!
        super(type, level, CosmosTrailState.builder()
                .setMaxHistory(50)
                .addTrail(new ResourceLocation("cosmos", "fire")) // Core fire
                .build()
        );
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void onHit(HitResult result) {
        this.discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}