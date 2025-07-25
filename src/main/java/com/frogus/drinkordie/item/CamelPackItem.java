package com.frogus.drinkordie.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CamelPackItem extends ArmorItem {
    public static final String NBT_WATER = "Water";
    public static final int CAPACITY = 4000; // 4000mB = 4 Flaschen

    public CamelPackItem() {
        super(
                CamelPackArmorMaterial.INSTANCE,
                Type.CHESTPLATE,
                new Properties().stacksTo(1).durability(8000)
        );
    }

    public static int getWater(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt("Water");
    }

    public static void setWater(ItemStack stack, int mb) {
        stack.getOrCreateTag().putInt("Water", Math.min(mb, CAPACITY));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int water = getWater(stack);
        int percent = (int)((water * 100f) / CAPACITY);
        tooltip.add(Component.literal("Wasser: " + water + " mB (" + percent + "%)").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int water = getWater(stack);
        return Math.round(13f * water / CAPACITY); // Vanilla Leiste: 13px max
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00cfff; // Optional: Aqua-blau (sonst Vanilla-grün)
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        copy.setCount(1);
        return copy;
    }
}
