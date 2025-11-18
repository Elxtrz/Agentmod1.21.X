package net.agent.agentmod.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class EntityWandItem extends Item {

    private static final float MAX_STRENGTH = 5.0f;
    private static final float MIN_STRENGTH = 0.1f;
    private static final double TARGET_DISTANCE = 20.0;
    private static final double AREA_HALF = 50.0;

    public EntityWandItem(Settings settings) {
        super(settings);
    }

    private float getStrength(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 2.5f;
        return comp.copyNbt().getFloat("strength");
    }

    private void setStrength(ItemStack stack, float strength) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt()
                : new NbtCompound();
        tag.putFloat("strength", MathHelper.clamp(strength, MIN_STRENGTH, MAX_STRENGTH));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private boolean isNonLivingOnly(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return false;
        return comp.copyNbt().getBoolean("nonLivingOnly");
    }

    private void setNonLivingOnly(ItemStack stack, boolean nonLivingOnly) {
        NbtCompound tag = stack.get(DataComponentTypes.CUSTOM_DATA) != null
                ? Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt()
                : new NbtCompound();
        tag.putBoolean("nonLivingOnly", nonLivingOnly);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    private void updateItemName(ItemStack stack) {
        String mode = isNonLivingOnly(stack) ? "Non-living" : "All";
        String name = "Entity Wand (S:" + String.format("%.1f", getStrength(stack)) + ", M:" + mode + ")";
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));
    }

    private void applyPull(World world, PlayerEntity user, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        float strength = getStrength(stack);
        boolean nonLivingOnly = isNonLivingOnly(stack);

        Vec3d eyePos = user.getEyePos();
        Vec3d look = user.getRotationVec(1.0f);
        Vec3d target = eyePos.add(look.multiply(TARGET_DISTANCE));

        Box box = new Box(
                target.x - AREA_HALF, target.y - AREA_HALF, target.z - AREA_HALF,
                target.x + AREA_HALF, target.y + AREA_HALF, target.z + AREA_HALF
        );

        List<Entity> entities = serverWorld.getEntitiesByClass(Entity.class, box, e -> true);

        for (Entity e : entities) {
            if (Objects.equals(e, user)) continue;
            if (nonLivingOnly && e instanceof LivingEntity) continue;

            Vec3d dir = target.subtract(e.getPos());
            Vec3d pull = dir.normalize().multiply(strength);
            e.setVelocity(e.getVelocity().add(pull));
            e.velocityModified = true;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) return TypedActionResult.pass(stack);

        if (user.isSneaking()) {
            boolean cur = isNonLivingOnly(stack);
            setNonLivingOnly(stack, !cur);
            updateItemName(stack);
            user.sendMessage(Text.literal("Mode: " + (isNonLivingOnly(stack) ? "Non-living only" : "All entities")), true);
        } else {
            applyPull(world, user, stack);
            updateItemName(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        float strength = getStrength(stack);

        if (player.isSneaking())
            setStrength(stack, strength - 0.1f);
        else
            setStrength(stack, strength + 0.1f);


        updateItemName(stack);
        player.sendMessage(Text.literal("Strength: " + String.format("%.1f", getStrength(stack))), true);
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§dRight-click: Pull entities toward target point§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Toggle All / Non-living entities§r"));
        tooltip.add(Text.literal("§aRight-click block: Increase strength§r"));
        tooltip.add(Text.literal("§cSneak + Right-click block: Decrease strength§r"));
        tooltip.add(Text.literal("§eCurrent strength: " + String.format("%.1f", getStrength(stack)) + "§r"));
        tooltip.add(Text.literal("§6Mode: " + (isNonLivingOnly(stack) ? "Non-living only" : "All entities") + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}