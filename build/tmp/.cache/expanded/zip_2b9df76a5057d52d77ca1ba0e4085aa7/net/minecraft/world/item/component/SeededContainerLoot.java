package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record SeededContainerLoot(ResourceKey<LootTable> f_314778_, long f_314296_) {
    public static final Codec<SeededContainerLoot> f_315295_ = RecordCodecBuilder.create(
        p_328588_ -> p_328588_.group(
                    ResourceKey.codec(Registries.f_314309_).fieldOf("loot_table").forGetter(SeededContainerLoot::f_314778_),
                    Codec.LONG.optionalFieldOf("seed", Long.valueOf(0L)).forGetter(SeededContainerLoot::f_314296_)
                )
                .apply(p_328588_, SeededContainerLoot::new)
    );
}