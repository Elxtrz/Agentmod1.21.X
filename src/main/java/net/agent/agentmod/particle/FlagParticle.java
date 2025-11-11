package net.agent.agentmod.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FlagParticle extends SpriteBillboardParticle {
    public FlagParticle(ClientWorld world, double x, double y, double z,
                        SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);

        this.velocityMultiplier = 0.8f;
        this.maxAge = 20 + world.random.nextInt(10); // random lifetime variation

        // random sprite — handled internally by SpriteProvider
        this.setSprite(spriteProvider.getSprite(world.random));

        // glowing colors
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 0.7f;
        this.alpha = 1.0f;
    }

    @Override
    public int getBrightness(float tint) {
        // Makes the particle glow at full brightness regardless of lighting
        return 0xF000F0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new FlagParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}