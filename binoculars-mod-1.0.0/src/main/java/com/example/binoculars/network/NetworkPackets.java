package com.example.binoculars.network;

import com.example.binoculars.BinocularsMod;
import com.example.binoculars.network.PacketLockTarget;
import com.example.binoculars.network.PacketRequestKill;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NetworkPackets {
    private static int id = 0;
    public static void register() {
        BinocularsMod.CHANNEL.registerMessage(id++, PacketLockTarget.class, PacketLockTarget::encode, PacketLockTarget::decode, PacketLockTarget::handle);
        BinocularsMod.CHANNEL.registerMessage(id++, PacketRequestKill.class, PacketRequestKill::encode, PacketRequestKill::decode, PacketRequestKill::handle);
    }
}