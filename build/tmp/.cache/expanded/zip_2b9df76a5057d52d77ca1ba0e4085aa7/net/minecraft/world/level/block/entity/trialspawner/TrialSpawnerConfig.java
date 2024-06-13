package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(
    int f_302636_,
    float f_303527_,
    float f_303822_,
    float f_303267_,
    float f_303451_,
    int f_303452_,
    SimpleWeightedRandomList<SpawnData> f_303733_,
    SimpleWeightedRandomList<ResourceKey<LootTable>> f_302816_,
    ResourceKey<LootTable> f_315702_
) {
    public static final TrialSpawnerConfig f_303284_ = new TrialSpawnerConfig(
        4,
        6.0F,
        2.0F,
        2.0F,
        1.0F,
        40,
        SimpleWeightedRandomList.empty(),
        SimpleWeightedRandomList.<ResourceKey<LootTable>>builder().m_306905_(BuiltInLootTables.f_303325_).m_306905_(BuiltInLootTables.f_303815_).build(),
        BuiltInLootTables.f_315958_
    );
    public static final Codec<TrialSpawnerConfig> f_314242_ = RecordCodecBuilder.create(
        p_327364_ -> p_327364_.group(
                    Codec.intRange(1, 128).lenientOptionalFieldOf("spawn_range", f_303284_.f_302636_).forGetter(TrialSpawnerConfig::f_302636_),
                    Codec.floatRange(0.0F, Float.MAX_VALUE).lenientOptionalFieldOf("total_mobs", f_303284_.f_303527_).forGetter(TrialSpawnerConfig::f_303527_),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .lenientOptionalFieldOf("simultaneous_mobs", f_303284_.f_303822_)
                        .forGetter(TrialSpawnerConfig::f_303822_),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .lenientOptionalFieldOf("total_mobs_added_per_player", f_303284_.f_303267_)
                        .forGetter(TrialSpawnerConfig::f_303267_),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .lenientOptionalFieldOf("simultaneous_mobs_added_per_player", f_303284_.f_303451_)
                        .forGetter(TrialSpawnerConfig::f_303451_),
                    Codec.intRange(0, Integer.MAX_VALUE)
                        .lenientOptionalFieldOf("ticks_between_spawn", f_303284_.f_303452_)
                        .forGetter(TrialSpawnerConfig::f_303452_),
                    SpawnData.LIST_CODEC
                        .lenientOptionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty())
                        .forGetter(TrialSpawnerConfig::f_303733_),
                    SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceKey.codec(Registries.f_314309_))
                        .lenientOptionalFieldOf("loot_tables_to_eject", f_303284_.f_302816_)
                        .forGetter(TrialSpawnerConfig::f_302816_),
                    ResourceKey.codec(Registries.f_314309_)
                        .lenientOptionalFieldOf("items_to_drop_when_ominous", f_303284_.f_315702_)
                        .forGetter(TrialSpawnerConfig::f_315702_)
                )
                .apply(p_327364_, TrialSpawnerConfig::new)
    );

    public int m_306590_(int p_309661_) {
        return (int)Math.floor((double)(this.f_303527_ + this.f_303267_ * (float)p_309661_));
    }

    public int m_306918_(int p_312677_) {
        return (int)Math.floor((double)(this.f_303822_ + this.f_303451_ * (float)p_312677_));
    }

    public long m_324829_() {
        return 160L;
    }
}