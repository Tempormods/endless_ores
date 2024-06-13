package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface PoolAliasBinding {
    Codec<PoolAliasBinding> f_303072_ = BuiltInRegistries.f_302795_.byNameCodec().dispatch(PoolAliasBinding::m_304964_, Function.identity());

    void m_305333_(RandomSource p_309848_, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> p_311325_);

    Stream<ResourceKey<StructureTemplatePool>> m_304920_();

    static Direct m_305746_(String p_310882_, String p_311396_) {
        return m_304861_(Pools.createKey(p_310882_), Pools.createKey(p_311396_));
    }

    static Direct m_304861_(ResourceKey<StructureTemplatePool> p_311763_, ResourceKey<StructureTemplatePool> p_312427_) {
        return new Direct(p_311763_, p_312427_);
    }

    static Random m_305102_(String p_311792_, SimpleWeightedRandomList<String> p_310543_) {
        SimpleWeightedRandomList.Builder<ResourceKey<StructureTemplatePool>> builder = SimpleWeightedRandomList.builder();
        p_310543_.unwrap().forEach(p_327480_ -> builder.add(Pools.createKey(p_327480_.data()), p_327480_.getWeight().asInt()));
        return m_308009_(Pools.createKey(p_311792_), builder.build());
    }

    static Random m_308009_(ResourceKey<StructureTemplatePool> p_311453_, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> p_311769_) {
        return new Random(p_311453_, p_311769_);
    }

    static RandomGroup m_307343_(SimpleWeightedRandomList<List<PoolAliasBinding>> p_310479_) {
        return new RandomGroup(p_310479_);
    }

    MapCodec<? extends PoolAliasBinding> m_304964_();
}