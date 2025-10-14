package com.example.binoculars;

import com.example.binoculars.network.PacketLockTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Predicate;
import java.util.List;

public class BinocularsItem extends Item {
    public BinocularsItem(Properties props) {
        super(props);
    }

    // Use: start using on right click (like spyglass)
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    // Called each tick while using (right hold). We'll perform a raytrace and if an entity is targeted, send lock packet.
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity living, int count) {
        if (!(living instanceof Player)) return;
        Player player = (Player) living;
        Level world = player.level;
        if (world.isClientSide) {
            // client-side raytrace (use player's look vector)
            Vec3 start = player.getEyePosition(1.0f);
            Vec3 look = player.getViewVector(1.0f);
            double range = 280.0;
            Vec3 end = start.add(look.scale(range));
            AABB bb = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);
            EntityHitResult entityHit = net.minecraft.world.phys.BlockHitResult.createMiss(start, null, end) == null ? null : null;
            // We will iterate and find the first living entity in the line of sight
            List<Entity> list = world.getEntities(player, bb, e -> e instanceof LivingEntity && e != player && e.isAlive());
            Entity closest = null;
            double best = range*range;
            for (Entity e : list) {
                Vec3 pos = e.position();
                double dist = pos.distanceToSqr(player.position());
                if (dist < best) {
                    // simple check: is within a small angle of view
                    Vec3 dirTo = pos.subtract(start).normalize();
                    double dot = dirTo.dot(look);
                    if (dot > 0.99) { // pretty close to crosshair
                        best = dist;
                        closest = e;
                    }
                }
            }
            if (closest != null && closest instanceof LivingEntity) {
                // send lock packet to server to mark glowing and banning on kill
                BinocularsMod.CHANNEL.sendToServer(new PacketLockTarget(closest.getUUID()));
            }
        }
    }
}