package net.agent.agentmod.item.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
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
import java.util.Set;

public class XrayWandItem extends Item {
    private static final int MAX_RADIUS = 64;
    private static final int MIN_RADIUS = 1;

    private static final Set<Block> ALLOWED_BLOCKS = Set.of(
            Blocks.STONE,
            Blocks.DEEPSLATE,
            Blocks.GRAVEL,
            Blocks.ANDESITE,
            Blocks.GRANITE,
            Blocks.DIORITE,
            Blocks.TUFF
    );

    public XrayWandItem(Settings settings) {
        super(settings);
    }

    // ======== Data Helpers (only radius) ========
    private int getRadius(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 1;
        return MathHelper.clamp(comp.copyNbt().getInt("radius"), MIN_RADIUS, MAX_RADIUS);
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
                Text.literal("Shape Wand (R:" + getRadius(stack) + ")"));
    }

    // ======== Place Cube ========
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        int radius = getRadius(stack);

        BlockPos center = context.getBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    BlockState current = world.getBlockState(pos);
                    if (ALLOWED_BLOCKS.contains(current.getBlock()))
                        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                }
            }
        }

        ((ServerWorld) world).playSound(null, center, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("Placed glass cube with radius " + radius), true);

        return ActionResult.SUCCESS;
    }

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
        ((ServerWorld) world).playSound(null, user.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.8f, 1.0f);

        return TypedActionResult.success(stack);
    }

    // ======== Tooltip ========
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§6Right-click: Increase radius§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease radius§r"));
        tooltip.add(Text.literal("§dUse on block: Place RxRxR glass cube§r"));
        tooltip.add(Text.literal("§6§lCurrent radius: " + getRadius(stack) + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}