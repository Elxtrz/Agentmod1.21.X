package net.agent.agentmod.effect;

import net.agent.agentmod.particle.ModParticles;
import net.agent.agentmod.potion.ModPotions;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.block.Blocks;

import java.util.UUID;
import java.util.WeakHashMap;

public class CurseOfTheWitchEffect extends StatusEffect {

    private static final WeakHashMap<UUID, Double> initialYMap = new WeakHashMap<>();
    private static final WeakHashMap<UUID, Long> startTimeMap = new WeakHashMap<>();
    private static final WeakHashMap<UUID, Boolean> hasSpawnedMap = new WeakHashMap<>();

    public CurseOfTheWitchEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {

        if (!(entity instanceof PlayerEntity player)) return false;
        World world = player.getWorld();
        UUID uuid = player.getUuid();

        // init maps
        initialYMap.putIfAbsent(uuid, player.getY());
        startTimeMap.putIfAbsent(uuid, world.getTime());
        hasSpawnedMap.putIfAbsent(uuid, false);

        double initialY = initialYMap.get(uuid);
        long startTime = startTimeMap.get(uuid);
        long t = world.getTime();

        double targetY = initialY + 10;
        double minY = targetY - 2;
        double maxY = targetY + 2;

        // 0–3 seconds: rise to target height
        if (t - startTime < 60) {
            if (player.getY() < targetY) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LEVITATION, 3, 0, true, false, false
                ));
            }
        }

        // At EXACT 3 seconds: spawn mobs + fire ONCE
        if ((t - startTime) >= 60 && !hasSpawnedMap.get(uuid)) {
            spawnEntitiesAndSetFire(player, world);
            hasSpawnedMap.put(uuid, true);
        }

        // 3–10 seconds: hover band
        if ((t - startTime) >= 60 && (t - startTime) < 200) {

            if (player.getY() < minY) {
                // nudge upward
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LEVITATION, 1, 0, true, false, false
                ));
            } else if (player.getY() > maxY) {
                // nudge downward
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOW_FALLING, 2, 0, true, false, false
                ));
            } else {
                // inside hover band: stop vertical drift
                player.setVelocity(player.getVelocity().multiply(1, 0, 1));
            }
        }

        // End after 10 seconds
        if ((t - startTime) >= 200) {
            cleanup(player);
        }

        if(!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            long ticks = serverWorld.getTime();
            final int period = 30;
            if (ticks % period == 0) {
                serverWorld.spawnParticles(ModParticles.WITCH_PARTICLE,
                        entity.getX() + 0.5D, entity.getY() + 1.0D, entity.getZ() + 0.5D,
                        4,
                        0.0, 0.0, 0.0,
                        0.8);
            }
        }
        return true;
    }

    private void spawnEntitiesAndSetFire(PlayerEntity player, World world) {
        if (world.isClient()) return;
        ServerWorld sw = (ServerWorld) world;

        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5;
            double radius = 5.0;

            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY();

            // Skeleton
            SkeletonEntity sk = new SkeletonEntity(EntityType.SKELETON, sw);
            sk.updatePosition(x, y, z);

            ItemStack bow = new ItemStack(Items.BOW);

            RegistryEntry<Enchantment> flame =
                    world.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(Enchantments.FLAME)
                            .orElseThrow();

            bow.addEnchantment(flame, 1);


            sk.equipStack(EquipmentSlot.MAINHAND, bow);

            ItemStack arrow = PotionContentsComponent.createStack(
                    Items.TIPPED_ARROW,
                    ModPotions.BLEED_POTION
            );
            sk.equipStack(EquipmentSlot.OFFHAND, arrow);

            sw.spawnEntity(sk);

            // Witch
            WitchEntity witch = new WitchEntity(EntityType.WITCH, sw);
            witch.updatePosition(x, y, z);
            sw.spawnEntity(witch);
        }

        // Fire ring
        BlockPos base = player.getBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = base.add(dx, 0, dz);
                if (sw.getBlockState(pos).isAir()) {
                    sw.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    private void cleanup(PlayerEntity player) {
        UUID uuid = player.getUuid();
        initialYMap.remove(uuid);
        startTimeMap.remove(uuid);
        hasSpawnedMap.remove(uuid);

        player.setNoGravity(false);
        player.setVelocity(0, 0, 0);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
