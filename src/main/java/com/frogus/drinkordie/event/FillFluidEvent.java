package com.frogus.drinkordie.event;

import com.frogus.drinkordie.fluid.ModFluids;
import com.frogus.drinkordie.item.ModItems;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class FillFluidEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();

        // Nur wenn das gehaltene Item eine Glasflasche ist
        if (heldItem.getItem() != Items.GLASS_BOTTLE)
            return;

        // Prüfe den aktuellen Block und ggf. den Block darüber
        tryFillBottleWithFluid(level, pos, player, heldItem, ModFluids.DIRTY_WATER.get(), new ItemStack(ModItems.DIRTY_WATER_BOTTLE.get()), event);
        tryFillBottleWithFluid(level, pos, player, heldItem, ModFluids.SALTY_WATER.get(), new ItemStack(ModItems.SALTY_WATER_BOTTLE.get()), event);
    }

    private static void tryFillBottleWithFluid(Level level, BlockPos pos, Player player, ItemStack heldItem,
                                               net.minecraft.world.level.material.Fluid fluid, ItemStack resultBottle, PlayerInteractEvent.RightClickBlock event) {
        BlockState state = level.getBlockState(pos);

        // Prüfe auf Quellblock an angeklickter Position
        if (state.getBlock() instanceof LiquidBlock && state.getFluidState().getType() == fluid && state.getFluidState().isSource()) {
            fillBottle(level, pos, player, heldItem, resultBottle, event.getHand(), event);
        }
        // Prüfe auf Quellblock eins höher (z.B. wenn auf Flüssigkeit geklickt wurde)
        else {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.getBlock() instanceof LiquidBlock && aboveState.getFluidState().getType() == fluid && aboveState.getFluidState().isSource()) {
                fillBottle(level, above, player, heldItem, resultBottle, event.getHand(), event);
            }
        }
    }

    private static void fillBottle(Level level, BlockPos pos, Player player, ItemStack heldItem,
                                   ItemStack filledBottle, InteractionHand hand, PlayerInteractEvent.RightClickBlock event) {
        if (!level.isClientSide) {
            // Flasche verbrauchen
            heldItem.shrink(1);

            // Gefüllte Flasche ins Inventar, Hand oder droppen
            if (heldItem.isEmpty()) {
                player.setItemInHand(hand, filledBottle.copy());
            } else if (!player.getInventory().add(filledBottle.copy())) {
                player.drop(filledBottle.copy(), false);
            }

            // Flüssigkeits-Quellblock entfernen
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        player.swing(hand);
    }
}
