package net.agent.agentmod.block.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LitBlackHoleBlock extends Block {
    private static final int LIFETIME_TICKS = 100; // 5 seconds (20 ticks/sec)
    private static final int TICK_INTERVAL = 5;    // spawn particles every 5 ticks
    private static final int PARTICLES_PER_TICK = 5;

    private int age = 0;

    public LitBlackHoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, TICK_INTERVAL);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) return;

        // spawn 5 particles per tick
        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            double spawnX = pos.getX() + random.nextDouble();
            double spawnY = pos.getY() + random.nextDouble();
            double spawnZ = pos.getZ() + random.nextDouble();

            // velocity doesn’t matter much — handled by the particle class
            world.spawnParticles(
                    ModParticles.BLACKHOLE_PARTICLE,
                    spawnX, spawnY, spawnZ,
                    1,
                    0, 0, 0,
                    0.0
            );
        }

        age += TICK_INTERVAL;

        if (age >= LIFETIME_TICKS) {
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

        // reschedule next tick
        world.scheduleBlockTick(pos, this, TICK_INTERVAL);
    }
}
