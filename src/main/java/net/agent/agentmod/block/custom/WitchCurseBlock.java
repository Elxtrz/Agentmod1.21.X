package net.agent.agentmod.block.custom;

import net.agent.agentmod.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class WitchCurseBlock extends Block {
    private static final int MOB_SPAWN_INTERVAL = 20 * 3;     // 3 seconds
    private static final int PLAYER_SPAWN_INTERVAL = 20 * 5;  // 5 seconds
    private static final int EFFECT_INTERVAL = 20 * 15;       // 15 seconds
    private static final int VORTEX_RADIUS = 5;               // 5 blocks
    private static final double VORTEX_PULL_STRENGTH = 0.8;  // horizontal pull per tick

    private int mobSpawnTimer = 0;
    private int playerSpawnTimer = 0;
    private int effectTimer = 0;

    public WitchCurseBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) {
            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        if (entity instanceof ServerPlayerEntity serverPlayer)
            serverPlayer.addStatusEffect(new StatusEffectInstance(ModEffects.CURSE_OF_THE_WITCH, 20 * 10, 0)); // 10s

        world.breakBlock(pos, false);
        world.scheduleBlockTick(pos, this, 1);

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient()) {
            super.onBlockBreakStart(state, world, pos, player);
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(ModEffects.CURSE_OF_THE_WITCH, 20 * 10, 0)); // 10s

        world.breakBlock(pos, false);

        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) return;

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        Box box = new Box(pos).expand(0, 1, 0);
        List<LivingEntity> standing = world.getEntitiesByClass(LivingEntity.class, box,
                e -> !(e instanceof SpiderEntity));
        boolean hasStandingEntity = !standing.isEmpty();

        List<ServerPlayerEntity> nearbyPlayers = world.getPlayers(
                p -> p.squaredDistanceTo(centerX, centerY, centerZ) <= (double) VORTEX_RADIUS * VORTEX_RADIUS);
        boolean playerNearby = !nearbyPlayers.isEmpty();

        // increment timers per tick
        mobSpawnTimer++;
        playerSpawnTimer++;
        effectTimer++;

        if (hasStandingEntity && mobSpawnTimer >= MOB_SPAWN_INTERVAL) {
            CaveSpiderEntity caveSpider = EntityType.CAVE_SPIDER.create(world);
            if (caveSpider != null) {
                caveSpider.refreshPositionAndAngles(centerX, pos.getY() + 1, centerZ, 0f, 0f);
                world.spawnEntity(caveSpider);
            }
            mobSpawnTimer = 0;
        }

        if (playerNearby) {
            if (playerSpawnTimer >= PLAYER_SPAWN_INTERVAL) {
                SpiderEntity spider = EntityType.SPIDER.create(world);
                if (spider != null) {
                    spider.refreshPositionAndAngles(centerX, pos.getY() + 1, centerZ, 0f, 0f);
                    world.spawnEntity(spider);
                }
                playerSpawnTimer = 0;
            }

            if (effectTimer >= EFFECT_INTERVAL) {
                for (ServerPlayerEntity player : nearbyPlayers) {
                    player.addStatusEffect(new StatusEffectInstance(ModEffects.BLEED, 20 * 5, 0));     // 5s
                    player.addStatusEffect(new StatusEffectInstance(ModEffects.GROUNDED, 20 * 10, 0)); // 10s
                }
                effectTimer = 0;
            }
        }

        // Vortex: pull only player entities horizontally toward block center
        if (playerNearby) {
            Vec3d center = new Vec3d(centerX, centerY, centerZ);
            for (ServerPlayerEntity player : nearbyPlayers) {
                Vec3d playerPos = player.getPos();
                Vec3d toCenter = center.subtract(playerPos);
                // keep only horizontal pull
                toCenter = new Vec3d(toCenter.x, 0, toCenter.z);
                double dist = Math.max(toCenter.length(), 0.0001);
                Vec3d pull = toCenter.normalize().multiply(VORTEX_PULL_STRENGTH);
                // apply pull
                player.addVelocity(pull.x, 0, pull.z);
                // ensure server informs client of velocity change on next tick
                player.velocityModified = true;
            }
        }

        // Reschedule tick if something to monitor (use 1 tick for smooth vortex)
        if (hasStandingEntity || playerNearby) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }
}