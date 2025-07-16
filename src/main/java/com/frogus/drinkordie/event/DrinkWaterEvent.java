package com.frogus.drinkordie.event;

import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DrinkWaterEvent {

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getItem();
            if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);
                hydrationCap.ifPresent(hydration -> {
                    float current = hydration.getHydration();
                    hydration.setHydration(current + 25);
                });
            }
        }
    }
}