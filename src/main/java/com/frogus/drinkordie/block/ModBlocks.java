package com.frogus.drinkordie.block;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.fluid.ModFluids;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DrinkOrDie.MODID);

    public static final RegistryObject<LiquidBlock> DIRTY_WATER_BLOCK =
            BLOCKS.register("dirty_water",
                    () -> new LiquidBlock(() -> ModFluids.DIRTY_WATER.get(),
                            Block.Properties.of().noCollission().replaceable().strength(100.0F).noLootTable()));

    public static final RegistryObject<LiquidBlock> SALTY_WATER_BLOCK =
            BLOCKS.register("salty_water",
                    () -> new LiquidBlock(() -> ModFluids.SALTY_WATER.get(),
                            Block.Properties.of().noCollission().replaceable().strength(100.0F).noLootTable()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}