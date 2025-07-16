package com.frogus.drinkordie.hud;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, value = Dist.CLIENT)
public class TemperatureHudOverlay {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // HUD für Creative/Spectator ausblenden
        //if (player.isCreative() || player.isSpectator()) return;

        LazyOptional<PlayerTemperature> tempCap = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP);
        tempCap.ifPresent(temperature -> {
            float temp = temperature.getTemperature();

            // Debug-Ausgabe: Siehst du im CLIENT-Log
            //System.out.println("HUD: Client-Side Temperature = " + temp);

            // Position oben rechts (10px vom rechten Rand, 10px von oben)
            int x = mc.getWindow().getGuiScaledWidth() - 10;
            int y = 10;

            String tempText = String.format("%.1f °C", temp);
            int textWidth = mc.font.width(tempText);

            GuiGraphics gg = event.getGuiGraphics();

            // Mit Schatten
            gg.drawString(mc.font, tempText, x - textWidth + 1, y + 1, 0xAA000000, false); // Schatten (schwarz, halbtransparent)
            gg.drawString(mc.font, tempText, x - textWidth, y, 0xFFFFFF, false); // Weiß
        });
    }
}
