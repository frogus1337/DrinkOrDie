package com.frogus.drinkordie.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BottleOfSaltItem extends Item {

    public BottleOfSaltItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }
}
