package net.agent.agentmod.effect;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class GroundedEffect extends StatusEffect {
    private static final int PARTICLE_PERIOD = 10;
    private static final double JUMP_DAMPEN = 0.85D;

    private static final Map<LivingEntity, Boolean> REENTRANCY_GUARD = new WeakHashMap<>();

    public GroundedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();
        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            long ticks = serverWorld.getTime();

            if (ticks % PARTICLE_PERIOD == 0L) {
                serverWorld.spawnParticles(
                        ModParticles.GRAVITY_PARTICLE,
                        entity.getX() + 0.5D, entity.getY() + 1.0D, entity.getZ() + 0.5D,
                        8,
                        0.0D, 0.0D, 0.0D,
                        0.8D
                );
            }

            Vec3d vel = entity.getVelocity();
            if (vel.y > 0.0D) {
                entity.setVelocity(vel.x, vel.y * JUMP_DAMPEN, vel.z);
            }
        }

        return true;
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        if (amount <= 0.0F) return;

        boolean isFall;
        try {
            isFall = source.isOf(DamageTypes.FALL);
        } catch (Throwable t) {
            isFall = "fall".equals(source.getName());
        }

        if (!isFall) return;

        if (REENTRANCY_GUARD.containsKey(entity))
            return;

        try {
            REENTRANCY_GUARD.put(entity, Boolean.TRUE);
            entity.damage(source, amount);
        } finally {
            REENTRANCY_GUARD.remove(entity);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}