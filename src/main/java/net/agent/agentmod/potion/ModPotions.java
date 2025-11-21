package net.agent.agentmod.potion;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModPotions {
    public static final RegistryEntry<Potion> SLIMEY_POTION = registerPotion("slimey_potion",
            new Potion(new StatusEffectInstance(ModEffects.SLIMEY, 1200, 0)));

    public static final RegistryEntry<Potion> BLEED_POTION = registerPotion("bleed_potion",
            new Potion(new StatusEffectInstance(ModEffects.BLEED, 20*15, 0)));

    public static final RegistryEntry<Potion> REDSTONE_STRUCK = registerPotion("redstone_struck",
            new Potion(new StatusEffectInstance(ModEffects.REDSTONE_STRUCK, 20*120, 0)));

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(Agentmod.MOD_ID, name), potion);
    }

    public static void registerPotions() {
        Agentmod.LOGGER.info("Registering Mod Potions for " + Agentmod.MOD_ID);
    }
}
