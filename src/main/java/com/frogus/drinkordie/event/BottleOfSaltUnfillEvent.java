package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.item.ModItems; // nur falls du direkte Vergleiche möchtest
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class BottleOfSaltUnfillEvent {

    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        // Prüfe: Ergebnis ist "salt" und als Zutat ist bottle_of_salt
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (!resultId.getNamespace().equals("drinkordie") || !resultId.getPath().equals("salt")) return;

        boolean foundBottleOfSalt = false;
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack ingredient = event.getInventory().getItem(i);
            if (!ingredient.isEmpty()) {
                ResourceLocation ingId = BuiltInRegistries.ITEM.getKey(ingredient.getItem());
                if (ingId.getNamespace().equals("drinkordie") && ingId.getPath().equals("bottle_of_salt")) {
                    foundBottleOfSalt = true;
                    break;
                }
            }
        }
        if (!foundBottleOfSalt) return;

        // Gib die Glasflasche zurück
        if (!event.getEntity().getAbilities().instabuild) {
            ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
            Player player = event.getEntity();
            if (!player.getInventory().add(glassBottle)) {
                player.drop(glassBottle, false);
            }
        }
    }
}
