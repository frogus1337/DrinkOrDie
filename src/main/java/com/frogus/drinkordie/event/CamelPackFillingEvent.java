package com.frogus.drinkordie.event;

import com.frogus.drinkordie.item.CamelPackItem;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class CamelPackFillingEvent {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();

        // Mit Camel Pack in der Haupt-/Nebenhand?
        if (!(held.getItem() instanceof CamelPackItem)) return;

        // Prüfen: Wasserflasche in der anderen Hand?
        ItemStack waterBottle = player.getOffhandItem();
        if (waterBottle.getItem() != Items.POTION || PotionUtils.getPotion(waterBottle) != Potions.WATER)
            return;

        int water = CamelPackItem.getWater(held);
        if (water >= CamelPackItem.CAPACITY) return; // Schon voll!

        // Füllen!
        CamelPackItem.setWater(held, Math.min(water + 1000, CamelPackItem.CAPACITY));
        if (!player.getAbilities().instabuild) {
            waterBottle.shrink(1);
            // Leere Glasflasche geben
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.BOTTLE_FILL, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        event.setCanceled(true);
    }
}
