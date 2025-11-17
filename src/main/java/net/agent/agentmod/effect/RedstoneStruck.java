package net.agent.agentmod.effect;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;

public class RedstoneStruck extends StatusEffect {

    private static final int PARTICLE_PERIOD = 10;

    // Items that convert in the player's hand only
    private static final Map<Item, Item> ITEM_CONVERSIONS = Map.ofEntries(
            Map.entry(Items.TORCH, Items.REDSTONE_TORCH),
            Map.entry(Items.GLOWSTONE_DUST, Items.REDSTONE),
            Map.entry(Items.STICK, Items.REDSTONE_TORCH)
    );

    // Blocks that can receive redstone power
    private static final Set<Block> POWERABLE_BLOCKS = Set.of(
            // redstone components
            net.minecraft.block.Blocks.REDSTONE_WIRE,
            net.minecraft.block.Blocks.REPEATER,
            net.minecraft.block.Blocks.COMPARATOR,
            net.minecraft.block.Blocks.OBSERVER,
            net.minecraft.block.Blocks.DROPPER,
            net.minecraft.block.Blocks.DISPENSER,
            net.minecraft.block.Blocks.PISTON,
            net.minecraft.block.Blocks.STICKY_PISTON,
            net.minecraft.block.Blocks.REDSTONE_LAMP,
            net.minecraft.block.Blocks.NOTE_BLOCK,

            // doors & trapdoors
            net.minecraft.block.Blocks.OAK_DOOR,
            net.minecraft.block.Blocks.IRON_DOOR,
            net.minecraft.block.Blocks.OAK_TRAPDOOR,
            net.minecraft.block.Blocks.IRON_TRAPDOOR,

            // rails
            net.minecraft.block.Blocks.RAIL,
            net.minecraft.block.Blocks.POWERED_RAIL,
            net.minecraft.block.Blocks.DETECTOR_RAIL,

            // misc that respond to redstone
            net.minecraft.block.Blocks.TNT,
            net.minecraft.block.Blocks.BELL
    );

    public RedstoneStruck(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();
        if (!world.isClient()) {

            ServerWorld server = (ServerWorld) world;
            long ticks = server.getTime();

            // particles
            if (ticks % PARTICLE_PERIOD == 0L) {
                server.spawnParticles(
                        ModParticles.REDSTONE_STRUCK_PARTICLE,
                        entity.getX(),
                        entity.getY() + 1,
                        entity.getZ(),
                        6,
                        0.1, 0.1, 0.1,
                        0.8
                );
            }

            // hand item conversion
            if (entity instanceof PlayerEntity player) {
                ItemStack main = player.getMainHandStack();
                Item replacement = ITEM_CONVERSIONS.get(main.getItem());
                if (replacement != null) {
                    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(replacement, main.getCount()));
                }
            }

            for (BlockPos pos : BlockPos.iterate(
                    entity.getBlockPos().add(-1, 0, -1),
                    entity.getBlockPos().add(1, 0, 1))) {
                if (!pos.equals(entity.getBlockPos())) {
                    BlockState adjacentState = world.getBlockState(pos);
                    if (POWERABLE_BLOCKS.contains(adjacentState.getBlock())) {
                        world.updateNeighborsAlways(pos, adjacentState.getBlock());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}