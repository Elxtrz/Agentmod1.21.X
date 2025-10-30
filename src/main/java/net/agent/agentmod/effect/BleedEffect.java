package net.agent.agentmod.effect;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BleedEffect extends StatusEffect {
    public BleedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), 2.5F);

        World world = entity.getWorld();
        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            long ticks = serverWorld.getTime();
            final int period = 30;
            if (ticks % period == 0) {
                serverWorld.spawnParticles(ModParticles.BLEED_PARTICLE,
                        entity.getX() + 0.5D, entity.getY() + 1.0D, entity.getZ() + 0.5D,
                        4,
                        0.0, 0.0, 0.0,
                        0.8);
            }
        }

        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}