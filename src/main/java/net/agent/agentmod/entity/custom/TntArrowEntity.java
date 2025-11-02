package net.agent.agentmod.entity.custom;

import net.agent.agentmod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TntArrowEntity extends PersistentProjectileEntity {
    private static final int MAX_POTION_DURATION_TICKS = 600;
    private static final int NO_POTION_COLOR = -1;
    private static final byte PARTICLE_EFFECT_STATUS = 0;

    public TntArrowEntity(EntityType<? extends TntArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TntArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(EntityType.ARROW, x, y, z, world, stack, shotFrom);
    }

    public TntArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(EntityType.ARROW, owner, world, stack, shotFrom);
    }

    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        }
    }

    private void spawnParticles(int amount) {
        this.getWorld().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    protected void onHit(LivingEntity target) {
        super.onHit(target);
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 5.0f, World.ExplosionSourceType.MOB);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 5.0f, World.ExplosionSourceType.MOB);
        this.discard();
    }

    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.TNT_ARROW);
    }
}
