/*package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.item.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaterBottleReplaceEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        // Prüfen, ob Spieler eine leere Glasflasche hält
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Items.GLASS_BOTTLE) {
            BlockPos pos = event.getPos();
            // Prüfen, ob Block Wasser ist
            if (!level.isClientSide) {
                BlockPos targetPos = pos.relative(event.getFace());
                // Vanilla füllt AUF den Block über der Wasserquelle -> wir prüfen sowohl die Quelle als auch das Nachbarfeld
                if (
                        level.getBlockState(pos).getBlock() == Blocks.WATER ||
                                level.getFluidState(pos).getType() == Fluids.WATER ||
                                level.getBlockState(targetPos).getBlock() == Blocks.WATER ||
                                level.getFluidState(targetPos).getType() == Fluids.WATER
                ) {
                    // Konsumiere 1 Flasche (nur wenn genug vorhanden)
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    // Gib salty_water_bottle als neues Item zurück!
                    ItemStack salty = new ItemStack(ModItems.SALTY_WATER_BOTTLE.get());
                    if (!player.getInventory().add(salty)) {
                        // Wenn Inventar voll, droppe das Item
                        player.drop(salty, false);
                    }

                    event.setCanceled(true); // Vanilla-Funktion verhindern
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
*/