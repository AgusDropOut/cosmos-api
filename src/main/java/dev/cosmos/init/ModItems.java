package dev.cosmos.init;

import dev.cosmos.Cosmos;
import dev.cosmos.example.item.ExampleBeamItem;
import dev.cosmos.example.item.ExampleExposedParametersBeamItem;
import dev.cosmos.example.item.ExampleTrailItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Cosmos.MODID);

    public static final RegistryObject<Item> EXAMPLE_WAND = ITEMS.register("test_wand",
            () -> new ExampleTrailItem(new Item.Properties()));
    public static final RegistryObject<Item> EXAMPLE_BEAM = ITEMS.register("test_beam",
            () -> new ExampleBeamItem(new Item.Properties()));
    public static final RegistryObject<Item> EXAMPLE_EXPOSED_PARAMETERS_BEAM = ITEMS.register("test_exposed_parameter_beam",
            () -> new ExampleExposedParametersBeamItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}