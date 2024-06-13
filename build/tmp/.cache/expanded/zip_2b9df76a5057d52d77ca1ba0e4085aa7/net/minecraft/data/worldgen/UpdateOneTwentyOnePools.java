package net.minecraft.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class UpdateOneTwentyOnePools {
    public static final ResourceKey<StructureTemplatePool> f_302199_ = m_307243_("empty");

    public static ResourceKey<StructureTemplatePool> m_307243_(String p_312895_) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_312895_));
    }

    public static void m_304805_(BootstrapContext<StructureTemplatePool> p_328801_, String p_312325_, StructureTemplatePool p_309820_) {
        Pools.register(p_328801_, p_312325_, p_309820_);
    }

    public static void m_304969_(BootstrapContext<StructureTemplatePool> p_328485_) {
        TrialChambersStructurePools.m_306329_(p_328485_);
    }
}