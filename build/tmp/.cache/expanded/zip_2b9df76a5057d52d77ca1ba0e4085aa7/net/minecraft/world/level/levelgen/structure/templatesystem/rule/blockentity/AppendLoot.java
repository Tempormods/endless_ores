package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;

public class AppendLoot implements RuleBlockEntityModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<AppendLoot> CODEC = RecordCodecBuilder.mapCodec(
        p_327506_ -> p_327506_.group(ResourceKey.codec(Registries.f_314309_).fieldOf("loot_table").forGetter(p_327505_ -> p_327505_.lootTable))
                .apply(p_327506_, AppendLoot::new)
    );
    private final ResourceKey<LootTable> lootTable;

    public AppendLoot(ResourceKey<LootTable> p_334648_) {
        this.lootTable = p_334648_;
    }

    @Override
    public CompoundTag apply(RandomSource pRandom, @Nullable CompoundTag pTag) {
        CompoundTag compoundtag = pTag == null ? new CompoundTag() : pTag.copy();
        ResourceKey.codec(Registries.f_314309_)
            .encodeStart(NbtOps.INSTANCE, this.lootTable)
            .resultOrPartial(LOGGER::error)
            .ifPresent(p_277353_ -> compoundtag.put("LootTable", p_277353_));
        compoundtag.putLong("LootTableSeed", pRandom.nextLong());
        return compoundtag;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_LOOT;
    }
}