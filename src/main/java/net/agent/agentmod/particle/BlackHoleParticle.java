package net.agent.agentmod.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BlackHoleParticle extends SpriteBillboardParticle {

    public BlackHoleParticle(ClientWorld clientWorld, double x, double y, double z,
                             SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);

        this.velocityMultiplier = 0.9f;
        this.maxAge = 40;
        this.setSpriteForAge(spriteProvider);

        this.scale = 0.2f;
        this.red = 0.1f;
        this.green = 0.1f;
        this.blue = 0.1f;
        this.alpha = 0.9f;
    }

    @Override
    public void tick() {
        super.tick();

        // Compute the block center
        double centerX = Math.floor(this.x) + 0.5;
        double centerY = Math.floor(this.y) + 0.5;
        double centerZ = Math.floor(this.z) + 0.5;

        // Direction toward the center
        double dx = centerX - this.x;
        double dy = centerY - this.y;
        double dz = centerZ - this.z;

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.001) dist = 0.001;

        dx /= dist;
        dy /= dist;
        dz /= dist;

        // Attraction force (pulls toward center)
        double attraction = 0.05;
        this.velocityX += dx * attraction;
        this.velocityY += dy * attraction;
        this.velocityZ += dz * attraction;

        // Tangential (swirl) force for spiral motion
        double tangential = 0.03;
        this.velocityX += (-dz * tangential);
        this.velocityZ += (dx * tangential);

        // Apply damping for smooth motion
        this.velocityX *= 0.95;
        this.velocityY *= 0.95;
        this.velocityZ *= 0.95;

        // Update position
        this.move(this.velocityX, this.velocityY, this.velocityZ);

        // Fade out near the end
        if (this.age > this.maxAge * 0.8) {
            this.alpha -= 0.05f;
        }

        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    // Factory to create instances of this particle
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new BlackHoleParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}