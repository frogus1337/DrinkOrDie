package com.frogus.drinkordie.item;

import com.frogus.drinkordie.fluid.ModFluids;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DrinkOrDie.MODID);

    // Normale Items

    public static final RegistryObject<Item> CANTEEN = ITEMS.register("canteen",
            () -> new Item(new Item.Properties().stacksTo(1).durability(10)));
    public static final RegistryObject<Item> CAMEL_PACK = ITEMS.register("camel_pack",
            () -> new Item(new Item.Properties().stacksTo(1).durability(40)));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOTTLE_OF_SALT = ITEMS.register("bottle_of_salt",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // Fluid-Buckets (Eimer)
    // com.frogus.drinkordie.item.ModItems

    public static final RegistryObject<Item> DIRTY_WATER_BUCKET = ITEMS.register("dirty_water_bucket",
            () -> new BucketItem(() -> ModFluids.DIRTY_WATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> SALTY_WATER_BUCKET = ITEMS.register("salty_water_bucket",
            () -> new BucketItem(() -> ModFluids.SALTY_WATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> DIRTY_WATER_BOTTLE = ITEMS.register("dirty_water_bottle",
            () -> new DrinkableDirtyWaterBottleItem());
    public static final RegistryObject<Item> SALTY_WATER_BOTTLE = ITEMS.register("salty_water_bottle",
            () -> new DrinkableSaltyWaterBottleItem());


    // Registrierung mit EventBus (Forge-Standard seit 1.18)
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
