package com.example.binoculars;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.HashSet;
import java.util.Set;

public class BannedMobData extends SavedData {
    private static final String DATA_NAME = "binoculars_banned_mobs";
    private Set<String> banned = new HashSet<>();

    public static BannedMobData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(n -> new BannedMobData(), DATA_NAME);
    }

    public BannedMobData() {}

    @Override
    public CompoundTag save(CompoundTag p_189551_) {
        ListTag list = new ListTag();
        for (String s : banned) {
            list.add(StringTag.valueOf(s));
        }
        p_189551_.put("banned", list);
        return p_189551_;
    }

    public void addBanned(String reg) {
        banned.add(reg);
    }

    public boolean isBanned(String reg) {
        return banned.contains(reg);
    }

    public static BannedMobData load(CompoundTag tag) {
        BannedMobData d = new BannedMobData();
        if (tag.contains("banned")) {
            ListTag list = tag.getList("banned", 8);
            for (int i = 0; i < list.size(); i++) {
                d.banned.add(list.getString(i));
            }
        }
        return d;
    }
}