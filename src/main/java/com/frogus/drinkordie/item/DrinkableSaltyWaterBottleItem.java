package com.frogus.drinkordie.item;

import com.frogus.drinkordie.data.BalanceData;
import com.frogus.drinkordie.data.BalanceHydrationConfig;
import com.frogus.drinkordie.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.UseAnim;

public class DrinkableSaltyWaterBottleItem extends Item {
    public DrinkableSaltyWaterBottleItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            // THIRST-EFFEKT für salty water bottle
            if (!level.isClientSide) {
                BalanceData.EffectConfig thirstConfig = BalanceHydrationConfig.DATA.saltyBottleThirstEffect;
                if (thirstConfig != null) {
                    player.addEffect(new MobEffectInstance(
                            ModEffects.THIRST.get(),
                            thirstConfig.duration,
                            thirstConfig.amplifier
                    ));
                }
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            // IMMER eine Glasflasche geben
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            } else {
                if (!player.getAbilities().instabuild) {
                    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                    boolean added = player.getInventory().add(bottle);
                    if (!added) {
                        player.drop(bottle, false);
                    }
                }
                return stack;
            }
        }
        // Falls kein Spieler (z.B. durch Dispenser): Vanilla-Verhalten
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}
