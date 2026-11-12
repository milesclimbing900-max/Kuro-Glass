package dev.kuro.glass.core;

import dev.kuro.glass.module.*;
import java.util.*;

public class ModuleManager {
    private static final List<BaseModule> MODULES = new ArrayList<>();
    public static void init() {
        Collections.addAll(MODULES,
            new Sprint(), new Fullbright(),
            new Keystrokes(), new CoordsHud(), new FpsHud(), new ArmorHudMod(), new ArrayListHud(), new WatermarkHud());
    }
    public static List<BaseModule> all() { return MODULES; }
    public static List<String> categories() { LinkedHashSet<String> c = new LinkedHashSet<>(); for (BaseModule m : MODULES) c.add(m.category); return new ArrayList<>(c); }
    public static <T extends BaseModule> T get(Class<T> c) { for (BaseModule m : MODULES) if (c.isInstance(m)) return c.cast(m); return null; }
}
