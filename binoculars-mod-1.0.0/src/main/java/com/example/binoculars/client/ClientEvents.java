package com.example.binoculars.client;

import com.example.binoculars.ModItems;
import com.example.binoculars.BinocularsItem;
import com.example.binoculars.BinocularsMod;
import com.example.binoculars.network.PacketLockTarget;
import com.example.binoculars.network.PacketRequestKill;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static double zoom = 3.0; // default 3x
    @SubscribeEvent
    public static void onFov(FOVModifierEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.options.renderDebug) return;
        ItemStack stack = mc.player.getMainHandItem();
        if (stack.getItem() == ModItems.BINOCULARS.get()) {
            // if using item (right hold)
            if (mc.options.keyUse.isDown()) { // best-effort: player is using item
                float baseFov = event.getFOV();
                double factor = zoom;
                // reduce FOV to simulate zoom (lower fov -> zoomed)
                event.setFOV((float)(baseFov / factor));
            }
        }
    }

    @SubscribeEvent
    public static void onScroll(MouseScrollEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack stack = mc.player.getMainHandItem();
        if (stack.getItem() == ModItems.BINOCULARS.get() && mc.options.keyUse.isDown()) {
            double delta = event.getScrollDelta();
            if (delta > 0) zoom = Math.min(zoom + 0.5, 5.0);
            if (delta < 0) zoom = Math.max(zoom - 0.5, 3.0);
            event.setCanceled(true);
        }
    }
}