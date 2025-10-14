package com.example.binoculars;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

import java.util.HashSet;
import java.util.Set;

public class ServerEvents {
    // temporary: add entity to a red-outlining team so outline appears red for clients
    public static void addTemporaryOutline(Level world, LivingEntity e) {
        if (!(world instanceof ServerLevel)) return;
        ServerLevel s = (ServerLevel)world;
        Scoreboard sb = s.getScoreboard();
        String teamName = "binoculars_red_outline";
        Team team = sb.getPlayerTeam(teamName);
        if (team == null) {
            team = sb.addPlayerTeam(teamName);
            team.setColor(net.minecraft.world.scores.Team.Visibility.ALWAYS, null);
        }
        sb.addPlayerToTeam(e.getStringUUID(), team);
    }

    public static void markEntityTypeBanned(Level world, net.minecraft.world.entity.EntityType<?> type) {
        markEntityTypeBanned(world, type.getRegistryName().toString());
    }

    public static void markEntityTypeBanned(Level world, String registryName) {
        if (!(world instanceof ServerLevel)) return;
        ServerLevel s = (ServerLevel)world;
        BannedMobData data = BannedMobData.get(s);
        data.addBanned(registryName);
        data.setDirty();
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        // if player is holding binoculars and attacked an entity that is glowing (locked), insta-kill
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity)) return;
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player)) return;
        net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player)event.getEntity();
        if (player.getMainHandItem().getItem() instanceof BinocularsItem || player.getOffhandItem().getItem() instanceof BinocularsItem) {
            LivingEntity le = (LivingEntity)target;
            if (le.hasEffect(net.minecraft.world.effect.MobEffects.GLOWING)) {
                // mark banned and remove
                markEntityTypeBanned(player.level, le.getType().getRegistryName().toString());
                le.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        Entity e = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel)) return;
        ServerLevel s = (ServerLevel)event.getLevel();
        BannedMobData data = BannedMobData.get(s);
        String reg = e.getType().getRegistryName().toString();
        if (data != null && data.isBanned(reg)) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
        }
    }
}