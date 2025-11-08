package net.agent.agentmod.item;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.enchantment.ModEnchantments;
import net.agent.agentmod.potion.ModPotions;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.agent.agentmod.enchantment.ModEnchantmentEffects.LIGHTNING_STRIKER;

public class ModItemGroups {
    public static final ItemGroup TUTORIAL_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "tutorial_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.PINK_GARNET))
                    .displayName(Text.translatable("itemgroup.agentmod.tutorial_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.PINK_GARNET);
                        entries.add(ModItems.RAW_PINK_GARNET);

                        entries.add(ModBlocks.RAW_PINK_GARNET_BLOCK);
                        entries.add(ModBlocks.PINK_GARNET_BLOCK);
                        entries.add(ModBlocks.PINK_GARNET_ORE);
                        entries.add(ModBlocks.PINK_GARNET_DEEPSLATE_ORE);
                        entries.add(ModBlocks.PINK_GARNET_END_ORE);
                        entries.add(ModBlocks.PINK_GARNET_NETHER_ORE);

                        entries.add(ModBlocks.MAGIC_BLOCK);

                        entries.add(ModItems.SUPER_CAULIFLOWER);
                        entries.add(ModItems.STARLIGHT_ASHES);

                        entries.add(ModItems.PINK_GARNET_HOE);
                        entries.add(ModItems.PINK_GARNET_AXE);
                        entries.add(ModItems.PINK_GARNET_PICKAXE);
                        entries.add(ModItems.PINK_GARNET_SWORD);
                        entries.add(ModItems.PINK_GARNET_SHOVEL);
                    })
                    .build());

    public static final ItemGroup WORLD_EDIT_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "world_edit_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.CRACK_WAND))
                    .displayName(Text.translatable("itemgroup.agentmod.world_edit_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.CRACK_WAND);

                        entries.add(ModItems.FLATTEN_WAND);
                        entries.add(ModItems.SPHERE_WAND);
                        entries.add(ModItems.PYRAMID_WAND);

                        entries.add(ModItems.VOID_WAND);

                        entries.add(ModItems.TNT_WAND);
                        entries.add(ModItems.XRAY_WAND);
                        entries.add(ModItems.ENTITY_WAND);
                    })
                    .build());

    public static final ItemGroup POTION_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "potion_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(Items.POTION))
                    .displayName(Text.translatable("itemgroup.agentmod.potion_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(PotionContentsComponent.createStack(Items.POTION, ModPotions.SLIMEY_POTION));
                        entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, ModPotions.SLIMEY_POTION));
                        entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, ModPotions.SLIMEY_POTION));

                        entries.add(PotionContentsComponent.createStack(Items.POTION, ModPotions.BLEED_POTION));
                        entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, ModPotions.BLEED_POTION));
                        entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, ModPotions.BLEED_POTION));
                    })
                    .build());

    public static final ItemGroup WEAPONS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "weapon_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.PINK_GARNET_HAMMER))
                    .displayName(Text.translatable("itemgroup.agentmod.weapon_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.PINK_GARNET_HAMMER);
                        entries.add(ModItems.BREEZE_SWORD);
                        entries.add(ModItems.SHOCK_SWORD);

                        entries.add(ModItems.CHAINSAW_SWORD);

//                        entries.add(ModItems.TNT_ARROW);

                        entries.add(ModBlocks.BLACK_HOLE_BLOCK);
                        entries.add(ModBlocks.X_BLOCK);
                        entries.add(ModBlocks.UPPER_4_BLOCK);
                    })
                    .build());

    public static final ItemGroup ARMOR = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Agentmod.MOD_ID, "armor_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.PINK_GARNET_CHESTPLATE))
                    .displayName(Text.translatable("itemgroup.agentmod.armor_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.PINK_GARNET_HELMET);
                        entries.add(ModItems.PINK_GARNET_CHESTPLATE);
                        entries.add(ModItems.PINK_GARNET_LEGGINGS);
                        entries.add(ModItems.PINK_GARNET_BOOTS);


                    })
                    .build());


    public static void registerItemGroups() {
        Agentmod.LOGGER.info("Registering Mod Item Groups for " + Agentmod.MOD_ID);
    }
}
