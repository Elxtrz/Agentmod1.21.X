package net.agent.agentmod.item.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class VoidWandItem extends Item {
    private static final int MAX_RADIUS = 50;
    private static final int MIN_RADIUS = 1;

    public VoidWandItem(Settings settings) {
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

    private void updateItemName(ItemStack stack) {
        stack.set(DataComponentTypes.CUSTOM_NAME,
                Text.literal("Void Wand (R:" + getRadius(stack) + ")"));
    }

    // ======== Clear rx1xr Chunk (single Y level) ========
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        int radius = getRadius(stack);
        BlockPos center = context.getBlockPos();

        int y = center.getY();
        ServerWorld serverWorld = (ServerWorld) world;
        // set to air across X and Z within radius at the clicked Y
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                serverWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }

        serverWorld.playSound(null, center, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("Cleared area " + (radius * 2 + 1) + "x1x" + (radius * 2 + 1) + " at Y=" + y), true);

        serverWorld.spawnParticles(ModParticles.SPIN_PARTICLE,
                context.getPlayer().getX() + 0.5D, context.getPlayer().getY() + 1.0D, context.getPlayer().getZ() + 0.5D,
                4,
                0.0, 0.0, 0.0,
                0.8);

        return ActionResult.SUCCESS;
    }

    // ======== Adjust Radius ========
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

        updateItemName(stack);
        return TypedActionResult.success(stack);
    }

    // ======== Tooltip ========
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§6Right-click: Increase radius§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease radius§r"));
        tooltip.add(Text.literal("§dUse on block: Clear " + getRadius(stack) +" x 1 x " + getRadius(stack) +" chunk at clicked Y§r"));
        tooltip.add(Text.literal("§6§lCurrent radius: " + getRadius(stack) + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
