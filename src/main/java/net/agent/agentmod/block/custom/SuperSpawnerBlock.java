package net.agent.agentmod.block.custom;

import net.agent.agentmod.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SuperSpawnerBlock extends Block {
    private static final int MAX_STORED = 50;
    private static final int GROW_INTERVAL = 20 * 3;
    private static final int PLAYER_SPAWN_INTERVAL = 20 * 2;
    private static final int SLOW_SPAWN_INTERVAL = 20 * 10;

    private final List<EntityType<?>> storedMobs = new ArrayList<>();
    private int growTimer = 0;
    private int spawnTimer = 0;

    private static final EntityType<?>[] VALID_MOBS = new EntityType<?>[]{
            EntityType.CAVE_SPIDER,
            EntityType.WITHER_SKELETON,
            EntityType.CREEPER,
            EntityType.PIGLIN_BRUTE,
            EntityType.SPIDER,
            EntityType.PILLAGER,
            EntityType.STRAY,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.SILVERFISH
    };

    public SuperSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient()) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1;
        double cz = pos.getZ() + 0.5;

        List<ServerPlayerEntity> nearbyPlayers = world.getPlayers(p ->
                p.squaredDistanceTo(cx, cy, cz) <= 25
        );
        boolean playerNearby = !nearbyPlayers.isEmpty();

        growTimer += 20;
        if (growTimer >= GROW_INTERVAL) {
            if (storedMobs.size() < MAX_STORED) {
                EntityType<?> chosen = VALID_MOBS[random.nextInt(VALID_MOBS.length)];
                storedMobs.add(chosen);
            }
            growTimer = 0;
        }

        spawnTimer += 20;
        int targetInterval = playerNearby ? PLAYER_SPAWN_INTERVAL : SLOW_SPAWN_INTERVAL;

        if (spawnTimer >= targetInterval && !storedMobs.isEmpty()) {
            int index = random.nextInt(storedMobs.size());
            EntityType<?> pick = storedMobs.remove(index);
            Entity mob = pick.create(world);
            if (mob != null) {
                mob.refreshPositionAndAngles(cx, cy, cz, 0f, 0f);
                world.spawnEntity(mob);
            }
            spawnTimer = 0;
        }

        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient()) {
            ServerWorld sw = (ServerWorld) world;
            double cx = pos.getX() + 0.5;
            double cy = pos.getY() + 1;
            double cz = pos.getZ() + 0.5;

            for (int i = 0; i < 10; i++) {
                Entity a = EntityType.SILVERFISH.create(sw);
                if (a != null) {
                    a.refreshPositionAndAngles(cx, cy, cz, 0f, 0f);
                    sw.spawnEntity(a);
                }
            }

            for (int i = 0; i < 10; i++) {
                Entity a = EntityType.ENDERMITE.create(sw);
                if (a != null) {
                    a.refreshPositionAndAngles(cx, cy, cz, 0f, 0f);
                    sw.spawnEntity(a);
                }
            }

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 3, 1));
            player.addStatusEffect(new StatusEffectInstance(ModEffects.BLEED, 20 * 5, 1));
        }

        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            ServerWorld sw = (ServerWorld) world;
            double cx = pos.getX() + 0.5;
            double cy = pos.getY() + 1;
            double cz = pos.getZ() + 0.5;

            ExperienceOrbEntity.spawn(sw, new Vec3d(cx, cy, cz), 30 * 7);

            for (EntityType<?> type : VALID_MOBS) {
                for (int i = 0; i < 3; i++) { // 3 of each type (nerf from 10 bec 10 too hard to kill)
                    Entity e = type.create(sw);
                    if (e != null) {
                        e.refreshPositionAndAngles(cx, cy, cz, 0f, 0f);
                        sw.spawnEntity(e);
                    }
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }
}