package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TradeRebalanceLootTableProvider {
    public static LootTableProvider create(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> p_328544_) {
        return new LootTableProvider(
            pPackOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(TradeRebalanceChestLoot::new, LootContextParamSets.CHEST)), p_328544_
        );
    }
}