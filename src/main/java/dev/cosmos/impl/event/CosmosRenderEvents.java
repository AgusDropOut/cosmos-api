package dev.cosmos.impl.event;

import dev.cosmos.Cosmos;
import dev.cosmos.impl.client.render.CosmosTrailManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cosmos.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CosmosRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {

            CosmosTrailManager.renderAllAndClear(
                    event.getPoseStack(),
                    event.getProjectionMatrix(),
                    event.getCamera()
            );

        }
    }
}