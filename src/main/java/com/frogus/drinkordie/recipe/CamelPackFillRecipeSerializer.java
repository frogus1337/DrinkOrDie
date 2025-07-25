package com.frogus.drinkordie.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CamelPackFillRecipeSerializer implements RecipeSerializer<CamelPackFillRecipe> {

    public CamelPackFillRecipeSerializer() {
        System.out.println("[CamelPackFillRecipeSerializer] Konstruktor wurde aufgerufen!");
    }

    @Override
    public CamelPackFillRecipe fromJson(ResourceLocation id, JsonObject json) {
        System.out.println("[CamelPackFillRecipeSerializer] fromJson() für " + id);
        return new CamelPackFillRecipe(id);
    }

    @Override
    public CamelPackFillRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        System.out.println("[CamelPackFillRecipeSerializer] fromNetwork() für " + id);
        return new CamelPackFillRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CamelPackFillRecipe recipe) {
        System.out.println("[CamelPackFillRecipeSerializer] toNetwork() aufgerufen");
        // nichts zu serialisieren
    }
}
