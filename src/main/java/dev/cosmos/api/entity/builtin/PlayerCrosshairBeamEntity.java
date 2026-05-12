package dev.cosmos.api.entity.builtin;

import dev.cosmos.api.entity.AbstractCosmosBeamEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class PlayerCrosshairBeamEntity extends AbstractCosmosBeamEntity {

    private static final EntityDataAccessor<Vector3f> TARGET_VEC = SynchedEntityData.defineId(PlayerCrosshairBeamEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(PlayerCrosshairBeamEntity.class, EntityDataSerializers.INT);

    private final float maxRange;

    public PlayerCrosshairBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.maxRange = 50.0f;
    }

    public PlayerCrosshairBeamEntity(EntityType<?> type, Level level, LivingEntity owner, float maxRange) {
        super(type, level);
        this.setOwner(owner);
        this.maxRange = maxRange;
        this.setPos(owner.getEyePosition());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_VEC, new Vector3f(0, 0, 0));
        this.entityData.define(OWNER_ID, -1);
    }

    public void setOwner(LivingEntity owner) {
        this.entityData.set(OWNER_ID, owner.getId());
    }

    public LivingEntity getOwner() {
        int ownerId = this.entityData.get(OWNER_ID);
        if (ownerId != -1) {
            Entity entity = this.level().getEntity(ownerId);
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    public void setTargetPosition(Vector3f vec) {
        this.entityData.set(TARGET_VEC, vec);
    }

    @Override
    public Vec3 getTargetPosition() {
        Vector3f v3f = this.entityData.get(TARGET_VEC);
        return new Vec3(v3f.x(), v3f.y(), v3f.z());
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            this.setPos(owner.getEyePosition());

            Vec3 eyePos = owner.getEyePosition();
            Vec3 lookVec = owner.getLookAngle();
            Vec3 endPos = eyePos.add(lookVec.x * maxRange, lookVec.y * maxRange, lookVec.z * maxRange);

            ClipContext context = new ClipContext(
                    eyePos, endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    owner
            );

            HitResult hitResult = this.level().clip(context);
            Vec3 hitVec = hitResult.getLocation();

            this.setTargetPosition(new Vector3f((float) hitVec.x, (float) hitVec.y, (float) hitVec.z));

            // Lifecycle Check: Discard if owner is dead OR stops holding right-click
            if (!owner.isAlive() || !owner.isUsingItem()) {
                if (!this.level().isClientSide) this.discard();
            }
        } else if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}