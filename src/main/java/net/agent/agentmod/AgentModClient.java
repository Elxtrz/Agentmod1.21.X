package net.agent.agentmod;

import net.agent.agentmod.entity.ModEntities;
import net.agent.agentmod.entity.client.TntArrowEntityRender;
import net.agent.agentmod.particle.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class AgentModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLEED_PARTICLE, BleedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_PARTICLE, LightningParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLACKHOLE_PARTICLE, BlackHoleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPIN_PARTICLE, SpinParticle.Factory::new);

        EntityRendererRegistry.register(ModEntities.TNT_ARROW, TntArrowEntityRender::new);
    }
}