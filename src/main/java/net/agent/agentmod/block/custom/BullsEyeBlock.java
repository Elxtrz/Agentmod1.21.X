package net.agent.agentmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class BullsEyeBlock extends Block {

    public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");
    private static final int RADIUS = 20;

    public BullsEyeBlock(Settings settings) {
        super(settings.luminance(state -> Math.min(state.get(ENABLED) ? 12 : 0, 12)));
        this.setDefaultState(this.stateManager.getDefaultState().with(ENABLED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }



    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;
        if (!state.get(ENABLED)) return;

        BlockPos closestTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(Blocks.TARGET)) {
                        double dist = checkPos.getSquaredDistance(pos);
                        if (dist < closestDistance) {
                            closestDistance = dist;
                            closestTarget = checkPos;
                        }
                    }
                }
            }
        }

        if (closestTarget != null) {
            Vec3d direction = new Vec3d(
                    closestTarget.getX() + 0.5 - entity.getX(),
                    closestTarget.getY() + 0.5 - entity.getY(),
                    closestTarget.getZ() + 0.5 - entity.getZ()
            );
            double distance = direction.length();
            direction = direction.normalize();

            double speed = 2 + Math.min(5.0, distance / 4.0);
            Vec3d velocity = direction.multiply(speed);

            entity.setVelocity(velocity.x, velocity.y, velocity.z);
            entity.velocityModified = true;
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            world.setBlockState(pos, state.cycle(ENABLED));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("§d§lPerhaps is looking for a target to hit...§r"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
