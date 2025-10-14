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

public class PacketRequestKill {
    public UUID target;

    public PacketRequestKill() {}
    public PacketRequestKill(UUID target) { this.target = target; }

    public static void encode(PacketRequestKill pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.target);
    }

    public static PacketRequestKill decode(FriendlyByteBuf buf) {
        PacketRequestKill p = new PacketRequestKill();
        p.target = buf.readUUID();
        return p;
    }

    public static void handle(PacketRequestKill pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level world = player.level;
            Entity e = world.getEntity(pkt.target);
            if (e instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity le = (net.minecraft.world.entity.LivingEntity)e;
                // kill instantly and bypass totem by directly calling kill() and marking as removed
                // record type to banned list
                ServerEvents.markEntityTypeBanned(world, le.getType().getRegistryName().toString());
                le.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}