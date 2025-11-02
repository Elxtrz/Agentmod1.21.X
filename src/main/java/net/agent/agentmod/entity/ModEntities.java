package net.agent.agentmod.entity;

import net.agent.agentmod.Agentmod;
import net.agent.agentmod.entity.custom.TntArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    private static final RegistryKey<EntityType<?>> FE_ARROW_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Agentmod.MOD_ID, "tnt_arrow"));

    public static final EntityType<TntArrowEntity> TNT_ARROW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Agentmod.MOD_ID, "tnt_arrow"),
            EntityType.Builder.<TntArrowEntity>create(TntArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.51f, 0.51f)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20).build("fe_arrow"));

    public static void registerModEntities() {
        Agentmod.LOGGER.info("Registering Mod Entities for " + Agentmod.MOD_ID);
    }
}
