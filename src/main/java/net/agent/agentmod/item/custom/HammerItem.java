package net.agent.agentmod.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends MiningToolItem {
    public HammerItem(ToolMaterial material, Settings settings) {
        super(material, BlockTags.PICKAXE_MINEABLE, settings);
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

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient && attacker instanceof ServerPlayerEntity player) {
            double chance = Math.random();
            if (chance < 0.85) { // 85% chance to summon lightning and give regeneration
                summonLightning((ServerWorld) world, target);
                giveRegen(player);
            }
        }

        return super.postHit(stack, target, attacker);
    }

    private void summonLightning(ServerWorld world, LivingEntity target) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
        world.spawnEntity(lightning);

        world.getServer().execute(() -> {
            if (target.isAlive())
                target.damage(world.getDamageSources().lightningBolt(), 6.0F);

        });
    }

    private void giveRegen(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2, true, true)); // 5s Regen III
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initialBlockPos, ServerPlayerEntity player) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    positions.add(initialBlockPos.add(x, y, z));
                }
            }
        }
        return positions;
    }
}