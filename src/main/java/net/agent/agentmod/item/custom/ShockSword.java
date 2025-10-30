package net.agent.agentmod.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShockSword extends SwordItem {

    public ShockSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (attacker.getRandom().nextDouble() < 0.33)
                target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.agent.agentmod.effect.ModEffects.SHOCKED,
                        400, 0));

        }

        return super.postHit(stack, target, attacker);
    }

}
