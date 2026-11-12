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

public class Keystrokes extends HudModule {
    final Setting size = add(Setting.num("Key size", 18, 12, 28, 1)), mouse = add(Setting.bool("Show mouse", true));
    public Keystrokes() { super("Keystrokes", "✥", "WASD and mouse buttons overlay.", 0.02, 0.6); }
    int k() { return (int) size.n + 2; }
    @Override public int width(Font f) { return k() * 3; }
    @Override public int height(Font f) { return k() * (mouse.b ? 3 : 2); }
    void key(GuiGraphics g, Font f, int x, int y, int w, String l, boolean down) {
        Theme t = Config.theme; Draw.rrect(g, x, y, w - 2, k() - 2, 3, down ? Theme.alpha(t.accent, 0.75) : Theme.alpha(0xFF0B0F14, 0.45));
        g.drawString(f, l, x + (w - 2 - f.width(l)) / 2, y + (k() - 2 - 8) / 2, Theme.alpha(t.textColor, t.hudAlpha), false);
    }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        var o = mc.options; int k = k();
        key(g, f, x + k, y, k, "W", o.keyUp.isDown()); key(g, f, x, y + k, k, "A", o.keyLeft.isDown());
        key(g, f, x + k, y + k, k, "S", o.keyDown.isDown()); key(g, f, x + 2 * k, y + k, k, "D", o.keyRight.isDown());
        if (mouse.b) { key(g, f, x, y + 2 * k, k + k / 2, "LMB", o.keyAttack.isDown()); key(g, f, x + k + k / 2, y + 2 * k, k + k / 2, "RMB", o.keyUse.isDown()); }
    }
}
