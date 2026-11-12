package dev.kuro.glass.module;

import dev.kuro.glass.core.*;
import dev.kuro.glass.hud.*;
import dev.kuro.glass.ui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.ItemStack;
import java.util.*;

public class ArmorHudMod extends HudModule {
    public ArmorHudMod() { super("Armor", "⛨", "Equipped armour and durability.", 0.5, 0.86); }
    @Override public int width(Font f) { return 4 * 20 + 6; } @Override public int height(Font f) { return 22; }
    @Override public void render(GuiGraphics g, Minecraft mc, Font f, int x, int y) {
        Draw.hudBackground(g, x, y, width(f), height(f)); if (mc.player == null) return;
        EquipmentSlot[] s = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < 4; i++) { ItemStack st = mc.player.getItemBySlot(s[i]); if (st.isEmpty()) continue; g.renderItem(st, x + 4 + i * 20, y + 3); g.renderItemDecorations(f, st, x + 4 + i * 20, y + 3); }
    }
}
