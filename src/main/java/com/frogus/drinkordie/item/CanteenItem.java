package com.frogus.drinkordie.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CanteenItem extends Item {
    public CanteenItem() {
        super(new Item.Properties().stacksTo(1).durability(10));
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // Verhindert Canteen + Canteen = Canteen (Vanilla-Repair)
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
