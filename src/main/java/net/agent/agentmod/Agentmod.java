package net.agent.agentmod;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Agentmod implements ModInitializer {
	public static final String MOD_ID = "agentmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}
