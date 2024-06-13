package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Registration for {@link LootPoolEntryType}.
 */
public class LootPoolEntries {
    public static final Codec<LootPoolEntryContainer> CODEC = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE
        .byNameCodec()
        .dispatch(LootPoolEntryContainer::getType, LootPoolEntryType::codec);
    public static final LootPoolEntryType EMPTY = register("empty", EmptyLootItem.CODEC);
    public static final LootPoolEntryType ITEM = register("item", LootItem.CODEC);
    public static final LootPoolEntryType f_314057_ = register("loot_table", NestedLootTable.f_315961_);
    public static final LootPoolEntryType DYNAMIC = register("dynamic", DynamicLoot.CODEC);
    public static final LootPoolEntryType TAG = register("tag", TagEntry.CODEC);
    public static final LootPoolEntryType ALTERNATIVES = register("alternatives", AlternativesEntry.CODEC);
    public static final LootPoolEntryType SEQUENCE = register("sequence", SequentialEntry.CODEC);
    public static final LootPoolEntryType GROUP = register("group", EntryGroup.CODEC);

    private static LootPoolEntryType register(String pName, MapCodec<? extends LootPoolEntryContainer> p_328987_) {
        return Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(pName), new LootPoolEntryType(p_328987_));
    }
}