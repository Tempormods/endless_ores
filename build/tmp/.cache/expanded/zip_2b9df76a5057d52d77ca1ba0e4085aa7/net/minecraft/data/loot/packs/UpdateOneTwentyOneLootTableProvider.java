package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class UpdateOneTwentyOneLootTableProvider {
    public static LootTableProvider m_307143_(PackOutput p_312207_, CompletableFuture<HolderLookup.Provider> p_334418_) {
        return new LootTableProvider(
            p_312207_,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneBlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneChestLoot::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneEntityLoot::new, LootContextParamSets.ENTITY),
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneShearingLoot::new, LootContextParamSets.f_316219_),
                new LootTableProvider.SubProviderEntry(UpdateOneTwentyOneEquipmentLoot::new, LootContextParamSets.f_313897_)
            ),
            p_334418_
        );
    }
}