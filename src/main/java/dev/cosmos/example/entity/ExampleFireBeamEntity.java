package dev.cosmos.example.entity;

import dev.cosmos.api.entity.CosmosBeamState;
import dev.cosmos.api.entity.builtin.PlayerCrosshairBeamEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ExampleFireBeamEntity extends PlayerCrosshairBeamEntity {

    public ExampleFireBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ExampleFireBeamEntity(EntityType<?> type, Level level, LivingEntity owner, float maxRange) {
        super(type, level, owner, maxRange);
    }

    @Override
    protected CosmosBeamState createDefaultState() {
        return CosmosBeamState.builder()
                .addBeam(new ResourceLocation("cosmos", "example_fire_beam"))
                .setLerpFactor(0.5f)
                .build();
    }
}
