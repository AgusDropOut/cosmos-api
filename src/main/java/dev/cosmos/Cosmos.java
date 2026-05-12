package dev.cosmos;

import com.mojang.logging.LogUtils;
import dev.cosmos.api.CosmosAPI;
import dev.cosmos.api.registry.CosmosDataRegistry;
import dev.cosmos.impl.data.handler.BeamDataHandler;
import dev.cosmos.impl.data.handler.MaterialDataHandler;
import dev.cosmos.impl.data.handler.TrailDataHandler;
import dev.cosmos.init.ModEntityTypes;
import dev.cosmos.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(Cosmos.MODID)
public class Cosmos {
    public static final String MODID = "cosmos";
    public static final Logger LOGGER = LogUtils.getLogger();



    public Cosmos() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModEntityTypes.register(modEventBus);

        CosmosDataRegistry.register("cosmos:trail_system", new TrailDataHandler());
        CosmosDataRegistry.register("cosmos:beam_system", new BeamDataHandler());
        CosmosDataRegistry.register("cosmos:material", new MaterialDataHandler());

        modEventBus.addListener(this::commonSetup);

        CosmosAPI.registerBeamEntity(ModEntityTypes.BEAM_ENTITY);
        CosmosAPI.registerBeamEntity(ModEntityTypes.EXPOSED_PARAMETERS_BEAM);
        CosmosAPI.registerTrailEntity(ModEntityTypes.TEST_PROJECTILE);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CosmosConfig.SPEC, "cosmos-common.toml");



        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterGameTests(RegisterGameTestsEvent event) {
            LOGGER.info("Cosmos API: Manually registering GameTests...");
            event.register(dev.cosmos.test.gametest.CosmosEntityGameTests.class);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Cosmos API: Initializing common setup.");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Cosmos API: Initializing client setup.");

        }
    }
}