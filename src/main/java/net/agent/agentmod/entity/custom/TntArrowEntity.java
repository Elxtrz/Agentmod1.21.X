package net.agent.agentmod.entity.custom;

import net.agent.agentmod.entity.ModEntities;
import net.agent.agentmod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TntArrowEntity extends ArrowEntity {

    private int fuse = 0;

    public TntArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TntArrowEntity(World world, LivingEntity owner) {
        super(ModEntities.TNT_ARROW, world);
        this.setOwner(owner);
    }

    public TntArrowEntity(World world, double x, double y, double z) {
        super(ModEntities.TNT_ARROW, world);
        this.updatePosition(x, y, z);
    }



    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient && !this.inGround) {
            this.getWorld().addParticle(ParticleTypes.SMALL_FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient()) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.5F, World.ExplosionSourceType.TNT);
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (!this.getWorld().isClient()) {
            this.getWorld().createExplosion(this, blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z, 4.0F, World.ExplosionSourceType.TNT);
            this.discard();
        }
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(ModItems.TNT_ARROW);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Fuse")) this.fuse = nbt.getInt("Fuse");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Fuse", this.fuse);
    }
}