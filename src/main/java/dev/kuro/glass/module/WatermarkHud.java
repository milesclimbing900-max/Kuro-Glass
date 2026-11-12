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

public class WatermarkHud extends HudModule {
    final Setting text = add(Setting.choice("Style", 0, "Kuro ◆ Glass", "KURO", "kuro.glass"));
    public WatermarkHud() { super("Watermark", "✦", "Client name badge.", 0.98, 0.02); }
    @Override public int width(Font f) { return f.width(text.option()) + 24; } @Override public int height(Font f) { return 16; }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        Draw.hudBackground(g, x, y, width(f), 16); Theme t = Config.theme;
        g.drawString(f, glyph, x + 5, y + 4, Theme.alpha(t.accent, t.hudAlpha), false);
        g.drawString(f, text.option(), x + 18, y + 4, Theme.alpha(t.textColor, t.hudAlpha), false);
    }
}
