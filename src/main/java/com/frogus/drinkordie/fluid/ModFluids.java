package com.frogus.drinkordie.fluid;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.block.ModBlocks;
import com.frogus.drinkordie.item.ModItems;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;



public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, DrinkOrDie.MODID);

    // Supplier für Properties


    public static final RegistryObject<FlowingFluid> DIRTY_WATER = FLUIDS.register("dirty_water",
            () -> new ForgeFlowingFluid.Source(ModFluids.DIRTY_WATER_PROPERTIES));
    public static final RegistryObject<FlowingFluid> DIRTY_WATER_FLOWING = FLUIDS.register("dirty_water_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DIRTY_WATER_PROPERTIES));
    // Supplier für Properties

    public static final RegistryObject<FlowingFluid> SALTY_WATER = FLUIDS.register("salty_water",
            () -> new ForgeFlowingFluid.Source(ModFluids.SALTY_WATER_PROPERTIES));
    public static final RegistryObject<FlowingFluid> SALTY_WATER_FLOWING = FLUIDS.register("salty_water_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SALTY_WATER_PROPERTIES));


    public static final ForgeFlowingFluid.Properties DIRTY_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.DIRTY_WATER_FLUID_TYPE, DIRTY_WATER, DIRTY_WATER_FLOWING)
            .slopeFindDistance(4).levelDecreasePerBlock(1).block(ModBlocks.DIRTY_WATER_BLOCK)
            .bucket(ModItems.DIRTY_WATER_BUCKET);
    public static final ForgeFlowingFluid.Properties SALTY_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.SALTY_WATER_FLUID_TYPE, SALTY_WATER, SALTY_WATER_FLOWING)
            .slopeFindDistance(4).levelDecreasePerBlock(1).block(ModBlocks.SALTY_WATER_BLOCK)
            .bucket(ModItems.SALTY_WATER_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}