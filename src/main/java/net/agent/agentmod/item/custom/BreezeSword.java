package net.agent.agentmod.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreezeSword extends SwordItem {

    public BreezeSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            System.out.println("[BreezeSword] postHit triggered");

            // Launch both entities
            launchEntity(attacker);
            launchEntity(target);

            // Spawn wind burst
            spawnWindBursts(serverWorld, attacker);
        } else {
            System.out.println("[BreezeSword] Running client side — skipping");
        }

        return super.postHit(stack, target, attacker);
    }

    private void launchEntity(LivingEntity entity) {
        Vec3d velocity = entity.getVelocity();
        entity.setVelocity(velocity.x, 1.5, velocity.z);
        entity.velocityModified = true;
        entity.velocityDirty = true;
        System.out.println("[BreezeSword] Launched entity: " + entity.getName().getString());
    }

    private void spawnWindBursts(ServerWorld world, LivingEntity entity) {
        System.out.println("[BreezeSword] Spawning wind charges...");
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            double xDir = Math.cos(angle);
            double zDir = Math.sin(angle);

            WindChargeEntity wind = new WindChargeEntity(EntityType.WIND_CHARGE, world);
            wind.refreshPositionAndAngles(entity.getX(), entity.getY() + 1.5, entity.getZ(), 0, 0);
            wind.setVelocity(xDir * 1.5, 0.1, zDir * 1.5);
            wind.setOwner(entity);

            boolean success = world.spawnEntity(wind);
            System.out.println("[BreezeSword] Wind charge spawned: " + success);
        }
    }
}
