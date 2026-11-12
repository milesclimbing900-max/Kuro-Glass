package dev.kuro.glass.core;

import java.util.LinkedHashMap;
import java.util.Map;

/** Every visual knob the user can change in the Appearance tab. Persisted by Config. */
public class Theme {
    public int accent = 0xFF3B9BFF;      // ARGB
    public int textColor = 0xFFF2F6FA;
    public double glassAlpha = 0.34;     // panel fill opacity
    public double borderAlpha = 0.55;
    public double radius = 10;
    public double shadow = 0.35;
    public double hudAlpha = 0.85;
    public double hudScale = 1.0;
    public double uiScale = 1.0;
    public boolean tint = true;          // tint glass with accent
    public boolean hudFrames = true;     // draw sci-fi corner brackets on HUD widgets
    public String frameStyle = "Bracket"; // Bracket | Line | None | Chevron
    public String glyphOn = "◆", glyphOff = "◇";
    public static final Map<String, int[]> PRESETS = new LinkedHashMap<>();
    static {
        PRESETS.put("Ocean", new int[]{0xFF3B9BFF, 0xFFF2F6FA});
        PRESETS.put("Violet HUD", new int[]{0xFF8B6CFF, 0xFFEDE8FF});
        PRESETS.put("Sakura", new int[]{0xFFFF8FB8, 0xFFFFF0F5});
        PRESETS.put("Mint", new int[]{0xFF4FE0B0, 0xFFEFFFF8});
        PRESETS.put("Amber", new int[]{0xFFFFB347, 0xFFFFF6E6});
    }
    public static int alpha(int argb, double a) { int al = (int) Math.round(Math.max(0, Math.min(1, a)) * ((argb >>> 24) == 0 ? 255 : (argb >>> 24))); return (al << 24) | (argb & 0xFFFFFF); }
    public static int hsv(float h, float s, float v) { return 0xFF000000 | java.awt.Color.HSBtoRGB(h, s, v) & 0xFFFFFF; }
}
