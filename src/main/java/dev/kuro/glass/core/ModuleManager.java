package dev.kuro.glass.core;

import dev.kuro.glass.module.*;
import java.util.*;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    public static void init() {
        Collections.addAll(MODULES,
            new Sprint(), new Fullbright(),
            new Keystrokes(), new CoordsHud(), new FpsHud(), new ArmorHudMod(), new ArrayListHud(), new WatermarkHud());
    }
    public static List<Module> all() { return MODULES; }
    public static List<String> categories() { LinkedHashSet<String> c = new LinkedHashSet<>(); for (Module m : MODULES) c.add(m.category); return new ArrayList<>(c); }
    public static <T extends Module> T get(Class<T> c) { for (Module m : MODULES) if (c.isInstance(m)) return c.cast(m); return null; }
}
