package com.frogus.drinkordie.effect;

import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DrinkOrDie.MODID);

    public static final RegistryObject<MobEffect> THIRST = EFFECTS.register("thirst", ThirstEffect::new);

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
