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

public class SphereWandItem extends Item {
    private static final int MAX_RADIUS = 50;
    private static final int MIN_RADIUS = 1;

    public enum Mode { SPHERE, CIRCLE }

    public SphereWandItem(Settings settings) {
        super(settings);
    }

    // ======== Data Helpers ========
    private int getRadius(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 1;
        return comp.copyNbt().getInt("radius");
    }

    private void setRadius(ItemStack stack, int radius) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        tag.putInt("radius", MathHelper.clamp(radius, MIN_RADIUS, MAX_RADIUS));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private Mode getMode(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return Mode.CIRCLE;
        String mode = comp.copyNbt().getString("mode");
        return mode.isEmpty() ? Mode.CIRCLE : Mode.valueOf(mode);
    }

    private void setMode(ItemStack stack, Mode mode) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        tag.putString("mode", mode.name());
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
                Text.literal("Shape Wand (" + getMode(stack).name() + ", R:" + getRadius(stack) + ", " + (isFilled(stack) ? "FILLED" : "HOLLOW") + ")"));
    }

    // ======== Place Shape ========
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        int radius = getRadius(stack);
        Mode mode = getMode(stack);
        boolean filled = isFilled(stack);

        BlockPos center = context.getBlockPos();
        BlockState targetState = world.getBlockState(center);

        int startY = mode == Mode.CIRCLE ? center.getY() : center.getY() - radius;
        int endY = mode == Mode.CIRCLE ? center.getY() : center.getY() + radius;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = (mode == Mode.SPHERE ? -radius : 0); y <= (mode == Mode.SPHERE ? radius : 0); y++) {
                    double distance = Math.sqrt(x * x + z * z + (mode == Mode.SPHERE ? y * y : 0));
                    if (filled) {
                        if (distance <= radius) {
                            BlockPos pos = new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z);
                            world.setBlockState(pos, targetState);
                        }
                    } else {
                        // Hollow: include points on the surface only
                        if (distance >= radius - 1 && distance <= radius) {
                            BlockPos pos = new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z);
                            world.setBlockState(pos, targetState);
                        }
                    }
                }
            }
        }
        ((ServerWorld) world).playSound(null, center, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("Placed " + mode.name() + " " + (filled ? "FILLED" : "HOLLOW") + " with radius " + radius), true);
        return ActionResult.SUCCESS;
    }

    // ======== Adjust Radius & Toggle Mode/Property ========
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.success(stack);

        boolean sneaking = user.isSneaking();

        // Sneak + Right-click -> decrease radius
        // Normal Right-click -> increase radius
        int radius = getRadius(stack);
        radius = sneaking ? Math.max(MIN_RADIUS, radius - 1) : Math.min(MAX_RADIUS, radius + 1);
        setRadius(stack, radius);
        user.sendMessage(Text.literal("Radius set to " + radius), true);

        // If player is not sneaking -> toggle 2D/3D mode (treat as "swing/attack without shifting" behavior here)
        if (!sneaking) {
            Mode newMode = getMode(stack) == Mode.CIRCLE ? Mode.SPHERE : Mode.CIRCLE;
            setMode(stack, newMode);
            user.sendMessage(Text.literal("Mode set to " + newMode.name()), true);
        } else {
            // If player is sneaking while using the wand -> toggle filled/hollow
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
        tooltip.add(Text.literal("§6Right-click: Increase radius§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease radius§r"));
        tooltip.add(Text.literal("§dUse on block: Place shape§r"));
        tooltip.add(Text.literal("§eShift + Right-click: Toggle SPHERE/CIRCLE§r"));
        tooltip.add(Text.literal("§eSneak + Shift + Right-click: Toggle FILLED/HOLLOW§r"));
        tooltip.add(Text.literal("§6§lCurrent radius: " + getRadius(stack) + "§r"));
        tooltip.add(Text.literal("§dCurrent mode: " + getMode(stack).name() + "§r"));
        tooltip.add(Text.literal("§dCurrent property: " + (isFilled(stack) ? "FILLED" : "HOLLOW") + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}