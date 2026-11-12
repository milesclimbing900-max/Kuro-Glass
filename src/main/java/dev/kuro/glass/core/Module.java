package dev.kuro.glass.core;

import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name, category, description;
    /** Glyph shown in the menu and HUD arraylist; user-editable. */
    public String glyph;
    public int bind = -1; public boolean bindDown;
    public boolean enabled, showInList = true;
    public final List<Setting> settings = new ArrayList<>();

    protected Module(String name, String category, String glyph, String description) {
        this.name = name; this.category = category; this.glyph = glyph; this.description = description; }
    protected <T extends Setting> T add(T s) { settings.add(s); return s; }
    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean v) { if (v == enabled) return; enabled = v; if (v) onEnable(); else onDisable(); Config.save(); }
    public void onEnable() {} public void onDisable() {} public void onTick(Minecraft mc) {}
    /** Short live status text for the HUD list (e.g. current value). */
    public String status() { return ""; }
}
