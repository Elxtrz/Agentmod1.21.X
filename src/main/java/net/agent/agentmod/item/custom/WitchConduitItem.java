package net.agent.agentmod.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.item.tooltip.TooltipType;

import net.agent.agentmod.effect.ModEffects;
import net.agent.agentmod.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class WitchConduitItem extends Item {

    private static final int MAX_USES = 5;
    private static final int ASHES_PER_USE = 16;

    public WitchConduitItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack conduit, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {

        if (otherStack.isEmpty()) return false;

        if (otherStack.isOf(ModItems.STARLIGHT_ASHES)) {
            return handleAshes(conduit, otherStack);
        }

        if (isPotionItem(otherStack)) {

            if (getUses(conduit) <= 0) return false;

            // Reject max-level potions
            if (hasMaxLevelEffect(otherStack)) return false;

            boolean upgraded = upgradePotion(otherStack);
            if (!upgraded) return false;

            int uses = getUses(conduit) - ((otherStack.getCount() + 31) / 32);
            if (uses < 0) uses = 0;

            setUses(conduit, uses);
            return true;
        }

        return false;
    }

    private boolean handleAshes(ItemStack conduit, ItemStack ashesStack) {
        int uses = getUses(conduit);
        if (uses >= MAX_USES) return false;

        int ashCount = ashesStack.getCount();
        int possibleUses = ashCount / ASHES_PER_USE;
        if (possibleUses <= 0) return false;

        int space = MAX_USES - uses;
        int used = Math.min(space, possibleUses);

        ashesStack.decrement(used * ASHES_PER_USE);
        setUses(conduit, uses + used);

        return true;
    }

    private boolean isPotionItem(ItemStack stack) {
        return stack.isOf(Items.POTION)
                || stack.isOf(Items.SPLASH_POTION)
                || stack.isOf(Items.LINGERING_POTION)
                || stack.isOf(Items.TIPPED_ARROW);
    }

    // Rejects any potion that already has a level 3 effect
    private boolean hasMaxLevelEffect(ItemStack stack) {
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;

        for (StatusEffectInstance effect : contents.getEffects()) {
            if (effect.getAmplifier() >= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean upgradePotion(ItemStack stack) {
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;

        List<StatusEffectInstance> newEffects = new ArrayList<>();
        boolean upgraded = false;

        for (StatusEffectInstance effect : contents.getEffects()) {

            if (effect.getEffectType() == StatusEffects.SLOW_FALLING ||
                    effect.getEffectType() == ModEffects.SLIMEY ||
                    effect.getEffectType() == StatusEffects.WEAVING) {

                newEffects.add(effect);
                continue;
            }

            int amp = effect.getAmplifier();

            if (amp >= 2)
                return false;


            upgraded = true;

            newEffects.add(new StatusEffectInstance(
                    effect.getEffectType(),
                    effect.getDuration(),
                    amp + 1,
                    effect.isAmbient(),
                    effect.shouldShowParticles(),
                    effect.shouldShowIcon()
            ));
        }

        if (!upgraded) return false;

        // Force overwrite instead of stacking
        PotionContentsComponent newContents = new PotionContentsComponent(
                contents.potion(),
                contents.customColor(),
                newEffects
        );

        stack.set(DataComponentTypes.POTION_CONTENTS, newContents);
        return true;
    }

    private int getUses(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return MAX_USES;

        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains("WitchConduitUses")) return MAX_USES;

        return nbt.getInt("WitchConduitUses");
    }

    private void setUses(ItemStack stack, int uses) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = comp != null ? comp.copyNbt() : new NbtCompound();
        nbt.putInt("WitchConduitUses", uses);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§d§lUses: " + getUses(stack) + " / " + MAX_USES + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}