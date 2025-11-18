package net.agent.agentmod.block.custom;

import net.agent.agentmod.block.ModBlocks;
import net.agent.agentmod.effect.ModEffects;
import net.agent.agentmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MobJailBlockLevel2 extends Block {

    public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");
    public static final IntProperty DIA_COUNT = IntProperty.of("dia_count", 0, 128);
    public static final IntProperty GARNET_COUNT = IntProperty.of("garnet_count", 0, 16);

    private final int mobSize = 50;

    private final ArrayList<EntityType<?>> storedMobs = new ArrayList<>();

    public MobJailBlockLevel2(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState()
                        .with(TRIGGERED, false)
                        .with(DIA_COUNT, 0)
                        .with(GARNET_COUNT, 0)
        );
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED, DIA_COUNT, GARNET_COUNT);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {

        if (!world.isClient()) {

            if (entity instanceof ItemEntity itemEntity) {
                if (itemEntity.getStack().isOf(Items.DIAMOND)) {
                    int stackAmount = itemEntity.getStack().getCount();
                    int current = state.get(DIA_COUNT);
                    int space = 128 - current;
                    int toAdd = Math.min(stackAmount, space);

                    if (toAdd > 0) {
                        int newTotal = current + toAdd;
                        world.setBlockState(pos, state.with(DIA_COUNT, newTotal), 3);

                        itemEntity.getStack().decrement(toAdd);
                        if (itemEntity.getStack().isEmpty()) itemEntity.discard();
                    }
                }
                if (itemEntity.getStack().isOf(ModItems.PINK_GARNET)) {
                    int stackAmount = itemEntity.getStack().getCount();
                    int current = state.get(GARNET_COUNT);
                    int space = 16 - current;
                    int toAdd = Math.min(stackAmount, space);

                    if (toAdd > 0) {
                        int newTotal = current + toAdd;
                        world.setBlockState(pos, state.with(GARNET_COUNT, newTotal), 3);

                        itemEntity.getStack().decrement(toAdd);
                        if (itemEntity.getStack().isEmpty()) itemEntity.discard();
                    }
                }
            }


            if (entity instanceof LivingEntity living && !(entity instanceof PlayerEntity)) {
                if (storedMobs.size() < mobSize) {
                    if(living.hasStatusEffect(ModEffects.REDSTONE_STRUCK)){
                        storedMobs.add(living.getType());
                        releaseAllStronger((ServerWorld) world, pos);
                        living.remove(Entity.RemovalReason.DISCARDED);
                    }else {
                        storedMobs.add(living.getType());
                        living.remove(Entity.RemovalReason.DISCARDED);
                    }

                }
            }
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block,
                               BlockPos fromPos, boolean notify) {

        if (world.isClient()) return;

        boolean powered = world.isReceivingRedstonePower(pos);

        if (powered && !state.get(TRIGGERED)) {
            world.setBlockState(pos, state.with(TRIGGERED, true));
            releaseAll((ServerWorld) world, pos);
        }

        if (!powered && state.get(TRIGGERED)) {
            world.setBlockState(pos, state.with(TRIGGERED, false));
        }
    }

    private void releaseAll(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1;
        double z = pos.getZ() + 0.5;

        for (EntityType<?> type : storedMobs) {
            Entity e = type.create(world);
            if (e != null) {
                e.refreshPositionAndAngles(x+Math.random()*5*(Math.random()>0.5? -1:1), y, z+Math.random()*5*(Math.random()>0.5? -1:1),
                        world.random.nextFloat() * 360f, 0);
                world.spawnEntity(e);
            }
        }

        storedMobs.clear();
    }

    private void releaseAllStronger(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1;
        double z = pos.getZ() + 0.5;

        for (EntityType<?> type : storedMobs) {
            Entity e = type.create(world);
            if (e != null) {
                e.refreshPositionAndAngles(x+Math.random()*5*(Math.random()>0.5? -1:1), y, z+Math.random()*5*(Math.random()>0.5? -1:1),
                        world.random.nextFloat() * 360f, 0);
                if (e instanceof LivingEntity livingEntity) {
                    if(Math.random()<0.5) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 10, 1));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20, 1));
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.SHOCKED, 20 * 20, 1));
                    }
                }
                world.spawnEntity(e);
            }
        }

        storedMobs.clear();
    }
}