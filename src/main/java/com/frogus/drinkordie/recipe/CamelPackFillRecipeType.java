package com.frogus.drinkordie.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public class CamelPackFillRecipeType implements RecipeType<CamelPackFillRecipe> {
    public static final CamelPackFillRecipeType INSTANCE = new CamelPackFillRecipeType();

    private CamelPackFillRecipeType() {
        System.out.println("[CamelPackFillRecipeType] Konstruktor wurde aufgerufen!");
    }
}
