package dev.kuro.glass.hud;

import dev.kuro.glass.core.*;
import dev.kuro.glass.ui.Draw;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class HudRenderer {
    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, Identifier.fromNamespaceAndPath("kuroglass", "hud"),
            (g, tick) -> { Minecraft mc = Minecraft.getInstance(); if (mc.options.hideGui || mc.player == null) return; renderAll(g, mc, false); });
    }
    public static double[] pos(HudModule m) { return Config.hudPos.computeIfAbsent(m.name, k -> new double[]{m.defX, m.defY}); }
    /** Draws all enabled widgets; in editor mode draws disabled ones dimmed too. */
    public static void renderAll(GuiGraphics g, Minecraft mc, boolean editor) {
        Font f = mc.font; double s = Config.theme.hudScale; int sw = g.guiWidth(), sh = g.guiHeight();
        for (Module mod : ModuleManager.all()) {
            if (!(mod instanceof HudModule m) || (!m.enabled && !editor)) continue;
            double[] p = pos(m); int w = m.width(f), h = m.height(f);
            int x = (int) Math.round(p[0] * (sw - w * s)), y = (int) Math.round(p[1] * (sh - h * s));
            g.pose().pushMatrix(); g.pose().translate(x, y); g.pose().scale((float) s, (float) s);
            m.render(g, mc, f, 0, 0);
            g.pose().popMatrix();
        }
    }
    public static int[] bounds(HudModule m, Font f, int sw, int sh) {
        double s = Config.theme.hudScale; double[] p = pos(m); int w = (int) (m.width(f) * s), h = (int) (m.height(f) * s);
        return new int[]{(int) Math.round(p[0] * (sw - w)), (int) Math.round(p[1] * (sh - h)), w, h};
    }
}
