package com.example.binoculars.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import com.example.binoculars.BinocularsMod;
import com.example.binoculars.ServerEvents;
import java.util.function.Supplier;
import java.util.UUID;

public class PacketLockTarget {
    public UUID target;

    public PacketLockTarget() {}
    public PacketLockTarget(UUID target) { this.target = target; }

    public static void encode(PacketLockTarget pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.target);
    }

    public static PacketLockTarget decode(FriendlyByteBuf buf) {
        PacketLockTarget p = new PacketLockTarget();
        p.target = buf.readUUID();
        return p;
    }

    public static void handle(PacketLockTarget pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            // if sender is null, it's client-to-server without player (ignore)
            if (player == null) return;
            Level world = player.level;
            Entity e = world.getEntity(pkt.target);
            if (e instanceof net.minecraft.world.entity.LivingEntity) {
                // apply glowing effect server-side so all players see it
                net.minecraft.world.entity.LivingEntity le = (net.minecraft.world.entity.LivingEntity)e;
                le.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 20, 0, false, false));
                // also add to a temporary team to color outline red (best-effort)
                ServerEvents.addTemporaryOutline(world, le);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}