package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public class UpdateOneTwentyOneStructures {
    public static void m_307692_(BootstrapContext<Structure> p_335898_) {
        HolderGetter<Biome> holdergetter = p_335898_.m_255434_(Registries.BIOME);
        HolderGetter<StructureTemplatePool> holdergetter1 = p_335898_.m_255434_(Registries.TEMPLATE_POOL);
        p_335898_.m_321889_(
            BuiltinStructures.f_303057_,
            new JigsawStructure(
                Structures.structure(
                    holdergetter.getOrThrow(BiomeTags.f_303725_),
                    Arrays.stream(MobCategory.values())
                        .collect(
                            Collectors.toMap(
                                p_312179_ -> (MobCategory)p_312179_,
                                p_313247_ -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create())
                            )
                        ),
                    GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                    TerrainAdjustment.ENCAPSULATE
                ),
                holdergetter1.getOrThrow(TrialChambersStructurePools.f_302378_),
                Optional.empty(),
                20,
                UniformHeight.of(VerticalAnchor.absolute(-40), VerticalAnchor.absolute(-20)),
                false,
                Optional.empty(),
                116,
                TrialChambersStructurePools.f_302598_
            )
        );
    }
}