package net.agent.agentmod.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (otherStack.isEmpty())
            return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);

        if (otherStack.isOf(ModItems.STARLIGHT_ASHES)) {
            int amount = otherStack.getCount();
            int stored = getAshes(stack);

            int total = stored + amount;
            int fullUses = total / ASHES_PER_USE;
            int remainder = total % ASHES_PER_USE;

            setAshes(stack, remainder);

            if (fullUses > 0) {
                int uses = getUses(stack) + fullUses;

                if (uses >= MAX_USES) {
                    uses = MAX_USES;
                    stack.decrement(1);
                }

                setUses(stack, uses);
            }

            otherStack.decrement(amount);
            return true;
        }

        if (isPotionItem(otherStack)) {
            int potionCount = otherStack.getCount();

            boolean upgraded = increasePotionLevel(otherStack, slot);
            if (!upgraded) return false;

            int addedUses = (potionCount + 31) / 32;
            int uses = getUses(stack) + addedUses;

            if (uses >= MAX_USES) {
                uses = MAX_USES;
                stack.decrement(1);
            }

            setUses(stack, uses);
            return true;
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    private boolean isPotionItem(ItemStack stack) {
        return stack.isOf(Items.POTION)
                || stack.isOf(Items.SPLASH_POTION)
                || stack.isOf(Items.LINGERING_POTION)
                || stack.isOf(Items.TIPPED_ARROW);
    }

    private boolean increasePotionLevel(ItemStack stack, Slot slot) {
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;

        Iterable<StatusEffectInstance> iterableEffects = contents.getEffects();
        if (iterableEffects == null) return false;

        List<StatusEffectInstance> effects = new ArrayList<>();
        for (StatusEffectInstance e : iterableEffects) effects.add(e);
        if (effects.isEmpty()) return false;

        boolean upgradedAny = false;
        ArrayList<StatusEffectInstance> newEffects = new ArrayList<>();

        for (StatusEffectInstance effect : effects) {

            // Effects that cannot upgrade
            if (effect.getEffectType() == StatusEffects.SLOW_FALLING ||
                    effect.getEffectType() == ModEffects.SLIMEY ||
                    effect.getEffectType() == StatusEffects.WEAVING
            ) {
                newEffects.add(effect);
                continue;
            }

            int amp = effect.getAmplifier();

            if (amp < 2) {
                upgradedAny = true;
                newEffects.add(new StatusEffectInstance(
                        effect.getEffectType(),
                        effect.getDuration(),
                        amp + 1,
                        effect.isAmbient(),
                        effect.shouldShowParticles(),
                        effect.shouldShowIcon()
                ));
            } else {
                newEffects.add(effect);
            }
        }

        if (!upgradedAny) return false;

        // Create new potion contents
        PotionContentsComponent newContents = new PotionContentsComponent(
                contents.potion(),
                contents.customColor(),
                newEffects
        );

        ItemStack newStack = new ItemStack(stack.getItem(), stack.getCount());
        newStack.set(DataComponentTypes.POTION_CONTENTS, newContents);

        slot.setStack(newStack);
        return true;
    }

    private int getUses(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = comp != null ? comp.copyNbt() : new NbtCompound();
        return nbt.getInt("WitchConduitUses");
    }

    private void setUses(ItemStack stack, int uses) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = comp != null ? comp.copyNbt() : new NbtCompound();
        nbt.putInt("WitchConduitUses", uses);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private int getAshes(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = comp != null ? comp.copyNbt() : new NbtCompound();
        return nbt.getInt("WitchConduitAshes");
    }

    private void setAshes(ItemStack stack, int amount) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = comp != null ? comp.copyNbt() : new NbtCompound();
        nbt.putInt("WitchConduitAshes", amount);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§d§lUses: " + getUses(stack) + " / " + MAX_USES + "§r"));
        tooltip.add(Text.literal("§6§lStored Ashes: " + getAshes(stack) + " / " + ASHES_PER_USE + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}