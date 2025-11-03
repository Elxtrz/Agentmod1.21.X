package net.agent.agentmod.block.custom;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.item.ModItems;
import net.agent.agentmod.particle.ModParticles;
import net.agent.agentmod.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlackHoleBlock extends Block {
    public BlackHoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        float f = 0.7f + (float) Math.random();
        world.playSound(player, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, f);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {

        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getStack().getItem() == ModItems.STARLIGHT_ASHES) {
                itemEntity.setStack(ItemStack.EMPTY);
                if (!world.isClient()) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.setBlockState(pos, ModBlocks.LIT_BLACK_HOLE_BLOCK.getDefaultState());
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
