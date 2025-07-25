

package com.frogus.drinkordie.event;

import com.frogus.drinkordie.config.ModServerConfig;
import com.frogus.drinkordie.fluid.ModFluids;
import com.frogus.drinkordie.item.ModItems;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class FillFluidEvent {

    // Entlang der Blickrichtung prüfen (Vanilla-Style)
    private static boolean tryFillCustomFluidBottleRaycast(Player player, Level level, ItemStack heldItem, PlayerInteractEvent event) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        double reach = 5.0D;

        for (double d = 0; d <= reach; d += 0.1D) {
            Vec3 checkPos = eyePos.add(look.scale(d));
            BlockPos blockPos = BlockPos.containing(checkPos);
            BlockState state = level.getBlockState(blockPos);

            // DIRTY WATER SOURCE
            if (!state.getFluidState().isEmpty() && state.getFluidState().getType() == ModFluids.DIRTY_WATER.get()) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                ItemStack bottle = new ItemStack(ModItems.DIRTY_WATER_BOTTLE.get());
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
                return true;
            }
            // SALTY WATER SOURCE
            if (!state.getFluidState().isEmpty() && state.getFluidState().getType() == ModFluids.SALTY_WATER.get()) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                ItemStack bottle = new ItemStack(ModItems.SALTY_WATER_BOTTLE.get());
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);

                if (ModServerConfig.SERVER.bottlesRemoveSource.get()) {
                    // Quelle entfernen
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                }

                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldItem = event.getItemStack();

        if (player == null || level.isClientSide) return;
        if (heldItem.getItem() != Items.GLASS_BOTTLE) return;

        tryFillCustomFluidBottleRaycast(player, level, heldItem, event);

    }
}


/*
package com.frogus.drinkordie.event;



import com.frogus.drinkordie.fluid.ModFluids;
import com.frogus.drinkordie.item.ModItems;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class FillFluidEvent {

    // Prüft auf "dirty_water" oder "salty_water" und sucht dabei ggf. nach unten nach einer Quelle (Vanilla-Logik!)
    private static boolean tryFillCustomFluidBottle(Player player, Level level, ItemStack heldItem, BlockPos pos, BlockState blockState, PlayerInteractEvent event) {
        if (player == null || heldItem == null || blockState == null) return false;

        var fluidType = blockState.getFluidState().getType();

        // --- DIRTY WATER ---
        if (fluidType == ModFluids.DIRTY_WATER.get() || fluidType == ModFluids.DIRTY_WATER_FLOWING.get()) {
            BlockPos posDown = pos.immutable();
            boolean foundSource = false;

            // Vanilla-Logik: Solange dirty_water oder dirty_water_flowing nach unten, suche nach Quelle
            while (true) {
                BlockState state = level.getBlockState(posDown);
                var f = state.getFluidState().getType();
                if (f == ModFluids.DIRTY_WATER.get()) {
                    foundSource = true;
                    break;
                }
                if (f == ModFluids.DIRTY_WATER_FLOWING.get()) {
                    posDown = posDown.below();
                    continue;
                }
                break; // anderes Fluid, Luft, Block -> keine Quelle gefunden
            }

            if (foundSource) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                ItemStack bottle = new ItemStack(ModItems.DIRTY_WATER_BOTTLE.get());
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
                return true;
            }
        }

        // --- SALTY WATER ---
        if (fluidType == ModFluids.SALTY_WATER.get() || fluidType == ModFluids.SALTY_WATER_FLOWING.get()) {
            BlockPos posDown = pos.immutable();
            boolean foundSource = false;

            while (true) {
                BlockState state = level.getBlockState(posDown);
                var f = state.getFluidState().getType();
                if (f == ModFluids.SALTY_WATER.get()) {
                    foundSource = true;
                    break;
                }
                if (f == ModFluids.SALTY_WATER_FLOWING.get()) {
                    posDown = posDown.below();
                    continue;
                }
                break;
            }

            if (foundSource) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                ItemStack bottle = new ItemStack(ModItems.SALTY_WATER_BOTTLE.get());
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldItem = event.getItemStack();

        if (player == null || level.isClientSide) return;
        if (heldItem.getItem() != Items.GLASS_BOTTLE) return;

        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);

        tryFillCustomFluidBottle(player, level, heldItem, pos, blockState, event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldItem = event.getItemStack();

        if (player == null || level.isClientSide) return;
        if (heldItem.getItem() != Items.GLASS_BOTTLE) return;

        // Raytrace mit Flüssigkeiten!
        HitResult hit = player.pick(5.0D, 0.0F, true);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            tryFillCustomFluidBottle(player, level, heldItem, pos, blockState, event);
        }
    }
}
*/