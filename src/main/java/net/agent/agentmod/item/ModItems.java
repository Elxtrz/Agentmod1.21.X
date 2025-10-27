package net.agent.agentmod.item;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.item.custom.CrackItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems{

    public static final Item PINK_GARNET = registerItem("pink_garnet",
            new Item(new Item.Settings()));

    public static final Item RAW_PINK_GARNET = registerItem("raw_pink_garnet",
            new Item(new Item.Settings()));

    public static final Item CRACK_ITEM = registerItem("crack_item",
            new CrackItem(new Item.Settings().maxDamage(100).maxCount(1)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(Agentmod.MOD_ID, name), item);
    }

    public static void registerModItems(){
        Agentmod.LOGGER.info("Registering Mod Items for " + Agentmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(PINK_GARNET);
            entries.add(RAW_PINK_GARNET);
        });
    }
}
