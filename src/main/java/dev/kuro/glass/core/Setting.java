package dev.kuro.glass.core;

import java.util.List;

/** One configurable value on a module. Types: BOOL, NUM, ENUM, COLOR(int ARGB stored in num as long). */
public class Setting {
    public enum Type { BOOL, NUM, ENUM, COLOR }
    public final String name; public final Type type;
    public boolean b; public double n, min, max, step; public int idx; public List<String> options; public int color;
    private Setting(String name, Type t) { this.name = name; this.type = t; }
    public static Setting bool(String n, boolean v) { Setting s = new Setting(n, Type.BOOL); s.b = v; return s; }
    public static Setting num(String n, double v, double min, double max, double step) {
        Setting s = new Setting(n, Type.NUM); s.n = v; s.min = min; s.max = max; s.step = step; return s; }
    public static Setting choice(String n, int idx, String... opts) {
        Setting s = new Setting(n, Type.ENUM); s.idx = idx; s.options = List.of(opts); return s; }
    public static Setting color(String n, int argb) { Setting s = new Setting(n, Type.COLOR); s.color = argb; return s; }
    public String option() { return options.get(idx); }
    public void cycle() { idx = (idx + 1) % options.size(); }
    public void setNum(double v) { n = Math.max(min, Math.min(max, step > 0 ? Math.round(v / step) * step : v)); }
}
