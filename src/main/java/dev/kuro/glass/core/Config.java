package dev.kuro.glass.core;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;

/** JSON persistence: theme, window layout, HUD positions, per-module state/binds/glyphs/settings. */
public class Config {
    public static Theme theme = new Theme();
    public static final Map<String, double[]> hudPos = new HashMap<>(); // id -> {x,y} in 0..1 of screen
    public static double winX = -1, winY = -1, winW = 560, winH = 340;
    private static JsonObject modulesJson = new JsonObject();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("kuroglass.json"); }

    public static void load() {
        try { if (!Files.exists(file())) return;
            JsonObject o = JsonParser.parseString(Files.readString(file())).getAsJsonObject();
            if (o.has("theme")) theme = GSON.fromJson(o.get("theme"), Theme.class);
            if (o.has("hud")) for (var e : o.getAsJsonObject("hud").entrySet()) { JsonArray a = e.getValue().getAsJsonArray(); hudPos.put(e.getKey(), new double[]{a.get(0).getAsDouble(), a.get(1).getAsDouble()}); }
            if (o.has("window")) { JsonArray w = o.getAsJsonArray("window"); winX = w.get(0).getAsDouble(); winY = w.get(1).getAsDouble(); winW = w.get(2).getAsDouble(); winH = w.get(3).getAsDouble(); }
            if (o.has("modules")) modulesJson = o.getAsJsonObject("modules");
        } catch (Exception ex) { System.err.println("[KuroGlass] config load failed: " + ex); }
    }
    public static void applyToModules() {
        for (Module m : ModuleManager.all()) { JsonObject j = modulesJson.has(m.name) ? modulesJson.getAsJsonObject(m.name) : null; if (j == null) continue;
            if (j.has("bind")) m.bind = j.get("bind").getAsInt();
            if (j.has("glyph")) m.glyph = j.get("glyph").getAsString();
            if (j.has("list")) m.showInList = j.get("list").getAsBoolean();
            for (Setting s : m.settings) if (j.has(s.name)) { JsonElement v = j.get(s.name); switch (s.type) {
                case BOOL -> s.b = v.getAsBoolean(); case NUM -> s.n = v.getAsDouble(); case ENUM -> s.idx = Math.min(v.getAsInt(), s.options.size() - 1); case COLOR -> s.color = v.getAsInt(); } }
            if (j.has("on") && j.get("on").getAsBoolean()) { m.enabled = true; m.onEnable(); } }
    }
    public static void save() {
        try { JsonObject o = new JsonObject(); o.add("theme", GSON.toJsonTree(theme));
            JsonObject h = new JsonObject(); hudPos.forEach((k, v) -> { JsonArray a = new JsonArray(); a.add(v[0]); a.add(v[1]); h.add(k, a); }); o.add("hud", h);
            JsonArray w = new JsonArray(); w.add(winX); w.add(winY); w.add(winW); w.add(winH); o.add("window", w);
            JsonObject ms = new JsonObject(); for (Module m : ModuleManager.all()) { JsonObject j = new JsonObject();
                j.addProperty("on", m.enabled); j.addProperty("bind", m.bind); j.addProperty("glyph", m.glyph); j.addProperty("list", m.showInList);
                for (Setting s : m.settings) switch (s.type) { case BOOL -> j.addProperty(s.name, s.b); case NUM -> j.addProperty(s.name, s.n); case ENUM -> j.addProperty(s.name, s.idx); case COLOR -> j.addProperty(s.name, s.color); }
                ms.add(m.name, j); }
            o.add("modules", ms); Files.writeString(file(), GSON.toJson(o));
        } catch (Exception ex) { System.err.println("[KuroGlass] config save failed: " + ex); }
    }
}
