package net.agent.agentmod.item;

import net.agent.agentmod.AgentMod;
import net.agent.agentmod.item.custom.CrackItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems{

    public static final Item PINK_GARNET = registerItem("pink_garnet",
            new Item(new Item.Settings()));

    public static final Item RAW_PINK_GARNET = registerItem("raw_pink_garnet",
            new Item(new Item.Settings()));

    public static final Item CRACK_WAND = registerItem("crack_wand",
            new CrackItem(new Item.Settings().maxDamage(100).maxCount(1)));

    public static final Item SUPER_CAULIFLOWER = registerItem("super_cauliflower",
            new Item(new Item.Settings().food(ModFoodComponents.SUPER_CAULIFLOWER)) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("item.agentmod.super_cauliflower.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }
            });

    public static final Item STARLIGHT_ASHES = registerItem("starlight_ashes",
            new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(AgentMod.MOD_ID, name), item);
    }

    public static void registerModItems(){
        AgentMod.LOGGER.info("Registering Mod Items for " + AgentMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(PINK_GARNET);
            entries.add(RAW_PINK_GARNET);
        });
    }
}
