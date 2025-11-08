package net.agent.agentmod.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GravityParticle extends SpriteBillboardParticle {
    public GravityParticle(ClientWorld clientWorld, double x, double y, double z,
                           SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);

        this.velocityMultiplier = 0.8f;

        this.maxAge = 120;
        this.setSpriteForAge(spriteProvider);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (++this.age >= this.maxAge) {
            this.markDead();
            return;
        }

        // Force a constant downward velocity so the particle always falls straight down
        this.velocityY = -0.05d;

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        // Apply horizontal drag
        this.velocityX *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;

        // If we've hit a non-air block below, stop the particle
        BlockPos below = new BlockPos((int)Math.floor(this.x), (int)Math.floor(this.y - 0.5), (int)Math.floor(this.z));
        if (!this.world.getBlockState(below).isAir()) {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new GravityParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}