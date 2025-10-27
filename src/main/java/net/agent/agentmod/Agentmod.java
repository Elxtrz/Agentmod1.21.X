package net.agent.agentmod;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.item.ModItemGroups;
import net.agent.agentmod.item.ModItems;
import net.agent.agentmod.util.HammerUsageEvent;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Agentmod implements ModInitializer {
	public static final String MOD_ID = "agentmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		FuelRegistry.INSTANCE.add(ModItems.STARLIGHT_ASHES, 1000);

		PlayerBlockBreakEvents.BEFORE.register(new HammerUsageEvent());
	}
}
