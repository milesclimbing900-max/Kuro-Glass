package dev.kuro.glass.hud;

import dev.kuro.glass.core.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** A movable HUD widget. Position is stored as screen fractions so it survives resolution changes. */
public abstract class HudModule extends Module {
    public final double defX, defY;
    protected HudModule(String name, String glyph, String desc, double dx, double dy) { super(name, "HUD", glyph, desc); defX = dx; defY = dy; }
    public abstract int width(Font f); public abstract int height(Font f);
    public abstract void render(GuiGraphics g, Minecraft mc, Font f, int x, int y);
    @Override public boolean equals(Object o) { return this == o; }
}
