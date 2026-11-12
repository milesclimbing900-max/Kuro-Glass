package dev.kuro.glass.ui;

import dev.kuro.glass.core.*;
import net.minecraft.client.gui.GuiGraphics;

/** Drawing helpers: rounded rects, liquid-glass panels, sci-fi HUD frames. Only uses GuiGraphics.fill for portability. */
public final class Draw {
    private Draw() {}
    public static void rrect(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        if (r == 0) { g.fill(x, y, x + w, y + h, c); return; }
        g.fill(x, y + r, x + w, y + h - r, c);
        for (int i = 0; i < r; i++) {
            int inset = (int) Math.round(r - Math.sqrt((double) r * r - (double) (r - i - 0.5) * (r - i - 0.5)));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, c);
            g.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, c);
        }
    }
    public static void rborder(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
        rrect(g, x, y, w, h, r, c);
    }
    /** Liquid glass: shadow, tinted body, bright rim, top specular sheen. */
    public static void glass(GuiGraphics g, int x, int y, int w, int h, int r, double strength) {
        Theme t = Config.theme;
        for (int i = 3; i >= 1; i--) rrect(g, x - i, y - i + 2, w + 2 * i, h + 2 * i, r + i, Theme.alpha(0xFF000000, t.shadow * 0.10 / i * 2));
        int rim = Theme.alpha(0xFFFFFFFF, t.borderAlpha * strength);
        rrect(g, x, y, w, h, r, rim);
        int body = t.tint ? blend(0xFFFFFFFF, t.accent, 0.18) : 0xFFFFFFFF;
        rrect(g, x + 1, y + 1, w - 2, h - 2, Math.max(0, r - 1), Theme.alpha(0xFF0B0F14, 0.30 + t.glassAlpha * 0.5 * strength));
        rrect(g, x + 1, y + 1, w - 2, h - 2, Math.max(0, r - 1), Theme.alpha(body, t.glassAlpha * 0.45 * strength));
        // specular sheen on the upper third
        rrect(g, x + 2, y + 2, w - 4, Math.max(2, h / 3), Math.max(0, r - 2), Theme.alpha(0xFFFFFFFF, 0.07 * strength));
    }
    public static void pill(GuiGraphics g, int x, int y, int w, int h, int c) { rrect(g, x, y, w, h, h / 2, c); }
    public static int blend(int a, int b, double t) {
        int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t), gg = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t), bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return 0xFF000000 | r << 16 | gg << 8 | bl;
    }
    /** HUD frame styles echo the supplied sci-fi HUD sheet; colour/opacity come from the theme. */
    public static void hudFrame(GuiGraphics g, int x, int y, int w, int h) {
        Theme t = Config.theme; int c = Theme.alpha(t.accent, t.hudAlpha);
        switch (t.frameStyle) {
            case "Bracket" -> { int l = 5;
                g.fill(x, y, x + l, y + 1, c); g.fill(x, y, x + 1, y + l, c);
                g.fill(x + w - l, y, x + w, y + 1, c); g.fill(x + w - 1, y, x + w, y + l, c);
                g.fill(x, y + h - 1, x + l, y + h, c); g.fill(x, y + h - l, x + 1, y + h, c);
                g.fill(x + w - l, y + h - 1, x + w, y + h, c); g.fill(x + w - 1, y + h - l, x + w, y + h, c); }
            case "Line" -> g.fill(x, y + h, x + w, y + h + 1, c);
            case "Chevron" -> { g.fill(x, y, x + 2, y + h, c); for (int i = 0; i < 3; i++) g.fill(x + w - 3 + i, y + i, x + w - 2 + i, y + 1 + i, c); g.fill(x + w - 1, y, x + w, y + h, Theme.alpha(t.accent, t.hudAlpha * 0.5)); }
            default -> {}
        }
    }
    public static void hudBackground(GuiGraphics g, int x, int y, int w, int h) {
        Theme t = Config.theme; rrect(g, x, y, w, h, (int) Math.min(t.radius, 6), Theme.alpha(0xFF0B0F14, 0.25 + t.glassAlpha * 0.4)); hudFrame(g, x, y, w, h);
    }
    public static boolean in(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
}
