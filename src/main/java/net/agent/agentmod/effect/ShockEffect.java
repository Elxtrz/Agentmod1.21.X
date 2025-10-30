package net.agent.agentmod.effect;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ShockEffect extends StatusEffect {
    public ShockEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            long ticks = serverWorld.getTime();

            if (ticks % 30 == 0)
                serverWorld.spawnParticles(ModParticles.LIGHTNING_PARTICLE,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    3, 0.2, 0.2, 0.2, 0.01);

            if (ticks % 20 == 0)
                if (entity.getRandom().nextFloat() < 0.05F)
                    summonLightning(serverWorld, entity);


        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    private void summonLightning(ServerWorld world, LivingEntity target) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
        world.spawnEntity(lightning);

        world.getServer().execute(() -> {
            if (target.isAlive())
                target.damage(world.getDamageSources().lightningBolt(), 6.0F);

        });
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}