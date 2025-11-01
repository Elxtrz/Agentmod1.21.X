package net.agent.agentmod.entity;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.entity.custom.TntArrowEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<TntArrowEntity> TNT_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Agentmod.MOD_ID, "tnt_arrow"), // <-- use .of
            FabricEntityTypeBuilder.<TntArrowEntity>create(SpawnGroup.MISC, TntArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );


    public static void registerModEntities() {
        Agentmod.LOGGER.info("Registering Mod Entities for " + Agentmod.MOD_ID);
    }
}
