package net.agent.agentmod.world.gen;

import net.agent.agentmod.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class ModOreGeneration {
    public static void generateOres() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.PINK_GARNET_ORE_PLACED_KEY);

        // Example for individual Biomes
        // BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.FOREST, BiomeKeys.PLAINS),
        // GenerationStep.Feature.UNDERGROUND_ORES,
        //         ModPlacedFeatures.PINK_GARNET_ORE_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.NETHER_PINK_GARNET_ORE_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.END_PINK_GARNET_ORE_PLACED_KEY);
    }
}