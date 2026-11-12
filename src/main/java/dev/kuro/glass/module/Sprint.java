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

public class Sprint extends Module {
    public Sprint() { super("Sprint", "Movement", "➤", "Always sprint while moving forward."); }
    @Override public void onTick(Minecraft mc) { if (mc.player.input.hasForwardImpulse() && !mc.player.horizontalCollision) mc.options.keySprint.setDown(true); }
    @Override public String status() { return "auto"; }
}
