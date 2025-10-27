package net.agent.agentmod.item;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup TUTORIAL_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "tutorial_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.PINK_GARNET))
                    .displayName(Text.translatable("itemgroup.agentmod.tutorial_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.PINK_GARNET);
                        entries.add(ModItems.RAW_PINK_GARNET);
                        entries.add(ModBlocks.TEST_BLOCK);

                        entries.add(ModBlocks.MAGIC_BLOCK);

                        entries.add(ModItems.SUPER_CAULIFLOWER);
                        entries.add(ModItems.STARLIGHT_ASHES);
                    })
                    .build());

    public static final ItemGroup WORLD_EDIT_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "world_edit_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(Items.COMMAND_BLOCK))
                    .displayName(Text.translatable("itemgroup.agentmod.world_edit_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.CRACK_WAND);
                    })
                    .build());

    public static void registerItemGroups() {
        Agentmod.LOGGER.info("Registering Mod Item Groups for " + Agentmod.MOD_ID);
    }
}
