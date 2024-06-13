package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

record Direct(ResourceKey<StructureTemplatePool> f_303616_, ResourceKey<StructureTemplatePool> f_302715_) implements PoolAliasBinding {
    static MapCodec<Direct> f_302489_ = RecordCodecBuilder.mapCodec(
        p_311220_ -> p_311220_.group(
                    ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Direct::f_303616_),
                    ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("target").forGetter(Direct::f_302715_)
                )
                .apply(p_311220_, Direct::new)
    );

    @Override
    public void m_305333_(RandomSource p_312348_, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> p_310565_) {
        p_310565_.accept(this.f_303616_, this.f_302715_);
    }

    @Override
    public Stream<ResourceKey<StructureTemplatePool>> m_304920_() {
        return Stream.of(this.f_302715_);
    }

    @Override
    public MapCodec<Direct> m_304964_() {
        return f_302489_;
    }
}