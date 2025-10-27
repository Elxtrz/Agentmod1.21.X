package net.agent.agentmod.block.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends MiningToolItem {
    public HammerItem(ToolMaterial material, Settings settings) {
        super(material, BlockTags.PICKAXE_MINEABLE, settings);
    }

    //    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, ServerPlayerEntity player) {
//        List<BlockPos> positions = new ArrayList<>();
//        HitResult hit = player.raycast(20, 0, false);
//        if (hit.getType() == HitResult.Type.BLOCK) {
//            BlockHitResult blockHit = (BlockHitResult) hit;
//
//            if(blockHit.getSide() == Direction.DOWN || blockHit.getSide() == Direction.UP) {
//                for(int x = -range; x <= range; x++) {
//                    for(int y = -range; y <= range; y++) {
//                        positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
//                    }
//                }
//            }
//
//            if(blockHit.getSide() == Direction.NORTH || blockHit.getSide() == Direction.SOUTH) {
//                for(int x = -range; x <= range; x++) {
//                    for(int y = -range; y <= range; y++) {
//                        positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
//                    }
//                }
//            }
//
//            if(blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST) {
//                for(int x = -range; x <= range; x++) {
//                    for(int y = -range; y <= range; y++) {
//                        positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
//                    }
//                }
//            }
//        }
//
//        return positions;
//    }
    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, ServerPlayerEntity player) {
        List<BlockPos> positions = new ArrayList<>();
        // Creates a cubic area centered on the initial block.
        // For a 5x5x5 cube, call this with range = 2.
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ() + z));
                }
            }
        }
        return positions;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient && selected && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            living.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.HASTE, 10, 0, true, false, true));
        }
    }

    // fix this it loki dont work i need lnt damage
//    @Override
//    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//
//    }
}