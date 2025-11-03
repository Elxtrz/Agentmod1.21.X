package net.agent.agentmod.block.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LitBlackHoleBlock extends Block {
    private static final int LIFETIME_TICKS = 100; // 5 seconds at 20 TPS
    private static final int TICK_INTERVAL = 5;    // spawn particles every 5 ticks (0.25s)

    public LitBlackHoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            // Store creation time in block entity data or in memory if you have a block entity.
            world.scheduleBlockTick(pos, this, TICK_INTERVAL);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Save the creation tick in block state tags or temp memory; here we’ll fake it using age map.
        // Simplify: attach "age" to world property map per pos.
        long currentTime = world.getTime();
        long startTime = state.contains(net.minecraft.state.property.Properties.AGE_15)
                ? state.get(net.minecraft.state.property.Properties.AGE_15)
                : currentTime;

        int ticksExisted = (int) (currentTime - startTime);
        double progress = Math.min(1.0, ticksExisted / (double) LIFETIME_TICKS);

        int particleCount = 2 + (int) (progress * 10); // fewer particles
        double baseSpeed = 0.02 + progress * 0.3;      // smoother approach speed

        for (int i = 0; i < particleCount; i++) {
            double spawnX = pos.getX() + world.random.nextDouble();
            double spawnY = pos.getY() + world.random.nextDouble();
            double spawnZ = pos.getZ() + world.random.nextDouble();

            double centerX = pos.getX() + 0.5D;
            double centerY = pos.getY() + 0.5D;
            double centerZ = pos.getZ() + 0.5D;

            double dirX = centerX - spawnX;
            double dirY = centerY - spawnY;
            double dirZ = centerZ - spawnZ;
            double dist = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
            if (dist == 0) dist = 0.001;

            double motionX = dirX / dist * baseSpeed;
            double motionY = dirY / dist * baseSpeed;
            double motionZ = dirZ / dist * baseSpeed;

            world.spawnParticles(
                    ModParticles.BLACKHOLE_PARTICLE,
                    spawnX, spawnY, spawnZ,
                    1,
                    motionX, motionY, motionZ,
                    0.0
            );
        }

        // Explosion after 5 seconds
        if (ticksExisted >= LIFETIME_TICKS) {
            world.createExplosion(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    30.0f,
                    World.ExplosionSourceType.TNT
            );
            world.removeBlock(pos, false);
            return;
        }

        // Schedule next tick slower (every 5 ticks)
        world.scheduleBlockTick(pos, this, TICK_INTERVAL);
    }
}
