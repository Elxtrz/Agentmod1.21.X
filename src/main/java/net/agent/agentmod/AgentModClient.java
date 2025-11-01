package net.agent.agentmod;

import net.agent.agentmod.entity.ModEntities;
import net.agent.agentmod.entity.custom.TntArrowEntity;
import net.agent.agentmod.particle.BleedParticle;
import net.agent.agentmod.particle.LightningParticle;
import net.agent.agentmod.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;

public class AgentModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLEED_PARTICLE, BleedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_PARTICLE, LightningParticle.Factory::new);
    }
}