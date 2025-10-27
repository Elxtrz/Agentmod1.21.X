package net.agent.agentmod.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent SUPER_CAULIFLOWER = new FoodComponent.Builder()
            .nutrition(3)
            .saturationModifier(0.25f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 200, 5), 0.05f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1), 0.70f)
            .alwaysEdible()
            .build();
}