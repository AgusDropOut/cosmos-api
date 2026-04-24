package dev.cosmos.impl.client;

import dev.cosmos.Cosmos;
import dev.cosmos.impl.client.render.CosmosBeamEntityRenderer;
import dev.cosmos.impl.data.CosmosDataManager;
import dev.cosmos.init.ModEntityTypes;
import dev.cosmos.impl.test.CosmosTestProjectileRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cosmos.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CosmosClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.TEST_PROJECTILE.get(), CosmosTestProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BEAM_ENTITY.get(), CosmosBeamEntityRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new CosmosDataManager());
    }
}