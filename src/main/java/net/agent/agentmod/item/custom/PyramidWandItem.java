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

public class PyramidWandItem extends Item {
    private static final int MAX_HEIGHT = 50;
    private static final int MIN_HEIGHT = 2;

    public PyramidWandItem(Settings settings) {
        super(settings);
    }

    // ======== Data Helpers ========
    private int getHeight(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return MIN_HEIGHT;
        return comp.copyNbt().getInt("height");
    }

    private void setHeight(ItemStack stack, int height) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        tag.putInt("height", MathHelper.clamp(height, MIN_HEIGHT, MAX_HEIGHT));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private boolean isFilled(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return true;
        return comp.copyNbt().getBoolean("filled");
    }

    private void setFilled(ItemStack stack, boolean filled) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        tag.putBoolean("filled", filled);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private void updateItemName(ItemStack stack) {
        stack.set(DataComponentTypes.CUSTOM_NAME,
                Text.literal("Pyramid Wand (H:" + getHeight(stack) + ", " + (isFilled(stack) ? "FILLED" : "HOLLOW") + ")"));
    }

    // ======== Place Pyramid ========
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        int height = getHeight(stack);
        boolean filled = isFilled(stack);

        BlockPos base = context.getBlockPos();
        BlockState targetState = world.getBlockState(base);

        // Build pyramid layer by layer
        for (int y = 0; y < height; y++) {
            int layerSize = height - y - 1; // <-- subtract 1 to make top layer = 1 block
            for (int x = -layerSize; x <= layerSize; x++) {
                for (int z = -layerSize; z <= layerSize; z++) {
                    if (filled || x == -layerSize || x == layerSize || z == -layerSize || z == layerSize) {
                        BlockPos pos = base.add(x, y, z);
                        world.setBlockState(pos, targetState);
                    }
                }
            }
        }


        ((ServerWorld) world).playSound(null, base, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("Placed " + (filled ? "FILLED" : "HOLLOW") + " pyramid with height " + height), true);
        return ActionResult.SUCCESS;
    }

    // ======== Adjust Height & Toggle Hollow/Filled ========
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.success(stack);

        boolean sneaking = user.isSneaking();

        // Sneak + Right-click -> decrease height
        // Normal Right-click -> increase height
        int height = getHeight(stack);
        height = sneaking ? Math.max(MIN_HEIGHT, height - 1) : Math.min(MAX_HEIGHT, height + 1);
        setHeight(stack, height);
        user.sendMessage(Text.literal("Height set to " + height), true);

        // Toggle filled/hollow if sneaking
        if (sneaking) {
            setFilled(stack, !isFilled(stack));
            user.sendMessage(Text.literal("Toggled to " + (isFilled(stack) ? "FILLED" : "HOLLOW")), true);
        }

        updateItemName(stack);
        ((ServerWorld) world).playSound(null, user.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.8f, 1.0f);

        return TypedActionResult.success(stack);
    }

    // ======== Tooltip ========
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§6Right-click: Increase height§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease height§r"));
        tooltip.add(Text.literal("§dUse on block: Place pyramid§r"));
        tooltip.add(Text.literal("§eSneak + Right-click: Toggle FILLED/HOLLOW§r"));
        tooltip.add(Text.literal("§6§lCurrent height: " + getHeight(stack) + "§r"));
        tooltip.add(Text.literal("§dCurrent property: " + (isFilled(stack) ? "FILLED" : "HOLLOW") + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}