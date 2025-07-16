package com.frogus.drinkordie.hud;

import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.core.DrinkOrDie;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, value = Dist.CLIENT)
public class HydrationHudOverlay {

    // Deine Bildpfade (achte auf alles klein, keine Leerzeichen/Umlaute)
    private static final ResourceLocation FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "textures/gui/hydration_frame.png");

    private static final ResourceLocation FILL_TEXTURE  = ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "textures/gui/hydration_fill.png");

    private static final ResourceLocation SCALE_TEXTURE = ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "textures/gui/hydration_scale.png");

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (player.isCreative() || player.isSpectator()) return;

        LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);
        hydrationCap.ifPresent(hydration -> {
            float hydrationLevel = hydration.getHydration();





            // HUD-Position: z.B. rechts neben Hungerbar
            Window window = mc.getWindow();
            int x = window.getGuiScaledWidth() / 2 + 91 + 6;
            int y = window.getGuiScaledHeight() - 49;

            int width = 81;
            int height = 11;

            // Füllbalken: Breite je nach Hydration
            int fillWidth = (int)(width * (hydrationLevel / 100.0f));
            GuiGraphics gg = event.getGuiGraphics();





            // Rahmen (immer volle Breite)
            gg.blit(FRAME_TEXTURE, x, y, 0, 0, width, height, width, height);

            // Füllung (wird "maskiert" auf die aktuelle Hydration-Breite)
            gg.blit(FILL_TEXTURE, x + (width - fillWidth), y, width - fillWidth, 0, fillWidth, height, width, height);

            // Skala/Deko (immer volle Breite)
            gg.blit(SCALE_TEXTURE, x, y, 0, 0, width, height, width, height);

            // Prozenttext (optional, wie gehabt)
            String hydrationText = String.format("%.1f %%", hydrationLevel);
            int textWidth = mc.font.width(hydrationText);
            int textX = x + (width - textWidth) / 2;
            int textY = y + 1;

            // ...vorher: Berechnung/Anzeige Hydration-Level etc.

            String statusText;
            int statusColor;

// Status und Farbe je nach Wert wählen
            if (hydrationLevel > 45.0f) {
                statusText = "fit";
                statusColor = 0xFFFFFF; // weiß
            }

            else if (hydrationLevel > 25.0f) {
                statusText = "thirsty";
                statusColor = 0xFFFF55; // gelb-orange (Minecraft Goldfarbe)
            } else if (hydrationLevel > 5.0f) {
                statusText = "drying";
                statusColor = 0xFFAA00; // orange-rot (Minecraft Orange)
            } else {
                statusText = "dying of thirst";
                statusColor = 0xFF5500;
            }

            // Screen-Tint bei niedrigem Hydrationwert
            if (hydrationLevel < 33f) {
                int sw = window.getGuiScaledWidth();
                int sh = window.getGuiScaledHeight();

                int tintColor;
                int alpha;
                if (hydrationLevel < 10f) {
                    tintColor = 0xFFFFE0; // hellgelb (Eierschale)
                    alpha = 3;           // sehr kräftig
                } else {
                    tintColor = 0xF7F7F7; // fast weiß
                    alpha = 1;           // leicht transparent
                }
                // alpha (0..255), dann RGB
                int argb = (alpha << 24) | tintColor;
                event.getGuiGraphics().fill(0, 0, sw, sh, argb);
            }

// Text positionieren (z. B. über der Leiste zentriert)
            int statusWidth = mc.font.width(statusText);
            int statusX = x + (width - statusWidth) / 2;
            int statusY = y - 10;

// Mit leichtem Schatten für Lesbarkeit
            gg.drawString(mc.font, statusText, statusX + 1, statusY + 1, 0xAA000000, false);
            gg.drawString(mc.font, statusText, statusX, statusY, statusColor, false);



            // Mit Schatten für Lesbarkeit
            gg.drawString(mc.font, hydrationText, textX + 1, textY + 1, 0xAA000000, false);
            gg.drawString(mc.font, hydrationText, textX, textY, 0xFFFFFF, false);
        });
    }
}
