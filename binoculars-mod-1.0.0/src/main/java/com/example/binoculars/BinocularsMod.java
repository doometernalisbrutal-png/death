package com.example.binoculars;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.resources.ResourceLocation;

@Mod(BinocularsMod.MODID)
public class BinocularsMod {
    public static final String MODID = "binoculars";
    public static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;

    public BinocularsMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register items
        ModItems.register(bus);

        // Network channel
        CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
        );

        // register packets
        NetworkPackets.register();

        // common event handlers
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
        // client events
        MinecraftForge.EVENT_BUS.register(new com.example.binoculars.client.ClientEvents());
    }
}