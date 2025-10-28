package net.agent.agentmod;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.effect.ModEffects;
import net.agent.agentmod.item.ModItemGroups;
import net.agent.agentmod.item.ModItems;
import net.agent.agentmod.potion.ModPotions;
import net.agent.agentmod.util.HammerUsageEvent;
import net.agent.agentmod.util.ModTags;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
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

		AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
			if((entity instanceof CowEntity || entity instanceof SheepEntity) && !world.isClient()){
				if(playerEntity.getMainHandStack().getItem() == ModItems.CRACK_WAND){
					playerEntity.getMainHandStack().damage(1, playerEntity, EquipmentSlot.MAINHAND);
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, 1));
				}
				return ActionResult.SUCCESS;
			}

			return ActionResult.FAIL;
		});

		ModEffects.registerEffects();

		ModPotions.registerPotions();

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, Items.SLIME_BALL, ModPotions.SLIMEY_POTION);
		});
	}
}
