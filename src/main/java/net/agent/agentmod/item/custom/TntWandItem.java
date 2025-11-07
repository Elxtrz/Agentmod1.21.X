package net.agent.agentmod.item.custom;

import net.agent.agentmod.particle.ModParticles;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TntWandItem extends Item {
    private static final int MAX_RADIUS = 64;
    private static final int MIN_RADIUS = 1;

    public TntWandItem(Settings settings) {
        super(settings);
    }

    // ======== Data Helpers (radius stored in item nbt) ========
    private int getRadius(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return MIN_RADIUS;
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
                Text.literal("TNT Wand (C:" + getRadius(stack) + ")"));
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // client side: trigger animation only
        if (world.isClient())
            return TypedActionResult.success(stack);

        if (!world.isClient() && world instanceof ServerWorld) {

        ServerWorld serverWorld = (ServerWorld) world;
        int radius = getRadius(stack);
        Random rand = (Random) world.getRandom();

        double yaw = Math.toRadians(user.getYaw());
        double pitch = Math.toRadians(user.getPitch());
        double dirX = -Math.sin(yaw) * Math.cos(pitch);
        double dirY = -Math.sin(pitch);
        double dirZ = Math.cos(yaw) * Math.cos(pitch);

        double eyeX = user.getX();
        double eyeY = user.getEyeY() - 0.2;
        double eyeZ = user.getZ();

        for (int i = 0; i < radius; i++) {
            double offset = 1.2;
            double spawnX = eyeX + dirX * offset;
            double spawnY = eyeY + dirY * offset;
            double spawnZ = eyeZ + dirZ * offset;

            double spread = 0.15;
            double dx = dirX + (rand.nextDouble() - 0.5) * spread;
            double dy = dirY + (rand.nextDouble() - 0.5) * spread * 0.5;
            double dz = dirZ + (rand.nextDouble() - 0.5) * spread;

            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 1e-4)
                len = 1;
            double speed = 1.4 + rand.nextDouble() * 0.4;

            TntEntity tnt = new TntEntity(world, spawnX, spawnY, spawnZ, user);
            tnt.setFuse(ThreadLocalRandom.current().nextInt(30, 70));
            tnt.setVelocity(dx / len * speed, dy / len * speed, dz / len * speed);

            serverWorld.spawnEntity(tnt);
        }
        
        serverWorld.playSound(
                null,
                user.getBlockPos(),
                SoundEvents.ENTITY_TNT_PRIMED,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );
        serverWorld.spawnParticles(
                ModParticles.SPIN_PARTICLE,
                user.getX(), user.getY() + 1.0, user.getZ(),
                10,
                0.25, 0.25, 0.25,
                0.05
        );

        }

        return TypedActionResult.success(stack);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        PlayerEntity user = context.getPlayer();
        if (user == null) return ActionResult.PASS;

        ItemStack stack = user.getStackInHand(context.getHand());
        boolean sneaking = user.isSneaking();

        int radius = getRadius(stack);
        radius = sneaking ? Math.max(MIN_RADIUS, radius - 1) : Math.min(MAX_RADIUS, radius + 1);
        setRadius(stack, radius);
        user.sendMessage(Text.literal("Count set to " + radius), true);

        updateItemName(stack);
        ((ServerWorld) world).playSound(null, user.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.8f, 1.0f);

        return ActionResult.SUCCESS;
    }

    // Tooltip
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§6Left-click: Shoot TNTs§r"));
        tooltip.add(Text.literal("§bRight-click: Increase count§r"));
        tooltip.add(Text.literal("§bSneak + Right-click: Decrease count§r"));
        tooltip.add(Text.literal("§6Current count: " + getRadius(stack) + "§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}