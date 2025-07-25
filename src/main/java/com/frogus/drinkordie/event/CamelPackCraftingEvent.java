/*package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.item.CamelPackItem;
import com.frogus.drinkordie.item.ModItems;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class CamelPackCraftingEvent {
    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof CamelPackItem)) return;
        Player player = event.getEntity();

        int bottles = 0;
        int fillFromGrid = 0;
        int slotCamelPack = -1;
        ItemStack camelPackSource = ItemStack.EMPTY;

        // Suche Camel Pack und Wasserflaschen
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack ing = event.getInventory().getItem(i);
            if (ing.getItem() == ModItems.CAMEL_PACK.get() && slotCamelPack == -1) {
                slotCamelPack = i;
                camelPackSource = ing.copy();
            }
            if (ing.getItem() == Items.POTION && PotionUtils.getPotion(ing) == Potions.WATER) {
                bottles++;
                fillFromGrid += 1000;
            }
        }

        if (slotCamelPack == -1) return;

        int previous = camelPackSource.isEmpty() ? 0 : CamelPackItem.getWater(camelPackSource);
        int fillTotal = Math.min(CamelPackItem.CAPACITY, previous + fillFromGrid);

        // Setze im Crafting-Ergebnis den neuen Füllstand
        CamelPackItem.setWater(result, fillTotal);

        // *** ALTES Camel Pack IM GRID ERSETZEN ***
        if (!event.getInventory().getItem(slotCamelPack).isEmpty()) {
            ItemStack updated = event.getInventory().getItem(slotCamelPack).copy();
            CamelPackItem.setWater(updated, fillTotal);
            event.getInventory().setItem(slotCamelPack, updated);
        }

        // Gib alle verbrauchten Flaschen zurück
        if (!player.getAbilities().instabuild) {
            for (int j = 0; j < bottles; j++) {
                ItemStack glass = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(glass)) {
                    player.drop(glass, false);
                }
            }
        }
    }
}
*/