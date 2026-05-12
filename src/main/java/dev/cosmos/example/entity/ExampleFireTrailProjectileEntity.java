package dev.cosmos.example.entity;

import dev.cosmos.api.entity.AbstractCosmosTrailProjectile;
import dev.cosmos.api.entity.CosmosTrailState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ExampleFireTrailProjectileEntity extends AbstractCosmosTrailProjectile {

    public ExampleFireTrailProjectileEntity(EntityType<? extends ExampleFireTrailProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected CosmosTrailState createDefaultState() {
        return CosmosTrailState.builder()
                .setMaxHistory(50)
                .addTrail(new ResourceLocation("cosmos", "example_fire_trail"))
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