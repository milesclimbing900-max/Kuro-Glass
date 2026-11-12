package dev.kuro.glass.ui;

import dev.kuro.glass.core.*;
import dev.kuro.glass.hud.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.function.DoubleConsumer;

/** Draggable, resizable liquid-glass window: sidebar tabs, module cards, per-module detail pane, Appearance editor. */
public class GlassScreen extends Screen {
    private record Hit(int x, int y, int w, int h, Runnable left, Runnable right, DoubleConsumer drag) {}
    private final List<Hit> hits = new ArrayList<>();
    private DoubleConsumer activeDrag; private int dragMode; // 1 move, 2 resize
    private double dox, doy;
    private String tab; private Module selected; private int scroll, dScroll; private boolean listenBind, listenGlyph;
    private static final String[] PALETTE = {"◆","◇","●","○","★","☆","✦","✧","♥","♦","♠","♣","☠","☯","⚜","☄","☾","☼","✪","❖","➤","⌖","⛨","☰","ϟ","Ω","∞","✿","♪","☢"};
    private static final Map<String, String> CAT_ICON = Map.of("Movement", "➤", "Render", "☀", "HUD", "▣", "Appearance", "✦");

    public GlassScreen() { super(Component.literal("Kuro Glass")); }
    @Override public boolean isPauseScreen() { return false; }
    @Override protected void init() { if (tab == null) tab = ModuleManager.categories().get(0); if (Config.winX < 0) { Config.winX = (width - Config.winW) / 2; Config.winY = (height - Config.winH) / 2; } }
    @Override public void removed() { Config.save(); }

    private int T() { return Theme.alpha(Config.theme.textColor, 1.0); }
    private int dim() { return Theme.alpha(Config.theme.textColor, 0.55); }

    @Override public void render(GuiGraphics g, int mx, int my, float dt) {
        super.render(g, mx, my, dt); // vanilla menu backdrop blur gives the frosted look behind the glass
        hits.clear(); Font f = font; Theme t = Config.theme;
        int x = (int) Config.winX, y = (int) Config.winY, w = (int) Config.winW, h = (int) Config.winH; int r = (int) t.radius + 4;
        Draw.glass(g, x, y, w, h, r, 1.0);
        // title bar (drag to move)
        g.drawString(f, "◆ Kuro Glass", x + 16, y + 10, T(), false);
        g.drawString(f, "✕", x + w - 18, y + 10, dim(), false); hits.add(new Hit(x + w - 24, y + 4, 20, 18, this::onClose, null, null));
        hits.add(new Hit(x, y, w - 26, 26, null, null, null)); // marker for move, handled in mouseClicked
        // sidebar
        List<String> tabs = new ArrayList<>(ModuleManager.categories()); tabs.add("Appearance");
        int sy = y + 36; Draw.rrect(g, x + 8, sy - 4, 34, tabs.size() * 32 + 4, 17, Theme.alpha(0xFFFFFFFF, 0.08));
        for (String c : tabs) { boolean on = c.equals(tab);
            if (on) Draw.rrect(g, x + 11, sy, 28, 28, 14, Theme.alpha(t.accent, 0.85));
            String ic = CAT_ICON.getOrDefault(c, "◈"); g.drawString(f, ic, x + 25 - f.width(ic) / 2, sy + 10, on ? 0xFFFFFFFF : dim(), false);
            final String cc = c; hits.add(new Hit(x + 11, sy, 28, 28, () -> { tab = cc; selected = null; scroll = 0; dScroll = 0; }, null, null)); sy += 32; }
        int cx = x + 54, cy = y + 34, cw = w - 66, ch = h - 46;
        g.drawString(f, tab, cx, y + 10, dim(), false);
        if (tab.equals("Appearance")) appearance(g, f, mx, my, cx, cy, cw, ch); else modules(g, f, mx, my, cx, cy, cw, ch);
        // resize grip
        g.drawString(f, "◢", x + w - 12, y + h - 12, dim(), false);
        hits.add(new Hit(x + w - 16, y + h - 16, 16, 16, null, null, null));
    }

    // ---------- module cards + detail ----------
    private void modules(GuiGraphics g, Font f, int mx, int my, int x, int y, int w, int h) {
        int detailW = selected != null ? Math.min(210, w / 2) : 0, gw = w - detailW - (selected != null ? 8 : 0);
        int cols = Math.max(1, gw / 130), cwid = (gw - (cols - 1) * 8) / cols, chgt = 46;
        List<Module> ms = new ArrayList<>(); for (Module m : ModuleManager.all()) if (m.category.equals(tab)) ms.add(m);
        int rows = (ms.size() + cols - 1) / cols; scroll = Math.max(0, Math.min(scroll, Math.max(0, rows * (chgt + 8) - h)));
        g.enableScissor(x - 2, y, x + gw + 2, y + h);
        for (int i = 0; i < ms.size(); i++) { Module m = ms.get(i); int cx = x + (i % cols) * (cwid + 8), cy = y + (i / cols) * (chgt + 8) - scroll;
            Draw.glass(g, cx, cy, cwid, chgt, 10, m == selected ? 1.4 : 0.8);
            if (m.enabled) Draw.rrect(g, cx + 1, cy + 1, cwid - 2, chgt - 2, 9, Theme.alpha(Config.theme.accent, 0.25));
            g.drawString(f, m.enabled ? m.glyph : Config.theme.glyphOff, cx + 10, cy + 10, m.enabled ? Config.theme.accent : dim(), false);
            g.drawString(f, m.name, cx + 24, cy + 10, T(), false);
            String d = f.plainSubstrByWidth(m.description, cwid - 60); g.drawString(f, d, cx + 10, cy + 28, dim(), false);
            Draw.pill(g, cx + cwid - 30, cy + 8, 22, 12, m.enabled ? Config.theme.accent : Theme.alpha(0xFFFFFFFF, 0.2));
            Draw.rrect(g, cx + cwid - (m.enabled ? 19 : 29), cy + 9, 10, 10, 5, 0xFFFFFFFF);
            if (cy + chgt > y && cy < y + h) hits.add(new Hit(cx, cy, cwid, chgt, m::toggle, () -> { selected = m; dScroll = 0; }, null)); }
        g.disableScissor();
        if (selected != null) detail(g, f, x + gw + 8, y, detailW, h);
        if (ms.isEmpty()) g.drawString(f, "Nothing here yet", x, y, dim(), false);
        g.drawString(f, "Left-click toggles · right-click opens settings", x, y + h + 4, dim(), false);
    }

    private void detail(GuiGraphics g, Font f, int x, int y, int w, int h) {
        Module m = selected; Draw.glass(g, x, y, w, h, 10, 1.1);
        g.drawString(f, m.glyph + " " + m.name, x + 10, y + 8, T(), false);
        int yy = y + 24 - dScroll;
        g.enableScissor(x, y + 20, x + w, y + h - 4);
        yy = button(g, f, x + 10, yy, w - 20, listenBind ? "Press a key… (Esc clears)" : "Bind: " + (m.bind > 0 ? GLFW.glfwGetKeyName(m.bind, 0) == null ? "key " + m.bind : GLFW.glfwGetKeyName(m.bind, 0).toUpperCase() : "none"), listenBind, () -> { listenBind = !listenBind; listenGlyph = false; });
        yy = button(g, f, x + 10, yy, w - 20, "Show in module list: " + (m.showInList ? "yes" : "no"), false, () -> { m.showInList = !m.showInList; Config.save(); });
        g.drawString(f, listenGlyph ? "Type any character, or pick:" : "Glyph (click, then type or pick):", x + 10, yy + 2, dim(), false); yy += 12;
        g.drawString(f, m.glyph, x + 10, yy + 3, Config.theme.accent, false); hits.add(new Hit(x + 8, yy, 24, 14, () -> listenGlyph = !listenGlyph, null, null));
        int px = x + 36, py = yy; for (String s : PALETTE) { if (px + 12 > x + w - 6) { px = x + 10; py += 13; if (py == yy) py += 13; }
            g.drawString(f, s, px, py + 3, s.equals(m.glyph) ? Config.theme.accent : T(), false); final String ss = s; hits.add(new Hit(px - 1, py, 12, 13, () -> { m.glyph = ss; Config.save(); }, null, null)); px += 12; }
        yy = py + 20;
        for (Setting s : m.settings) yy = setting(g, f, x + 10, yy, w - 20, s);
        g.disableScissor();
    }

    private int setting(GuiGraphics g, Font f, int x, int y, int w, Setting s) {
        switch (s.type) {
            case BOOL -> { return button(g, f, x, y, w, s.name + ": " + (s.b ? "on" : "off"), s.b, () -> { s.b = !s.b; Config.save(); }); }
            case ENUM -> { return button(g, f, x, y, w, s.name + ": " + s.option(), false, () -> { s.cycle(); Config.save(); }); }
            case NUM -> { return slider(g, f, x, y, w, s.name, s.n, s.min, s.max, v -> { s.setNum(v); }, s.step < 1 ? "%.2f" : "%.0f"); }
            case COLOR -> { return hue(g, f, x, y, w, s.name, v -> s.color = Theme.hsv((float) v, 0.75f, 1f)); }
        } return y;
    }

    // ---------- appearance ----------
    private void appearance(GuiGraphics g, Font f, int mx, int my, int x, int y, int w, int h) {
        Theme t = Config.theme; int colw = (w - 10) / 2; int yy = y - scroll, y2 = y - scroll;
        g.enableScissor(x - 2, y, x + w + 2, y + h);
        g.drawString(f, "Presets", x, yy, dim(), false); yy += 12; int px = x;
        for (var e : Theme.PRESETS.entrySet()) { int bw = f.width(e.getKey()) + 14; final var v = e.getValue();
            Draw.rrect(g, px, yy, bw, 16, 8, Theme.alpha(v[0], 0.7)); g.drawString(f, e.getKey(), px + 7, yy + 4, 0xFFFFFFFF, false);
            hits.add(new Hit(px, yy, bw, 16, () -> { t.accent = v[0]; t.textColor = v[1]; Config.save(); }, null, null)); px += bw + 4; }
        yy += 24;
        yy = hue(g, f, x, yy, colw, "Accent hue", v -> t.accent = Theme.hsv((float) v, 0.7f, 1f));
        yy = slider(g, f, x, yy, colw, "Glass opacity", t.glassAlpha, 0, 1, v -> t.glassAlpha = v, "%.2f");
        yy = slider(g, f, x, yy, colw, "Border opacity", t.borderAlpha, 0, 1, v -> t.borderAlpha = v, "%.2f");
        yy = slider(g, f, x, yy, colw, "Corner radius", t.radius, 0, 20, v -> t.radius = Math.round(v), "%.0f");
        yy = slider(g, f, x, yy, colw, "Shadow", t.shadow, 0, 1, v -> t.shadow = v, "%.2f");
        int x2 = x + colw + 10;
        y2 = hue(g, f, x2, y2 + 0 + 36, colw, "Text hue (right = white)", v -> t.textColor = v > 0.98 ? 0xFFF2F6FA : Theme.hsv((float) v, 0.12f, 1f));
        y2 = slider(g, f, x2, y2, colw, "HUD opacity", t.hudAlpha, 0.1, 1, v -> t.hudAlpha = v, "%.2f");
        y2 = slider(g, f, x2, y2, colw, "HUD scale", t.hudScale, 0.6, 2, v -> t.hudScale = Math.round(v * 20) / 20.0, "%.2f");
        y2 = button(g, f, x2, y2, colw, "HUD frame: " + t.frameStyle, false, () -> { String[] s = {"Bracket", "Line", "Chevron", "None"}; t.frameStyle = s[(Arrays.asList(s).indexOf(t.frameStyle) + 1) % s.length]; Config.save(); });
        y2 = button(g, f, x2, y2, colw, "Tint glass with accent: " + (t.tint ? "on" : "off"), t.tint, () -> { t.tint = !t.tint; Config.save(); });
        y2 = button(g, f, x2, y2, colw, "Edit HUD layout (drag widgets)", false, () -> minecraft.setScreen(new HudEditorScreen(this)));
        g.disableScissor();
    }

    // ---------- widgets ----------
    private int button(GuiGraphics g, Font f, int x, int y, int w, String label, boolean on, Runnable click) {
        Draw.rrect(g, x, y, w, 16, 8, on ? Theme.alpha(Config.theme.accent, 0.7) : Theme.alpha(0xFFFFFFFF, 0.12));
        g.drawString(f, f.plainSubstrByWidth(label, w - 10), x + 6, y + 4, T(), false); hits.add(new Hit(x, y, w, 16, click, null, null)); return y + 20; }
    private int slider(GuiGraphics g, Font f, int x, int y, int w, String label, double v, double min, double max, DoubleConsumer set, String fmt) {
        g.drawString(f, label, x, y, dim(), false); String vs = String.format(fmt, v); g.drawString(f, vs, x + w - f.width(vs), y, T(), false);
        Draw.pill(g, x, y + 12, w, 6, Theme.alpha(0xFFFFFFFF, 0.15)); int kx = (int) (x + (v - min) / (max - min) * (w - 8));
        Draw.pill(g, x, y + 12, Math.max(6, kx - x + 4), 6, Theme.alpha(Config.theme.accent, 0.8)); Draw.rrect(g, kx, y + 10, 8, 10, 4, 0xFFFFFFFF);
        final int fx = x, fw = w; DoubleConsumer d = mxv -> { set.accept(min + Math.max(0, Math.min(1, (mxv - fx - 4) / (fw - 8))) * (max - min)); };
        hits.add(new Hit(x, y + 6, w, 16, null, null, d)); return y + 26; }
    private int hue(GuiGraphics g, Font f, int x, int y, int w, String label, DoubleConsumer set) {
        g.drawString(f, label, x, y, dim(), false);
        int n = Math.max(8, w / 3); for (int i = 0; i < n; i++) g.fill(x + i * w / n, y + 12, x + (i + 1) * w / n + 1, y + 20, Theme.hsv(i / (float) n, 0.75f, 1f));
        final int fx = x, fw = w; hits.add(new Hit(x, y + 10, w, 12, null, null, mxv -> set.accept(Math.max(0, Math.min(1, (mxv - fx) / (double) fw))))); return y + 28; }

    // ---------- input ----------
    @Override public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        double mx = e.x(), my = e.y(); int b = e.button(); int x = (int) Config.winX, y = (int) Config.winY, w = (int) Config.winW, h = (int) Config.winH;
        for (int i = hits.size() - 1; i >= 0; i--) { Hit k = hits.get(i); if (!Draw.in(mx, my, k.x, k.y, k.w, k.h)) continue;
            if (k.drag != null) { activeDrag = k.drag; k.drag.accept(mx); return true; }
            if (b == 0 && k.left != null) { k.left.run(); Config.save(); return true; } if (b == 1 && k.right != null) { k.right.run(); return true; } }
        if (Draw.in(mx, my, x + w - 16, y + h - 16, 16, 16)) { dragMode = 2; return true; }
        if (Draw.in(mx, my, x, y, w - 26, 26)) { dragMode = 1; dox = mx - x; doy = my - y; return true; }
        return super.mouseClicked(e, dbl);
    }
    @Override public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (activeDrag != null) { activeDrag.accept(e.x()); return true; }
        if (dragMode == 1) { Config.winX = Math.max(0, Math.min(width - 60, e.x() - dox)); Config.winY = Math.max(0, Math.min(height - 40, e.y() - doy)); return true; }
        if (dragMode == 2) { Config.winW = Math.max(420, Math.min(width - Config.winX, e.x() - Config.winX)); Config.winH = Math.max(260, Math.min(height - Config.winY, e.y() - Config.winY)); return true; }
        return false;
    }
    @Override public boolean mouseReleased(MouseButtonEvent e) { activeDrag = null; dragMode = 0; Config.save(); return true; }
    @Override public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (selected != null && mx > Config.winX + Config.winW - 230) dScroll = Math.max(0, dScroll - (int) (sy * 14)); else scroll = Math.max(0, scroll - (int) (sy * 18)); return true; }
    @Override public boolean keyPressed(KeyEvent e) {
        if (listenBind && selected != null) { selected.bind = e.key() == GLFW.GLFW_KEY_ESCAPE ? -1 : e.key(); listenBind = false; Config.save(); return true; }
        return super.keyPressed(e); }
    @Override public boolean charTyped(CharacterEvent e) {
        if (listenGlyph && selected != null) { selected.glyph = new String(Character.toChars(e.codepoint())); listenGlyph = false; Config.save(); return true; } return super.charTyped(e); }
}
