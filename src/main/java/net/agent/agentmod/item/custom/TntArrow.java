package net.agent.agentmod.item.custom;

import net.agent.agentmod.entity.custom.TntArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TntArrow extends ArrowItem {

    public TntArrow(Item.Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new TntArrowEntity(world, shooter);
    }

    public PersistentProjectileEntity createEntity(World world, Position pos, ItemStack stack) {
        TntArrowEntity arrow = new TntArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return arrow;
    }
}
