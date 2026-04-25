package dev.cosmos.api;

import net.minecraft.world.entity.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The main entry point for developers using the Cosmos API.
 */
public class CosmosAPI {

    // Internal lists holding the user registered entities
    public static final List<Supplier<? extends EntityType<?>>> AUTOMATIC_BEAM_RENDERERS = new ArrayList<>();
    public static final List<Supplier<? extends EntityType<?>>> AUTOMATIC_TRAIL_RENDERERS = new ArrayList<>();

    /**
     * Registers an entity to automatically use the Cosmos Beam Renderer.
     * The entity MUST implement ICosmosBeam.
     */
    public static void registerBeamEntity(Supplier<? extends EntityType<?>> entityTypeSupplier) {
        AUTOMATIC_BEAM_RENDERERS.add(entityTypeSupplier);
    }

    /**
     * Registers an entity to automatically use the Cosmos Trail Renderer.
     * The entity MUST implement ICosmosTrail.
     */
    public static void registerTrailEntity(Supplier<? extends EntityType<?>> entityTypeSupplier) {
        AUTOMATIC_TRAIL_RENDERERS.add(entityTypeSupplier);
    }
}