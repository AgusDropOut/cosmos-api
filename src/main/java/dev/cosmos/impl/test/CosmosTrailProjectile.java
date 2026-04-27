package dev.cosmos.impl.test;

import dev.cosmos.api.entity.AbstractCosmosTrailProjectile;
import dev.cosmos.api.entity.CosmosTrailState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class CosmosTrailProjectile extends AbstractCosmosTrailProjectile {

    public CosmosTrailProjectile(EntityType<? extends CosmosTrailProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected CosmosTrailState createDefaultState() {
        return CosmosTrailState.builder()
                .setMaxHistory(50)
                .addTrail(new ResourceLocation("cosmos", "fire")) // Core fire
                .build();
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