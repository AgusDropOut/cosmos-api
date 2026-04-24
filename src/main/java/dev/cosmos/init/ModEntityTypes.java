package dev.cosmos.init;

import dev.cosmos.Cosmos;
import dev.cosmos.impl.entity.CosmosBeamEntity;
import dev.cosmos.impl.test.CosmosTestProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Cosmos.MODID);

    public static final RegistryObject<EntityType<CosmosTestProjectile>> TEST_PROJECTILE =
            ENTITY_TYPES.register("test_projectile",
                    () -> EntityType.Builder.<CosmosTestProjectile>of(CosmosTestProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("test_projectile"));
    public static final RegistryObject<EntityType<CosmosBeamEntity>> BEAM_ENTITY = ENTITY_TYPES.register("beam_entity",
            () -> EntityType.Builder.<CosmosBeamEntity>of(CosmosBeamEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("beam_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}