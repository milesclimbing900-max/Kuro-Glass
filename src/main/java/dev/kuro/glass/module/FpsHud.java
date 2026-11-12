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

public class FpsHud extends HudModule {
    public FpsHud() { super("FPS / Ping", "◔", "Frame rate and latency.", 0.02, 0.08); }
    String line(Minecraft mc) { int ping = 0; if (mc.getConnection() != null && mc.player != null) { var e = mc.getConnection().getPlayerInfo(mc.player.getUUID()); if (e != null) ping = e.getLatency(); }
        return mc.getFps() + " FPS  " + ping + " ms"; }
    @Override public int width(Font f) { return f.width(line(Minecraft.getInstance())) + 24; }
    @Override public int height(Font f) { return 16; }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        Draw.hudBackground(g, x, y, width(f), 16); Theme t = Config.theme;
        g.drawString(f, glyph, x + 5, y + 4, Theme.alpha(t.accent, t.hudAlpha), false);
        g.drawString(f, line(mc), x + 18, y + 4, Theme.alpha(t.textColor, t.hudAlpha), false);
    }
}
