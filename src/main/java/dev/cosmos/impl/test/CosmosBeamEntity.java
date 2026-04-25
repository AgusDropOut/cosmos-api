package dev.cosmos.impl.test; // Moved to test package since it's an implementation

import dev.cosmos.api.entity.AbstractCosmosBeamEntity;
import dev.cosmos.init.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class CosmosBeamEntity extends AbstractCosmosBeamEntity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(CosmosBeamEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> END_X = SynchedEntityData.defineId(CosmosBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Y = SynchedEntityData.defineId(CosmosBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Z = SynchedEntityData.defineId(CosmosBeamEntity.class, EntityDataSerializers.FLOAT);

    public CosmosBeamEntity(EntityType<?> type, Level level) {
        super(type, level, new ResourceLocation("cosmos", "fire_beam")); // Hardcoded for test
    }

    public CosmosBeamEntity(Level level, LivingEntity owner, ResourceLocation beamId) {
        super(ModEntityTypes.BEAM_ENTITY.get(), level, beamId);
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(END_X, 0f);
        this.entityData.define(END_Y, 0f);
        this.entityData.define(END_Z, 0f);
    }

    // Required by the Abstract class!
    @Override
    public Vec3 getTargetPosition() {
        return new Vec3(this.entityData.get(END_X), this.entityData.get(END_Y), this.entityData.get(END_Z));
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive() || !owner.isUsingItem()) {
            if (!this.level().isClientSide) this.discard();
            return;
        }

        this.setPos(owner.getX(), owner.getEyeY() - 0.2, owner.getZ());

        if (!this.level().isClientSide) {
            HitResult hit = owner.pick(30.0D, 1.0f, false);
            Vec3 end = hit.getLocation();
            this.entityData.set(END_X, (float) end.x);
            this.entityData.set(END_Y, (float) end.y);
            this.entityData.set(END_Z, (float) end.z);
        }
    }

    private LivingEntity getOwner() {
        return this.entityData.get(OWNER_UUID).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}