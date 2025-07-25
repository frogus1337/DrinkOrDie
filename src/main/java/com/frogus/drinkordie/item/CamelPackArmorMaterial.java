package com.frogus.drinkordie.item;

import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;

public class CamelPackArmorMaterial implements ArmorMaterial {
    public static final CamelPackArmorMaterial INSTANCE = new CamelPackArmorMaterial();

    private static final int[] DURABILITY_PER_SLOT = {13, 15, 16, 11};
    private static final int[] DEFENSE_PER_SLOT = {0, 2, 0, 0};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY_PER_SLOT[type.getSlot().getIndex()] * 8;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE_PER_SLOT[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return "drinkordie:camel_pack";
    }

    @Override
    public float getToughness() {
        return 0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0f;
    }
}
