package net.agent.agentmod.block.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class LitBlackHoleBlock extends Block {
    private static final int LIFETIME_TICKS = 100; // 5 seconds @ 20 TPS
    private static final int TICK_INTERVAL = 5;
    private static final int PARTICLES_PER_TICK = 5;

    // Simple runtime storage for tick tracking
    private static final Map<BlockPos, Integer> AGE_MAP = new HashMap<>();

    public LitBlackHoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            AGE_MAP.put(pos.toImmutable(), 0);
            world.scheduleBlockTick(pos, this, TICK_INTERVAL);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        AGE_MAP.remove(pos.toImmutable());
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int age = AGE_MAP.getOrDefault(pos.toImmutable(), 0) + TICK_INTERVAL;
        AGE_MAP.put(pos.toImmutable(), age);

        // Spawn particles moving toward center
        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            double radius = 1.8;
            double angle = 2 * Math.PI * random.nextDouble();
            double height = random.nextDouble();

            double spawnX = pos.getX() + 0.5 + radius * Math.cos(angle);
            double spawnY = pos.getY() + height;
            double spawnZ = pos.getZ() + 0.5 + radius * Math.sin(angle);

            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double dirX = centerX - spawnX;
            double dirY = centerY - spawnY;
            double dirZ = centerZ - spawnZ;
            double dist = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
            if (dist == 0) dist = 0.001;

            double speed = 0.15;
            double velX = dirX / dist * speed;
            double velY = dirY / dist * speed;
            double velZ = dirZ / dist * speed;

            world.spawnParticles(
                    ModParticles.BLACKHOLE_PARTICLE,
                    spawnX, spawnY, spawnZ,
                    1,
                    pos.getX() + 0.5,  // pass actual center X
                    pos.getY() + 0.5,  // pass actual center Y
                    pos.getZ() + 0.5,  // pass actual center Z
                    0.0
            );

        }

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
            AGE_MAP.remove(pos.toImmutable());
        } else {
            world.scheduleBlockTick(pos, this, TICK_INTERVAL);
        }
    }
}