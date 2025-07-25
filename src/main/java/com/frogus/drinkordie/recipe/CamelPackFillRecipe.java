package com.frogus.drinkordie.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import com.frogus.drinkordie.item.ModItems;
import net.minecraft.core.NonNullList;


public class CamelPackFillRecipe implements CraftingRecipe {

    private final ResourceLocation id;

    public CamelPackFillRecipe(ResourceLocation id) {
        this.id = id;
        System.out.println("[CamelPackFillRecipe] Konstruktor aufgerufen für Rezept: " + id);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int camelPacks = 0;
        int waterBottles = 0;

        System.out.println("[CamelPackFillRecipe] matches() aufgerufen");

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            System.out.println("[CamelPackFillRecipe] Slot " + i + ": " + stack);
            if (stack.getItem() == ModItems.CAMEL_PACK.get()) {
                camelPacks++;
            } else if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                waterBottles++;
            } else if (!stack.isEmpty()) {
                System.out.println("[CamelPackFillRecipe] Unzulässiges Item im Grid: " + stack);
                return false;
            }
        }
        boolean result = camelPacks == 1 && waterBottles >= 1 && waterBottles <= 4;
        System.out.println("[CamelPackFillRecipe] camelPacks=" + camelPacks + ", waterBottles=" + waterBottles + " -> matches=" + result);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return remaining;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        System.out.println("[CamelPackFillRecipe] assemble() aufgerufen");
        ItemStack camelPack = ItemStack.EMPTY;
        int waterBottles = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == ModItems.CAMEL_PACK.get()) {
                camelPack = stack.copy();
                System.out.println("[CamelPackFillRecipe] Camel Pack gefunden");
            } else if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                waterBottles++;
            }
        }

        if (camelPack.isEmpty() || waterBottles == 0) {
            System.out.println("[CamelPackFillRecipe] Kein Camel Pack oder keine Wasserflaschen gefunden!");
            return ItemStack.EMPTY;
        }

        int amountPerBottle = 1000;
        int maxWater = 4000;

        int currentWater = camelPack.getOrCreateTag().getInt("Water");
        int newWater = Math.min(currentWater + (waterBottles * amountPerBottle), maxWater);

        camelPack.getOrCreateTag().putInt("Water", newWater);
        System.out.println("[CamelPackFillRecipe] Water: alt=" + currentWater + ", neu=" + newWater);
        return camelPack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        boolean result = width * height >= 2;
        System.out.println("[CamelPackFillRecipe] canCraftInDimensions(" + width + "," + height + ") -> " + result);
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        System.out.println("[CamelPackFillRecipe] getResultItem() aufgerufen");
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        System.out.println("[CamelPackFillRecipe] getId() -> " + id);
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        System.out.println("[CamelPackFillRecipe] getSerializer() aufgerufen");
        return ModRecipeSerializers.CAMEL_PACK_FILL.get();
    }

    @Override
    public RecipeType<?> getType() {
        System.out.println("[CamelPackFillRecipe] getType() aufgerufen (nun RecipeType.CRAFTING)");
        return RecipeType.CRAFTING;
    }

    // *** WICHTIG ***: Das Rezeptbuch braucht die category()-Methode!
    // In manchen Mappings ist das Enum "RecipeBookCategory" nicht öffentlich.
    // Workaround: Gib null zurück, das akzeptiert Forge in der Regel!
    @Override
    public CraftingBookCategory category() {
        // Wenn du Zugriff auf RecipeBookCategory hast, verwende: return RecipeBookCategory.MISC;
        return null;
    }
}
