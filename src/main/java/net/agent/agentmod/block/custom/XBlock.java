package net.agent.agentmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class XBlock extends Block {
    public static final IntProperty TNT_COUNT = IntProperty.of("tnt_count", 0, 128);

    public XBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TNT_COUNT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TNT_COUNT);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        ItemStack stack = itemEntity.getStack();

        // Pickup TNT items into the block (like a simple hopper)
        if (stack.getItem() == Items.TNT) {
            int current = state.get(TNT_COUNT);
            int space = 128 - current;
            int toAdd = Math.min(stack.getCount(), space);
            if (toAdd > 0) {
                int newCount = current + toAdd;
                if (!world.isClient())
                    world.setBlockState(pos, state.with(TNT_COUNT, newCount), 3);
                stack.decrement(toAdd);
                if (stack.isEmpty()) {
                    itemEntity.setStack(ItemStack.EMPTY);
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);
                } else
                    itemEntity.setStack(stack);

            }
            // if toAdd == 0, storage full -> do nothing
            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        // Drop stored TNT as *lit* TNT entities in a circular pattern when gunpowder is dropped
        if (stack.getItem() == Items.GUNPOWDER) {
            int stored = state.get(TNT_COUNT);
            if (stored > 0) {
                if (!world.isClient() && world instanceof ServerWorld) {
                    // consume the gunpowder item
                    itemEntity.setStack(ItemStack.EMPTY);
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);

                    // reset stored TNT
                    world.setBlockState(pos, state.with(TNT_COUNT, 0), 3);

                    double centerX = pos.getX() + 0.5;
                    double centerY = pos.getY() + 1.0;
                    double centerZ = pos.getZ() + 0.5;
                    double radius = 1.5;
                    double velocityScale = 0.25;
                    for (int i = 0; i < stored; i++) {
                        double angle = 2.0 * Math.PI * i / stored;
                        double dx = Math.cos(angle) * radius;
                        double dz = Math.sin(angle) * radius;
                        double spawnX = centerX + dx;
                        double spawnZ = centerZ + dz;

                        TntEntity tnt = new TntEntity(world, spawnX, centerY, spawnZ, null);
                        tnt.setVelocity(dx * velocityScale, 0.5, dz * velocityScale);
                        int fuse = ThreadLocalRandom.current().nextInt(20, 81);
                        tnt.setFuse(fuse);
                        world.spawnEntity(tnt);
                    }
                }
            } // else stored == 0 -> do not pick up gunpowder (leave it)
            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        super.onSteppedOn(world, pos, state, entity);
    }
}