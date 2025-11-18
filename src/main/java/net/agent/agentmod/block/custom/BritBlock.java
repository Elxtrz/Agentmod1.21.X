package net.agent.agentmod.block.custom;

import net.agent.agentmod.item.ModItems;
import net.agent.agentmod.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BritBlock extends Block {
    public static final IntProperty GOLD_COUNT = IntProperty.of("gold_count", 0, 128);

    public BritBlock(Settings settings) {
        super(settings.luminance(state -> Math.min(state.get(GOLD_COUNT) / 10, 12)));
        this.setDefaultState(this.stateManager.getDefaultState().with(GOLD_COUNT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GOLD_COUNT);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, net.minecraft.entity.Entity entity) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        ItemStack stack = itemEntity.getStack();

        // Absorb gold ingots into internal storage
        if (stack.getItem() == Items.GOLD_INGOT) {
            int current = state.get(GOLD_COUNT);
            int space = 128 - current;
            int toAdd = Math.min(stack.getCount(), space);

            if (toAdd > 0) {
                int newCount = current + toAdd;
                int oldTens = current / 10;
                int newTens = newCount / 10;

                if (!world.isClient()) {
                    // Update brightness + stored gold count
                    BlockState newState = state.with(GOLD_COUNT, newCount);
                    world.setBlockState(pos, newState, 3);

                    if (newTens > oldTens) {
                        int milestones = newTens - oldTens;
                        for (int i = 0; i < milestones; i++) {
                            int dropAmount = ThreadLocalRandom.current().nextInt(1, 4);
                            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                    new ItemStack(ModItems.TEA, dropAmount));
                        }

                        if (world instanceof ServerWorld serverWorld) {
                            ParticleEffect particle = ModParticles.FLAG_PARTICLE;
                            for (int i = 0; i < 20; i++) {
                                double offsetX = ThreadLocalRandom.current().nextDouble(-0.4, 0.4);
                                double offsetY = ThreadLocalRandom.current().nextDouble(0.1, 0.8);
                                double offsetZ = ThreadLocalRandom.current().nextDouble(-0.4, 0.4);
                                serverWorld.spawnParticles(
                                        particle,
                                        pos.getX() + 0.5 + offsetX,
                                        pos.getY() + offsetY,
                                        pos.getZ() + 0.5 + offsetZ,
                                        3,
                                        0, 0, 0, 0.0
                                );
                            }
                        }
                    }
                }

                // Consume items
                stack.decrement(toAdd);
                if (stack.isEmpty()) {
                    itemEntity.remove(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
                }
            }

            super.onSteppedOn(world, pos, state, entity);
            return;
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack held = player.getMainHandStack();

        if (held.getItem() == ModItems.TEA && !world.isClient()) {
            held.decrement(1);

            ArrayList<ItemStack> drops = getPossibleDrops();

            for (int i = 0; i < drops.toArray().length; i++)
                ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drops.get(i));

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hit);
    }

    private ArrayList<ItemStack> getPossibleDrops() {
        ArrayList<ItemStack> drops = new ArrayList<>();

        Random rand = new Random();
        int choice = rand.nextInt(7);

        // Common drops
        drops.add(new ItemStack(Items.IRON_INGOT, rand.nextInt(1, 4)));
        drops.add(new ItemStack(Items.GOLD_INGOT, rand.nextInt(1, 3)));
        drops.add(new ItemStack(Items.COOKED_PORKCHOP, rand.nextInt(1, 5)));

        // Uncommon drops
        if (choice == 0)
            drops.add(new ItemStack(Items.DIAMOND, 2));
        else if (choice == 1)
            drops.add(new ItemStack(Items.GOLDEN_APPLE, 5));
        else if (choice == 2)
            drops.add(new ItemStack(ModItems.TEA, rand.nextInt(1, 3)));


        // Rare drops
        choice = rand.nextInt(100);
        if(choice >= 94)
            drops.add(new ItemStack(ModItems.PINK_GARNET_HAMMER));

        // Legendary drop
        choice = rand.nextInt(500);
        if(choice >= 495) {
            drops.add(new ItemStack(ModItems.PINK_GARNET_HELMET));
            drops.add(new ItemStack(ModItems.PINK_GARNET_CHESTPLATE));
            drops.add(new ItemStack(ModItems.PINK_GARNET_LEGGINGS));
            drops.add(new ItemStack(ModItems.PINK_GARNET_BOOTS));
        }

        ItemStack paperStack = new ItemStack(Items.PAPER, 32);
        String rainbowName = "§c§lN§6§lO §e§lT§a§lA§b§lX§d§lA§c§lT§6§lI§e§lO§a§lN §b§lW§d§lI§c§lT§6§lH§e§lO§a§lU§b§lT §d§lR§c§lE§6§lP§e§lR§a§lE§b§lS§d§lE§c§lN§6§lT§e§lA§a§lT§b§lI§d§lO§c§lN";
        paperStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(rainbowName));
        drops.add(paperStack);

        return drops;
    }
}
