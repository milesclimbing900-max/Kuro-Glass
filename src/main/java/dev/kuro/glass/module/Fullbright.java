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

public class Fullbright extends Module {
    public Fullbright() { super("Fullbright", "Render", "☀", "Client-side night vision for full brightness."); }
    @Override public void onTick(Minecraft mc) { mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, false)); }
    @Override public void onDisable() { Minecraft mc = Minecraft.getInstance(); if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION); }
}
