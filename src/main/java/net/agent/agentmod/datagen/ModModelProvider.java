package net.agent.agentmod.datagen;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PINK_GARNET_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_PINK_GARNET_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PINK_GARNET_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PINK_GARNET_DEEPSLATE_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PINK_GARNET_END_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PINK_GARNET_NETHER_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLACK_HOLE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIT_BLACK_HOLE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.X_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.UPPER_4_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BRIT_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOB_JAIL_BLOCK_LEVEL_1);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOB_JAIL_BLOCK_LEVEL_2);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOB_JAIL_BLOCK_LEVEL_3);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BULLS_EYE_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGIC_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.PINK_GARNET, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_PINK_GARNET, Models.GENERATED);

        itemModelGenerator.register(ModItems.TNT_ARROW, Models.GENERATED);

        itemModelGenerator.register(ModItems.SUPER_CAULIFLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACK_WAND, Models.GENERATED);
        itemModelGenerator.register(ModItems.STARLIGHT_ASHES, Models.GENERATED);
        itemModelGenerator.register(ModItems.TEA, Models.GENERATED);

        itemModelGenerator.register(ModItems.PINK_GARNET_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PINK_GARNET_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PINK_GARNET_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PINK_GARNET_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PINK_GARNET_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.PINK_GARNET_HAMMER, Models.HANDHELD);

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.PINK_GARNET_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.PINK_GARNET_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.PINK_GARNET_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.PINK_GARNET_BOOTS));

        itemModelGenerator.register(ModItems.FLATTEN_WAND, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SPHERE_WAND, Models.HANDHELD);
        itemModelGenerator.register(ModItems.VOID_WAND, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRAMID_WAND, Models.HANDHELD);
        itemModelGenerator.register(ModItems.XRAY_WAND, Models.HANDHELD);
        itemModelGenerator.register(ModItems.ENTITY_WAND, Models.HANDHELD);
    }
}