# Kuro Glass

Fabric client mod for **Minecraft 1.21.11** — a from-scratch rebuild of Kuro's GUI as a customisable liquid-glass interface.

## Build
```
./gradlew build      # jar in build/libs/
./gradlew runClient
```
Requires JDK 21. CI builds on every push (`.github/workflows/build.yml`).
If a dependency version fails to resolve, update `loader_version` / `fabric_version` in `gradle.properties` (https://fabricmc.net/develop/).

## Use
- **Right Shift** opens the menu (rebind in Controls).
- Left-click a card toggles a module, right-click opens its settings (bind, glyph, options).
- **Appearance** tab: presets, accent/text hue, glass/border/HUD opacity, corner radius, shadow, HUD scale, HUD frame style.
- **Edit HUD layout** lets you drag every widget; positions are saved.
- Config: `config/kuroglass.json`.

## Add a module
Extend `Module` (or `HudModule` for movable widgets), add settings with `add(Setting.num/bool/choice/color(...))`, register it in `ModuleManager.init()`. The menu, binds, glyph picker and saving are automatic.
