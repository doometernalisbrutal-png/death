package com.example.binoculars;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BinocularsMod.MODID);

    public static final RegistryObject<Item> BINOCULARS = ITEMS.register("binoculars", () -> new BinocularsItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}