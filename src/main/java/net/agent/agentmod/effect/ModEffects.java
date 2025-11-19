package net.agent.agentmod.effect;

import net.agent.agentmod.Agentmod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> SLIMEY = registerStatusEffect("slimey",
            new SlimeyEffect(StatusEffectCategory.NEUTRAL, 0x36ebab)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(Agentmod.MOD_ID, "slimey"), -0.25f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> BLEED = registerStatusEffect("bleed",
            new BleedEffect(StatusEffectCategory.NEUTRAL, 0x8B0000)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(Agentmod.MOD_ID, "bleed"), -0.1f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> SHOCKED = registerStatusEffect("shocked",
            new ShockEffect(StatusEffectCategory.NEUTRAL, 0x808080)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(Agentmod.MOD_ID, "shocked"), +0.1f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> GROUNDED = registerStatusEffect("grounded",
            new GroundedEffect(StatusEffectCategory.NEUTRAL, 0x8B0000)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(Agentmod.MOD_ID, "grounded"), -0.15f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> REDSTONE_STRUCK = registerStatusEffect("redstone_struck",
            new RedstoneStruck(StatusEffectCategory.NEUTRAL, 0x8A0303));

    public static final RegistryEntry<StatusEffect> CURSE_OF_THE_WITCH = registerStatusEffect("curse_of_the_witch",
            new CurseOfTheWitchEffect(StatusEffectCategory.NEUTRAL, colorToHex(new Color(90, 1, 43))));


    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Agentmod.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        Agentmod.LOGGER.info("Registering Mod Effects for " + Agentmod.MOD_ID);
    }

    private static int colorToHex(Color color) {
        return (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }
}