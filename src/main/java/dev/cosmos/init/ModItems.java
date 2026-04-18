package dev.cosmos.init;

import dev.cosmos.Cosmos;
import dev.cosmos.impl.test.CosmosTestItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Cosmos.MODID);

    public static final RegistryObject<Item> TEST_WAND = ITEMS.register("test_wand",
            () -> new CosmosTestItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}