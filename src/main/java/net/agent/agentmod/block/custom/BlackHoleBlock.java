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
