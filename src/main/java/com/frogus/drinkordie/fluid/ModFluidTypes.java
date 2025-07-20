package com.frogus.drinkordie.fluid;

// com.frogus.drinkordie.fluid.ModFluidTypes

import com.frogus.drinkordie.core.DrinkOrDie;
import org.joml.Vector3f;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, DrinkOrDie.MODID);

    public static final RegistryObject<FluidType> DIRTY_WATER_FLUID_TYPE = FLUID_TYPES.register("dirty_water_fluid",
            () -> new BaseFluidType(
                    new ResourceLocation("block/water_still"),
                    new ResourceLocation("block/water_flow"),
                    new ResourceLocation(DrinkOrDie.MODID, "misc/in_dirty_water"),
                    0xFF765339, // z.B. bräunlich
                    new Vector3f(118f / 255f, 83f / 255f, 57f / 255f),
                    FluidType.Properties.create().density(1000).viscosity(1000)
            ));
    public static final RegistryObject<FluidType> SALTY_WATER_FLUID_TYPE = FLUID_TYPES.register("salty_water_fluid",
            () -> new BaseFluidType(
                    new ResourceLocation("block/water_still"),
                    new ResourceLocation("block/water_flow"),
                    new ResourceLocation(DrinkOrDie.MODID, "misc/in_salty_water"),
                    0xFFDDEEEE, // blass-blau
                    new Vector3f(0.87f, 0.94f, 1f),
                    FluidType.Properties.create().density(1000).viscosity(1000)
            ));
    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
