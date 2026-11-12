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

public class ArrayListHud extends HudModule {
    final Setting status = add(Setting.bool("Show status", true)), sortLen = add(Setting.bool("Sort by length", true));
    public ArrayListHud() { super("Module List", "☰", "Enabled modules with their glyphs.", 0.98, 0.12); }
    List<String> lines() { List<String> l = new ArrayList<>(); for (Module m : ModuleManager.all()) if (m.enabled && m.showInList && !(m instanceof HudModule)) {
        String s = m.glyph + " " + m.name; if (status.b && !m.status().isEmpty()) s += " ▸ " + m.status(); l.add(s); }
        if (sortLen.b) l.sort((a, b) -> b.length() - a.length()); return l; }
    @Override public int width(Font f) { int w = 80; for (String s : lines()) w = Math.max(w, f.width(s) + 12); return w; }
    @Override public int height(Font f) { return Math.max(1, lines().size()) * 12 + 4; }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        int w = width(f); Draw.hudBackground(g, x, y, w, height(f)); Theme t = Config.theme; int i = 0; List<String> ls = lines();
        if (ls.isEmpty()) g.drawString(f, "▫ no modules on", x + 6, y + 4, Theme.alpha(t.textColor, t.hudAlpha * 0.5), false);
        for (String s : ls) { g.drawString(f, s, x + 6, y + 4 + i++ * 12, Theme.alpha(t.textColor, t.hudAlpha), false); }
    }
}
