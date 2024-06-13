package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class UpdateOneTwentyOneBiomeData {
    public static void m_323102_(BootstrapContext<Biome> p_333524_) {
        HolderGetter<PlacedFeature> holdergetter = p_333524_.m_255434_(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> holdergetter1 = p_333524_.m_255434_(Registries.CONFIGURED_CARVER);
        MobSpawnSettings.SpawnerData mobspawnsettings$spawnerdata = new MobSpawnSettings.SpawnerData(EntityType.f_316281_, 50, 4, 4);
        p_333524_.m_321889_(
            Biomes.MANGROVE_SWAMP,
            OverworldBiomes.mangroveSwamp(holdergetter, holdergetter1, p_330499_ -> p_330499_.addSpawn(MobCategory.MONSTER, mobspawnsettings$spawnerdata))
        );
        p_333524_.m_321889_(
            Biomes.SWAMP,
            OverworldBiomes.swamp(holdergetter, holdergetter1, p_329266_ -> p_329266_.addSpawn(MobCategory.MONSTER, mobspawnsettings$spawnerdata))
        );
    }
}