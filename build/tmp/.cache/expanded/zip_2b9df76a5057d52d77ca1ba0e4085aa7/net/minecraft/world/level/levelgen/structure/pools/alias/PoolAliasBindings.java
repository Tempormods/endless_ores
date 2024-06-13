package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class PoolAliasBindings {
    public static MapCodec<? extends PoolAliasBinding> m_306375_(Registry<MapCodec<? extends PoolAliasBinding>> p_311587_) {
        Registry.register(p_311587_, "random", Random.f_302880_);
        Registry.register(p_311587_, "random_group", RandomGroup.f_302191_);
        return Registry.register(p_311587_, "direct", Direct.f_302489_);
    }

    public static void m_304795_(BootstrapContext<StructureTemplatePool> p_330797_, Holder<StructureTemplatePool> p_311163_, List<PoolAliasBinding> p_310821_) {
        p_310821_.stream()
            .flatMap(PoolAliasBinding::m_304920_)
            .map(p_312426_ -> p_312426_.location().getPath())
            .forEach(
                p_327483_ -> Pools.register(
                        p_330797_,
                        p_327483_,
                        new StructureTemplatePool(
                            p_311163_, List.of(Pair.of(StructurePoolElement.single(p_327483_), 1)), StructureTemplatePool.Projection.RIGID
                        )
                    )
            );
    }
}