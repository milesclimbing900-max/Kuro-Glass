package dev.kuro.glass;

import dev.kuro.glass.core.*;
import dev.kuro.glass.hud.HudRenderer;
import dev.kuro.glass.ui.GlassScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KuroGlass implements ClientModInitializer {
    public static KeyMapping openKey;
    @Override public void onInitializeClient() {
        Config.load();
        ModuleManager.init();
        Config.applyToModules();
        HudRenderer.register();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.kuroglass.open",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) mc.setScreen(new GlassScreen());
            if (mc.player != null) for (Module m : ModuleManager.all()) {
                if (m.bind > 0 && InputConstants.isKeyDown(mc.getWindow(), m.bind) != m.bindDown) {
                    m.bindDown = !m.bindDown; if (m.bindDown && mc.screen == null) m.toggle();
                }
                if (m.enabled) m.onTick(mc);
            }
        });
    }
}
