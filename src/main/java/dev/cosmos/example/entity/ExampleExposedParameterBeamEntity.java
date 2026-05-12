package dev.cosmos.example.entity;

import dev.cosmos.api.entity.CosmosBeamState;
import dev.cosmos.api.entity.builtin.PlayerCrosshairBeamEntity;
import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleExposedParameterBeamEntity extends PlayerCrosshairBeamEntity {

    CosmosMaterialInstance materialInstance = new CosmosMaterialInstance(new ResourceLocation("cosmos", "example_exposed_parameters_beam"));
    private float heatLevel = 0.0f;


    public ExampleExposedParameterBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ExampleExposedParameterBeamEntity(EntityType<?> type, Level level, LivingEntity owner, float maxRange) {
        super(type, level, owner, maxRange);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTargetPosition() != null && level().isClientSide()) {
            BlockPos targetPos = BlockPos.containing(getTargetPosition().x, getTargetPosition().y, getTargetPosition().z);
            BlockState state = level().getBlockState(targetPos);

            //  Calculate Heat
            if (!state.isAir()) {
                heatLevel = Math.min(1.0f, heatLevel + 0.02f);
            } else {
                heatLevel = Math.max(0.0f, heatLevel - 0.05f);
            }
            materialInstance.setVec3("u_color_1", 1.0f, 0.4f - (heatLevel * 0.5f), 0.4f - heatLevel);
            materialInstance.setVec3("u_color_2", heatLevel, 0.0f, 1.0f - heatLevel);


        }
    }

    @Override
    protected CosmosBeamState createDefaultState() {
        return CosmosBeamState.builder()
                .addBeam(new ResourceLocation("cosmos", "example_fire_beam"), materialInstance )
                .setLerpFactor(0.5f)
                .build();
    }
}
