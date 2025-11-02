package net.agent.agentmod.item.custom;

import net.agent.agentmod.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ChainsawSword extends SwordItem {

    public ChainsawSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();

        if (!world.isClient) {
            if (attacker.getRandom().nextDouble() < 0.07)
                target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(ModEffects.BLEED,
                        120, 0));

        }

        return super.postHit(stack, target, attacker);
    }

}
