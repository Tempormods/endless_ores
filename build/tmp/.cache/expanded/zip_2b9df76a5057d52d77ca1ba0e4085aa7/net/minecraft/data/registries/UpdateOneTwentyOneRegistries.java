package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.UpdateOneTwentyOneDamageTypes;
import net.minecraft.data.worldgen.UpdateOneTwentyOnePools;
import net.minecraft.data.worldgen.UpdateOneTwentyOneProcessorLists;
import net.minecraft.data.worldgen.UpdateOneTwentyOneStructureSets;
import net.minecraft.data.worldgen.UpdateOneTwentyOneStructures;
import net.minecraft.data.worldgen.biome.UpdateOneTwentyOneBiomeData;
import net.minecraft.world.item.armortrim.UpdateOneTwentyOneArmorTrims;
import net.minecraft.world.level.block.entity.UpdateOneTwentyOneBannerPatterns;

public class UpdateOneTwentyOneRegistries {
    private static final RegistrySetBuilder f_302566_ = new RegistrySetBuilder()
        .add(Registries.BIOME, UpdateOneTwentyOneBiomeData::m_323102_)
        .add(Registries.TEMPLATE_POOL, UpdateOneTwentyOnePools::m_304969_)
        .add(Registries.STRUCTURE, UpdateOneTwentyOneStructures::m_307692_)
        .add(Registries.STRUCTURE_SET, UpdateOneTwentyOneStructureSets::m_305865_)
        .add(Registries.PROCESSOR_LIST, UpdateOneTwentyOneProcessorLists::m_307688_)
        .add(Registries.DAMAGE_TYPE, UpdateOneTwentyOneDamageTypes::m_320672_)
        .add(Registries.BANNER_PATTERN, UpdateOneTwentyOneBannerPatterns::m_321854_)
        .add(Registries.TRIM_PATTERN, UpdateOneTwentyOneArmorTrims::m_324999_);

    public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> m_307990_(CompletableFuture<HolderLookup.Provider> p_310387_) {
        return RegistryPatchGenerator.m_305415_(p_310387_, f_302566_);
    }
}