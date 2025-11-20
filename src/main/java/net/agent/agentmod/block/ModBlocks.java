package net.agent.agentmod.block;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.block.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.List;

public class ModBlocks {

    public static final Block MAGIC_BLOCK = registerBlock("magic_block",
            new MagicBlock(AbstractBlock.Settings.create().strength(5f).requiresTool().sounds(BlockSoundGroup.GLASS)));

    public static final Block PINK_GARNET_BLOCK = registerBlock("pink_garnet_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block RAW_PINK_GARNET_BLOCK = registerBlock("raw_pink_garnet_block",
            new Block(AbstractBlock.Settings.create().strength(3f)
                    .requiresTool()));

    public static final Block PINK_GARNET_ORE = registerBlock("pink_garnet_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().strength(3f).requiresTool()));

    public static final Block PINK_GARNET_DEEPSLATE_ORE = registerBlock("pink_garnet_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 6),
                    AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block PINK_GARNET_END_ORE = registerBlock("pink_garnet_end_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(4, 8),
                    AbstractBlock.Settings.create().strength(7f).requiresTool()));

    public static final Block PINK_GARNET_NETHER_ORE = registerBlock("pink_garnet_nether_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(1, 5),
                    AbstractBlock.Settings.create().strength(3f).requiresTool()));

    public static final Block BLACK_HOLE_BLOCK = registerBlock("black_hole_block",
            new BlackHoleBlock(AbstractBlock.Settings.create().strength(5f).requiresTool().sounds(BlockSoundGroup.GLASS)));

    public static final Block LIT_BLACK_HOLE_BLOCK = registerBlock("lit_black_hole_block",
            new LitBlackHoleBlock(AbstractBlock.Settings.create().strength(20f).requiresTool().sounds(BlockSoundGroup.GLASS)));

    public static final Block X_BLOCK = registerBlock("x_block",
            new XBlock(AbstractBlock.Settings.create().strength(5f).requiresTool().sounds(BlockSoundGroup.GLASS)) {
                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("block.agentmod.x_block.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }
            });

    public static final Block UPPER_4_BLOCK = registerBlock("upper_4_block",
            new Upper4Block(AbstractBlock.Settings.create().strength(20f).requiresTool().sounds(BlockSoundGroup.GLASS)));


    public static final Block BRIT_BLOCK = registerBlock("brit_block",
            new BritBlock(AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.VAULT)) {
                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("block.agentmod.brit_block.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }
            });

    public static final Block MOB_JAIL_BLOCK_LEVEL_1 = registerBlock("mob_jail_block_level_1",
            new MobJailBlockLevel1(AbstractBlock.Settings.create().strength(20f).requiresTool()));

    public static final Block MOB_JAIL_BLOCK_LEVEL_2 = registerBlock("mob_jail_block_level_2",
            new MobJailBlockLevel2(AbstractBlock.Settings.create().strength(20f).requiresTool()));

    public static final Block MOB_JAIL_BLOCK_LEVEL_3 = registerBlock("mob_jail_block_level_3",
            new MobJailBlockLevel3(AbstractBlock.Settings.create().strength(20f).requiresTool()));

    public static final Block BULLS_EYE_BLOCK = registerBlock("bulls_eye_block",
            new BullsEyeBlock(AbstractBlock.Settings.create().strength(4f).requiresTool()));

    public static final Block SUPER_SPAWNER_BLOCK = registerBlock("super_spawner_block",
            new SuperSpawnerBlock(AbstractBlock.Settings.create().strength(8f).requiresTool()));

    public static final Block WITCH_CURSE_BLOCK = registerBlock("witch_curse_block",
            new WitchCurseBlock(AbstractBlock.Settings.create().strength(2f).requiresTool()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Agentmod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Agentmod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Agentmod.LOGGER.info("Registering ModBlocks for " + Agentmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(RAW_PINK_GARNET_BLOCK);
        });
    }
}
