package com.frogus.drinkordie.event;


import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.data.DataMap;
import com.frogus.drinkordie.data.HydrationData;
import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DrinkWaterEvent {

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();

        // Der Item-Name, z.B. "minecraft:water_bottle" oder "minecraft:potion"
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());

        // Für Potions: Wasserflasche als eigenes Mapping
        if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER)
            itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "water_bottle");

        // Werte aus Datenmap laden
        HydrationData data = DataMap.getForItem(itemId.toString());

        // Hydration anpassen
        player.getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(hydration -> {
            hydration.setHydration(hydration.getHydration() + data.hydration);
        });

        // Temperatur anpassen (wenn gewünscht)
        player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP).ifPresent(temp -> {
            temp.setTemperature(temp.getTemperature() + data.temperature);
        });
    }
}
