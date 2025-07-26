package com.frogus.drinkordie.mixin;

import com.frogus.drinkordie.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BottleItem.class)
public class BottleItemMixin {
    @Inject(
            method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void drinkordie$replaceWaterBottleWithSalty(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {

        ItemStack itemstack = player.getItemInHand(hand);

        HitResult hitresult = player.pick(5.0D, 1.0F, false);

        if (hitresult instanceof BlockHitResult blockHit) {
            BlockPos blockpos = blockHit.getBlockPos();
            Direction side = blockHit.getDirection();
            BlockPos fluidpos = blockpos.relative(side);


            boolean waterHit =
                    level.getFluidState(blockpos).getType() == Fluids.WATER ||
                            level.getFluidState(fluidpos).getType() == Fluids.WATER;

            if (waterHit) {

                level.playSound(player, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.BOTTLE_FILL, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

                ItemStack saltyWater = new ItemStack(ModItems.SALTY_WATER_BOTTLE.get());
                System.out.println("[DrinkOrDie] About to give: " + saltyWater +
                        " | Item: " + saltyWater.getItem() +
                        " | ID: " + BuiltInRegistries.ITEM.getKey(saltyWater.getItem()));

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (itemstack.isEmpty()) {
                    cir.setReturnValue(InteractionResultHolder.sidedSuccess(saltyWater, level.isClientSide()));
                } else {
                    boolean added = player.getInventory().add(saltyWater);
                    if (!added) {
                        player.drop(saltyWater, false);
                    }
                    cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide()));
                }
            } else {
            }
        } else {
        }
    }
}
