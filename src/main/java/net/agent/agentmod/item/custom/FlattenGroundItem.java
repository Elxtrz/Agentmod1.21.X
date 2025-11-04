package net.agent.agentmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.item.tooltip.TooltipType;

import java.util.List;

public class FlattenGroundItem extends Item {
    private static final int MAX_RADIUS = 50;
    private static final int MIN_RADIUS = 1;

    public FlattenGroundItem(Settings settings) {
        super(settings);
    }

    private int getRadius(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 1;
        return comp.copyNbt().getInt("radius");
    }

    private void setRadius(ItemStack stack, int radius) {
        NbtCompound tag = new NbtCompound();
        tag.putInt("radius", MathHelper.clamp(radius, 1, 50));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private void updateItemName(ItemStack stack, int radius) {
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Flatten Wand (Radius: " + radius + ")"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        int radius = getRadius(stack);
        BlockPos center = context.getBlockPos();
        BlockState targetState = world.getBlockState(center);

        int y = center.getY();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                world.setBlockState(pos, targetState);
            }
        }

        ((ServerWorld) world).playSound(
                null, center, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f
        );

        player.sendMessage(Text.literal("Flattened ground with radius " + radius), true);
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.success(stack);

        int radius = getRadius(stack);

        if (user.isSneaking()) {
            radius = Math.max(MIN_RADIUS, radius - 1);
        } else {
            radius = Math.min(MAX_RADIUS, radius + 1);
        }

        setRadius(stack, radius);
        updateItemName(stack, radius);

        ((ServerWorld) world).playSound(
                null, user.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.8f, 1.0f
        );

        user.sendMessage(Text.literal("Radius set to " + radius), true);

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§6Right-click: Increase radius§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease radius§r"));
        tooltip.add(Text.literal("§dUse on block: Flatten same Y-level§r"));
        tooltip.add(Text.literal("§6§lCurrent radius: " + getRadius(stack)+"§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}