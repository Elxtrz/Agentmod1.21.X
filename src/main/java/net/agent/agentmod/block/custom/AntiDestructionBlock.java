package net.agent.agentmod.block.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AntiDestructionBlock extends Block {

    private static final int RANGE = 50;
    private static final int SCAN_INTERVAL = 10; // 2x per second
    private static int tickCounter = 0;

    private static final Set<BlockPos> ACTIVE_BLOCKS = new HashSet<>();
    private static final Set<PlayerEntity> TRACKED_PLAYERS = new HashSet<>();

    public AntiDestructionBlock(Settings settings) {
        super(settings);
        registerWorldTick();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            ACTIVE_BLOCKS.add(pos.toImmutable());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (!world.isClient && state.getBlock() != newState.getBlock()) {
            ACTIVE_BLOCKS.remove(pos);
        }
    }

    private static void registerWorldTick() {
        ServerTickEvents.END_WORLD_TICK.register(AntiDestructionBlock::scanWorld);
    }

    private static void scanWorld(ServerWorld world) {
        tickCounter++;
        if (tickCounter % SCAN_INTERVAL != 0) return;

        for (BlockPos center : ACTIVE_BLOCKS) {

            // Skip unloaded regions
            if (!world.isChunkLoaded(center)) continue;

            // --- Entity check (cheap) ---
            Box area = new Box(
                    center.getX() - RANGE, center.getY() - RANGE, center.getZ() - RANGE,
                    center.getX() + RANGE, center.getY() + RANGE, center.getZ() + RANGE
            );

            for (Entity e : world.getOtherEntities(null, area)) {
                if (e instanceof TntEntity ||
                        e instanceof EndCrystalEntity ||
                        (e instanceof CreeperEntity c && c.isIgnited()) ||
                        e instanceof TntMinecartEntity) {
                    e.discard();
                }
            }

            int minX = center.getX() - RANGE;
            int minY = Math.max(world.getBottomY(), center.getY() - RANGE);
            int minZ = center.getZ() - RANGE;

            int maxX = center.getX() + RANGE;
            int maxY = Math.min(world.getTopY(), center.getY() + RANGE);
            int maxZ = center.getZ() + RANGE;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    // Skip unloaded chunks
                    if (!world.isChunkLoaded(new BlockPos(x, center.getY(), z))) continue;

                    for (int y = minY; y <= maxY; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        BlockState bs = world.getBlockState(p);

                        if (bs.isOf(Blocks.TNT) || bs.isOf(Blocks.RESPAWN_ANCHOR))
                            world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                    }
                }
            }

            for (Iterator<PlayerEntity> it = TRACKED_PLAYERS.iterator(); it.hasNext();) {
                PlayerEntity p = it.next();
                if (p.isRemoved()) it.remove();
            }

            for (ServerPlayerEntity p : world.getPlayers()) {
                boolean insideAnyZone = false;
                for (BlockPos center1 : ACTIVE_BLOCKS) {
                    if (p.getBlockPos().isWithinDistance(center1, RANGE)) {
                        insideAnyZone = true;
                        break;
                    }
                }

                ServerPlayerEntity sp = p;

                if (insideAnyZone && TRACKED_PLAYERS.add(p))
                    sendOverlay(sp, "§a§lAnti-Destruction Zone§r");

                if (!insideAnyZone && TRACKED_PLAYERS.remove(p))
                    sendOverlay(sp, "§4§lLeft Protected Zone§r");

            }

        }
    }

    private static void sendOverlay(ServerPlayerEntity player, String msg) {
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.of(msg)));
    }
}