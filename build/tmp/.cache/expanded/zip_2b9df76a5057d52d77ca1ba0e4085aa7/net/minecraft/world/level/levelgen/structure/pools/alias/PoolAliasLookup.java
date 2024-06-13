package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@FunctionalInterface
public interface PoolAliasLookup {
    PoolAliasLookup f_303598_ = p_311984_ -> p_311984_;

    ResourceKey<StructureTemplatePool> m_307301_(ResourceKey<StructureTemplatePool> p_311151_);

    static PoolAliasLookup m_307806_(List<PoolAliasBinding> p_310301_, BlockPos p_313211_, long p_311952_) {
        if (p_310301_.isEmpty()) {
            return f_303598_;
        } else {
            RandomSource randomsource = RandomSource.create(p_311952_).forkPositional().at(p_313211_);
            Builder<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> builder = ImmutableMap.builder();
            p_310301_.forEach(p_311006_ -> p_311006_.m_305333_(randomsource, builder::put));
            Map<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> map = builder.build();
            return p_312268_ -> Objects.requireNonNull(map.getOrDefault(p_312268_, p_312268_), () -> "alias " + p_312268_ + " was mapped to null value");
        }
    }
}