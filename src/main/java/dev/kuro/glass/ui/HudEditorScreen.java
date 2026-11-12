package dev.kuro.glass.ui;

import dev.kuro.glass.core.*;
import dev.kuro.glass.hud.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Drag HUD widgets anywhere. Click a widget while holding nothing to toggle it; Esc returns. */
public class HudEditorScreen extends Screen {
    private final Screen parent; private HudModule drag; private double ox, oy;
    public HudEditorScreen(Screen parent) { super(Component.literal("HUD Editor")); this.parent = parent; }
    @Override public void render(GuiGraphics g, int mx, int my, float dt) {
        g.fill(0, 0, width, height, 0x55000000);
        HudRenderer.renderAll(g, minecraft, true);
        for (Module m : ModuleManager.all()) if (m instanceof HudModule h) { int[] b = HudRenderer.bounds(h, font, width, height);
            int c = Theme.alpha(h.enabled ? Config.theme.accent : 0xFFFFFFFF, h.enabled ? 0.9 : 0.35);
            g.fill(b[0] - 1, b[1] - 1, b[0] + b[2] + 1, b[1], c); g.fill(b[0] - 1, b[1] + b[3], b[0] + b[2] + 1, b[1] + b[3] + 1, c);
            g.fill(b[0] - 1, b[1], b[0], b[1] + b[3], c); g.fill(b[0] + b[2], b[1], b[0] + b[2] + 1, b[1] + b[3], c);
            if (!h.enabled) g.drawString(font, h.name + " (off)", b[0], b[1] - 10, 0xAAFFFFFF, false); }
        g.drawCenteredString(font, "Drag widgets · right-click toggles · Esc to go back", width / 2, 8, 0xFFFFFFFF);
    }
    @Override public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        for (Module m : ModuleManager.all()) if (m instanceof HudModule h) { int[] b = HudRenderer.bounds(h, font, width, height);
            if (Draw.in(e.x(), e.y(), b[0], b[1], b[2], b[3])) { if (e.button() == 1) { h.toggle(); return true; } drag = h; ox = e.x() - b[0]; oy = e.y() - b[1]; return true; } }
        return false; }
    @Override public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (drag == null) return false; int[] b = HudRenderer.bounds(drag, font, width, height); double[] p = HudRenderer.pos(drag);
        double rx = Math.max(1, width - b[2]), ry = Math.max(1, height - b[3]);
        p[0] = Math.max(0, Math.min(1, (e.x() - ox) / rx)); p[1] = Math.max(0, Math.min(1, (e.y() - oy) / ry)); return true; }
    @Override public boolean mouseReleased(MouseButtonEvent e) { drag = null; Config.save(); return true; }
    @Override public boolean keyPressed(KeyEvent e) { if (e.key() == 256) { minecraft.setScreen(parent); return true; } return super.keyPressed(e); }
    @Override public boolean shouldCloseOnEsc() { return false; }
}
