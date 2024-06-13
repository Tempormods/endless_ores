package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

record Random(ResourceKey<StructureTemplatePool> f_302490_, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> f_302245_) implements PoolAliasBinding {
    static MapCodec<Random> f_302880_ = RecordCodecBuilder.mapCodec(
        p_311839_ -> p_311839_.group(
                    ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Random::f_302490_),
                    SimpleWeightedRandomList.wrappedCodec(ResourceKey.codec(Registries.TEMPLATE_POOL)).fieldOf("targets").forGetter(Random::f_302245_)
                )
                .apply(p_311839_, Random::new)
    );

    @Override
    public void m_305333_(RandomSource p_312605_, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> p_311412_) {
        this.f_302245_.getRandom(p_312605_).ifPresent(p_327486_ -> p_311412_.accept(this.f_302490_, p_327486_.data()));
    }

    @Override
    public Stream<ResourceKey<StructureTemplatePool>> m_304920_() {
        return this.f_302245_.unwrap().stream().map(WeightedEntry.Wrapper::data);
    }

    @Override
    public MapCodec<Random> m_304964_() {
        return f_302880_;
    }
}