package net.minecraft.world.level.levelgen.structure.structures;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class ShipwreckPieces {
    private static final int f_316049_ = 32;
    static final BlockPos PIVOT = new BlockPos(4, 0, 15);
    private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{
        new ResourceLocation("shipwreck/with_mast"),
        new ResourceLocation("shipwreck/sideways_full"),
        new ResourceLocation("shipwreck/sideways_fronthalf"),
        new ResourceLocation("shipwreck/sideways_backhalf"),
        new ResourceLocation("shipwreck/rightsideup_full"),
        new ResourceLocation("shipwreck/rightsideup_fronthalf"),
        new ResourceLocation("shipwreck/rightsideup_backhalf"),
        new ResourceLocation("shipwreck/with_mast_degraded"),
        new ResourceLocation("shipwreck/rightsideup_full_degraded"),
        new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"),
        new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")
    };
    private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{
        new ResourceLocation("shipwreck/with_mast"),
        new ResourceLocation("shipwreck/upsidedown_full"),
        new ResourceLocation("shipwreck/upsidedown_fronthalf"),
        new ResourceLocation("shipwreck/upsidedown_backhalf"),
        new ResourceLocation("shipwreck/sideways_full"),
        new ResourceLocation("shipwreck/sideways_fronthalf"),
        new ResourceLocation("shipwreck/sideways_backhalf"),
        new ResourceLocation("shipwreck/rightsideup_full"),
        new ResourceLocation("shipwreck/rightsideup_fronthalf"),
        new ResourceLocation("shipwreck/rightsideup_backhalf"),
        new ResourceLocation("shipwreck/with_mast_degraded"),
        new ResourceLocation("shipwreck/upsidedown_full_degraded"),
        new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"),
        new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"),
        new ResourceLocation("shipwreck/sideways_full_degraded"),
        new ResourceLocation("shipwreck/sideways_fronthalf_degraded"),
        new ResourceLocation("shipwreck/sideways_backhalf_degraded"),
        new ResourceLocation("shipwreck/rightsideup_full_degraded"),
        new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"),
        new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")
    };
    static final Map<String, ResourceKey<LootTable>> MARKERS_TO_LOOT = Map.of(
        "map_chest", BuiltInLootTables.SHIPWRECK_MAP, "treasure_chest", BuiltInLootTables.SHIPWRECK_TREASURE, "supply_chest", BuiltInLootTables.SHIPWRECK_SUPPLY
    );

    public static ShipwreckPieces.ShipwreckPiece m_319528_(
        StructureTemplateManager p_334187_, BlockPos p_334016_, Rotation p_333925_, StructurePieceAccessor p_330683_, RandomSource p_331305_, boolean p_332987_
    ) {
        ResourceLocation resourcelocation = Util.getRandom(p_332987_ ? STRUCTURE_LOCATION_BEACHED : STRUCTURE_LOCATION_OCEAN, p_331305_);
        ShipwreckPieces.ShipwreckPiece shipwreckpieces$shipwreckpiece = new ShipwreckPieces.ShipwreckPiece(
            p_334187_, resourcelocation, p_334016_, p_333925_, p_332987_
        );
        p_330683_.addPiece(shipwreckpieces$shipwreckpiece);
        return shipwreckpieces$shipwreckpiece;
    }

    public static class ShipwreckPiece extends TemplateStructurePiece {
        private final boolean isBeached;

        public ShipwreckPiece(StructureTemplateManager pStructureTemplateManager, ResourceLocation pLocation, BlockPos pPos, Rotation pRotation, boolean pIsBeached) {
            super(StructurePieceType.SHIPWRECK_PIECE, 0, pStructureTemplateManager, pLocation, pLocation.toString(), makeSettings(pRotation), pPos);
            this.isBeached = pIsBeached;
        }

        public ShipwreckPiece(StructureTemplateManager pStructureTemplateManager, CompoundTag pTag) {
            super(StructurePieceType.SHIPWRECK_PIECE, pTag, pStructureTemplateManager, p_229383_ -> makeSettings(Rotation.valueOf(pTag.getString("Rot"))));
            this.isBeached = pTag.getBoolean("isBeached");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag pTag) {
            super.addAdditionalSaveData(pContext, pTag);
            pTag.putBoolean("isBeached", this.isBeached);
            pTag.putString("Rot", this.placeSettings.getRotation().name());
        }

        private static StructurePlaceSettings makeSettings(Rotation pRotation) {
            return new StructurePlaceSettings()
                .setRotation(pRotation)
                .setMirror(Mirror.NONE)
                .setRotationPivot(ShipwreckPieces.PIVOT)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        @Override
        protected void handleDataMarker(String pName, BlockPos pPos, ServerLevelAccessor pLevel, RandomSource pRandom, BoundingBox pBox) {
            ResourceKey<LootTable> resourcekey = ShipwreckPieces.MARKERS_TO_LOOT.get(pName);
            if (resourcekey != null) {
                RandomizableContainer.m_307915_(pLevel, pRandom, pPos.below(), resourcekey);
            }
        }

        @Override
        public void postProcess(
            WorldGenLevel pLevel,
            StructureManager pStructureManager,
            ChunkGenerator pGenerator,
            RandomSource pRandom,
            BoundingBox pBox,
            ChunkPos pChunkPos,
            BlockPos pPos
        ) {
            if (this.m_320364_()) {
                super.postProcess(pLevel, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
            } else {
                int i = pLevel.getMaxBuildHeight();
                int j = 0;
                Vec3i vec3i = this.template.getSize();
                Heightmap.Types heightmap$types = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
                int k = vec3i.getX() * vec3i.getZ();
                if (k == 0) {
                    j = pLevel.getHeight(heightmap$types, this.templatePosition.getX(), this.templatePosition.getZ());
                } else {
                    BlockPos blockpos = this.templatePosition.offset(vec3i.getX() - 1, 0, vec3i.getZ() - 1);

                    for (BlockPos blockpos1 : BlockPos.betweenClosed(this.templatePosition, blockpos)) {
                        int l = pLevel.getHeight(heightmap$types, blockpos1.getX(), blockpos1.getZ());
                        j += l;
                        i = Math.min(i, l);
                    }

                    j /= k;
                }

                this.m_321530_(this.isBeached ? this.m_318620_(i, pRandom) : j);
                super.postProcess(pLevel, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
            }
        }

        public boolean m_320364_() {
            Vec3i vec3i = this.template.getSize();
            return vec3i.getX() > 32 || vec3i.getY() > 32;
        }

        public int m_318620_(int p_332021_, RandomSource p_332823_) {
            return p_332021_ - this.template.getSize().getY() / 2 - p_332823_.nextInt(3);
        }

        public void m_321530_(int p_331508_) {
            this.templatePosition = new BlockPos(this.templatePosition.getX(), p_331508_, this.templatePosition.getZ());
        }
    }
}