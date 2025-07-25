package com.frogus.drinkordie.recipe;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeSerializers {
    static {
        System.out.println("[ModRecipeSerializers] Klasse wird geladen!");
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "drinkordie");
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "drinkordie");

    public static final RegistryObject<RecipeSerializer<CamelPackFillRecipe>> CAMEL_PACK_FILL =
            RECIPE_SERIALIZERS.register("camel_pack_fill", () -> {
                System.out.println("[ModRecipeSerializers] Registriere CAMEL_PACK_FILL Serializer");
                return new CamelPackFillRecipeSerializer();
            });

    public static final RegistryObject<RecipeType<CamelPackFillRecipe>> CAMEL_PACK_FILL_TYPE =
            RECIPE_TYPES.register("camel_pack_fill", () -> {
                System.out.println("[ModRecipeSerializers] Registriere CAMEL_PACK_FILL_TYPE Type");
                return CamelPackFillRecipeType.INSTANCE;
            });
}
