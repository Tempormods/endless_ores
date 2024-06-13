package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public abstract class BiomeData {
    public static void bootstrap(BootstrapContext<Biome> p_333300_) {
        HolderGetter<PlacedFeature> holdergetter = p_333300_.m_255434_(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> holdergetter1 = p_333300_.m_255434_(Registries.CONFIGURED_CARVER);
        p_333300_.m_321889_(Biomes.THE_VOID, OverworldBiomes.theVoid(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.PLAINS, OverworldBiomes.plains(holdergetter, holdergetter1, false, false, false));
        p_333300_.m_321889_(Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(holdergetter, holdergetter1, true, false, false));
        p_333300_.m_321889_(Biomes.SNOWY_PLAINS, OverworldBiomes.plains(holdergetter, holdergetter1, false, true, false));
        p_333300_.m_321889_(Biomes.ICE_SPIKES, OverworldBiomes.plains(holdergetter, holdergetter1, false, true, true));
        p_333300_.m_321889_(Biomes.DESERT, OverworldBiomes.desert(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.SWAMP, OverworldBiomes.swamp(holdergetter, holdergetter1, p_329782_ -> {
        }));
        p_333300_.m_321889_(Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp(holdergetter, holdergetter1, p_331723_ -> {
        }));
        p_333300_.m_321889_(Biomes.FOREST, OverworldBiomes.forest(holdergetter, holdergetter1, false, false, false));
        p_333300_.m_321889_(Biomes.FLOWER_FOREST, OverworldBiomes.forest(holdergetter, holdergetter1, false, false, true));
        p_333300_.m_321889_(Biomes.BIRCH_FOREST, OverworldBiomes.forest(holdergetter, holdergetter1, true, false, false));
        p_333300_.m_321889_(Biomes.DARK_FOREST, OverworldBiomes.darkForest(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(holdergetter, holdergetter1, true, true, false));
        p_333300_.m_321889_(Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.TAIGA, OverworldBiomes.taiga(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.SAVANNA, OverworldBiomes.savanna(holdergetter, holdergetter1, false, false));
        p_333300_.m_321889_(Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(holdergetter, holdergetter1, false, true));
        p_333300_.m_321889_(Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(holdergetter, holdergetter1, true, false));
        p_333300_.m_321889_(Biomes.JUNGLE, OverworldBiomes.jungle(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.BADLANDS, OverworldBiomes.badlands(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.MEADOW, OverworldBiomes.meadowOrCherryGrove(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.CHERRY_GROVE, OverworldBiomes.meadowOrCherryGrove(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.GROVE, OverworldBiomes.grove(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.RIVER, OverworldBiomes.river(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.FROZEN_RIVER, OverworldBiomes.river(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.BEACH, OverworldBiomes.beach(holdergetter, holdergetter1, false, false));
        p_333300_.m_321889_(Biomes.SNOWY_BEACH, OverworldBiomes.beach(holdergetter, holdergetter1, true, false));
        p_333300_.m_321889_(Biomes.STONY_SHORE, OverworldBiomes.beach(holdergetter, holdergetter1, false, true));
        p_333300_.m_321889_(Biomes.WARM_OCEAN, OverworldBiomes.warmOcean(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.OCEAN, OverworldBiomes.ocean(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.DEEP_OCEAN, OverworldBiomes.ocean(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(holdergetter, holdergetter1, false));
        p_333300_.m_321889_(Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(holdergetter, holdergetter1, true));
        p_333300_.m_321889_(Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.LUSH_CAVES, OverworldBiomes.lushCaves(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.DEEP_DARK, OverworldBiomes.deepDark(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.NETHER_WASTES, NetherBiomes.netherWastes(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.WARPED_FOREST, NetherBiomes.warpedForest(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.THE_END, EndBiomes.theEnd(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.END_HIGHLANDS, EndBiomes.endHighlands(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.END_MIDLANDS, EndBiomes.endMidlands(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands(holdergetter, holdergetter1));
        p_333300_.m_321889_(Biomes.END_BARRENS, EndBiomes.endBarrens(holdergetter, holdergetter1));
    }
}