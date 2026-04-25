package dev.cosmos.impl.client;

import dev.cosmos.Cosmos;
import dev.cosmos.api.CosmosAPI;
import dev.cosmos.api.entity.ICosmosBeam;
// import dev.cosmos.api.entity.ICosmosTrail; // Uncomment when ready
import dev.cosmos.api.entity.ICosmosTrail;
import dev.cosmos.impl.client.render.CosmosBeamRenderer;
import dev.cosmos.impl.client.render.CosmosTrailRenderer;
import dev.cosmos.impl.data.CosmosDataManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Cosmos.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CosmosClientSetup {



    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new CosmosDataManager());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {

        Cosmos.LOGGER.info("Cosmos API: Attaching Automatic Renderers...");


        for (Supplier<? extends EntityType<?>> typeSupplier : CosmosAPI.AUTOMATIC_BEAM_RENDERERS) {
            bindBeamRenderer(event, typeSupplier.get());
        }


        for (Supplier<? extends EntityType<?>> typeSupplier : CosmosAPI.AUTOMATIC_TRAIL_RENDERERS) {
             bindTrailRenderer(event, typeSupplier.get());
        }
    }





    /**
     * This helper method safely casts the wildcard <?> into our strict <T> bounds.
     * This satisfies the Java compiler so we can use the ::new method reference!
     */
    @SuppressWarnings("unchecked")
    private static <T extends Entity & ICosmosBeam> void bindBeamRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
        event.registerEntityRenderer((EntityType<T>) type, CosmosBeamRenderer::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity & ICosmosTrail> void bindTrailRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
        event.registerEntityRenderer((EntityType<T>) type, CosmosTrailRenderer::new);
    }

}