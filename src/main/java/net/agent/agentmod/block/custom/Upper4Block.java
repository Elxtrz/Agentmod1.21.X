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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class Upper4Block extends Block {
    private static final int MOB_SPAWN_INTERVAL = 20 * 3;     // 3 seconds
    private static final int PLAYER_SPAWN_INTERVAL = 20 * 5;  // 5 seconds
    private static final int EFFECT_INTERVAL = 20 * 15;       // 15 seconds

    private int mobSpawnTimer = 0;
    private int playerSpawnTimer = 0;
    private int effectTimer = 0;

    public Upper4Block(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;

        if (entity instanceof LivingEntity living && !(living instanceof SpiderEntity))
            world.scheduleBlockTick(pos, this, MOB_SPAWN_INTERVAL);


        super.onSteppedOn(world, pos, state, entity);
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
                p -> p.squaredDistanceTo(centerX, centerY, centerZ) <= 25.0);
        boolean playerNearby = !nearbyPlayers.isEmpty();

        mobSpawnTimer += MOB_SPAWN_INTERVAL;
        playerSpawnTimer += MOB_SPAWN_INTERVAL;
        effectTimer += MOB_SPAWN_INTERVAL;

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

        if (hasStandingEntity || playerNearby)
            world.scheduleBlockTick(pos, this, MOB_SPAWN_INTERVAL);
    }
}