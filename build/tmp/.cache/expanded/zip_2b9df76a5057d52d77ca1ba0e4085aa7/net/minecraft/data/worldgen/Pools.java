package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

    public static ResourceKey<StructureTemplatePool> createKey(String pName) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(pName));
    }

    public static void register(BootstrapContext<StructureTemplatePool> p_335139_, String pName, StructureTemplatePool pValue) {
        p_335139_.m_321889_(createKey(pName), pValue);
    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> p_332528_) {
        HolderGetter<StructureTemplatePool> holdergetter = p_332528_.m_255434_(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = holdergetter.getOrThrow(EMPTY);
        p_332528_.m_321889_(EMPTY, new StructureTemplatePool(holder, ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
        BastionPieces.bootstrap(p_332528_);
        PillagerOutpostPools.bootstrap(p_332528_);
        VillagePools.bootstrap(p_332528_);
        AncientCityStructurePieces.bootstrap(p_332528_);
        TrailRuinsStructurePools.bootstrap(p_332528_);
    }
}