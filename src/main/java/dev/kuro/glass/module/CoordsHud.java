package dev.kuro.glass.module;

import dev.kuro.glass.core.*;
import dev.kuro.glass.hud.*;
import dev.kuro.glass.ui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.ItemStack;
import java.util.*;

public class CoordsHud extends HudModule {
    final Setting decimals = add(Setting.num("Decimals", 1, 0, 3, 1)), dir = add(Setting.bool("Facing", true));
    public CoordsHud() { super("Coordinates", "⌖", "XYZ and facing direction.", 0.02, 0.02); }
    String line(Minecraft mc) { var p = mc.player; String fm = "%." + (int) decimals.n + "f";
        return String.format("X " + fm + "  Y " + fm + "  Z " + fm, p.getX(), p.getY(), p.getZ()) + (dir.b ? "  " + p.getDirection().getName().toUpperCase() : ""); }
    @Override public int width(Font f) { Minecraft mc = Minecraft.getInstance(); return (mc.player == null ? 150 : f.width(line(mc))) + 24; }
    @Override public int height(Font f) { return 16; }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        Draw.hudBackground(g, x, y, width(f), 16); Theme t = Config.theme;
        g.drawString(f, glyph, x + 5, y + 4, Theme.alpha(t.accent, t.hudAlpha), false);
        if (mc.player != null) g.drawString(f, line(mc), x + 18, y + 4, Theme.alpha(t.textColor, t.hudAlpha), false);
    }
}
