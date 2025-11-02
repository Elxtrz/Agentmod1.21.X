package net.agent.agentmod.entity.client;

import net.agent.agentmod.entity.custom.TntArrowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TntArrowEntityRender extends ProjectileEntityRenderer<TntArrowEntity> {

    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");

    public TntArrowEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(TntArrowEntity entity) {
        return TEXTURE;
    }
}