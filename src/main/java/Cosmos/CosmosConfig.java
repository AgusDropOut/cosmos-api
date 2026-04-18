package Cosmos;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber(modid = Cosmos.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CosmosConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    private static final ForgeConfigSpec.BooleanValue ENABLE_COMPLEX_EFFECTS = BUILDER
            .comment("If true, the mod will enable complex effects that may impact performance. Set to false to disable these effects for better performance.")
            .define("enableComplexEffects", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();


    public static boolean enableComplexEffects;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableComplexEffects = ENABLE_COMPLEX_EFFECTS.get();
    }
}