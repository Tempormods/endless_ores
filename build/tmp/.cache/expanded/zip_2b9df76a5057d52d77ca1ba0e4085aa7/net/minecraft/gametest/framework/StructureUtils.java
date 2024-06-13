package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class StructureUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int f_316717_ = 10;
    public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
    public static String testStructuresDir = "gameteststructures";

    public static Rotation getRotationForRotationSteps(int pRotationSteps) {
        switch (pRotationSteps) {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE_90;
            case 2:
                return Rotation.CLOCKWISE_180;
            case 3:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + pRotationSteps);
        }
    }

    public static int getRotationStepsForRotation(Rotation pRotation) {
        switch (pRotation) {
            case NONE:
                return 0;
            case CLOCKWISE_90:
                return 1;
            case CLOCKWISE_180:
                return 2;
            case COUNTERCLOCKWISE_90:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + pRotation);
        }
    }

    public static AABB getStructureBounds(StructureBlockEntity pStructureBlockEntity) {
        return AABB.of(getStructureBoundingBox(pStructureBlockEntity));
    }

    public static BoundingBox getStructureBoundingBox(StructureBlockEntity pStructureBlockEntity) {
        BlockPos blockpos = m_304927_(pStructureBlockEntity);
        BlockPos blockpos1 = m_305507_(blockpos, pStructureBlockEntity.getStructureSize(), pStructureBlockEntity.getRotation());
        return BoundingBox.fromCorners(blockpos, blockpos1);
    }

    public static BlockPos m_304927_(StructureBlockEntity p_311311_) {
        return p_311311_.getBlockPos().offset(p_311311_.getStructurePos());
    }

    public static void addCommandBlockAndButtonToStartTest(BlockPos pStructureBlockPos, BlockPos pOffset, Rotation pRotation, ServerLevel pServerLevel) {
        BlockPos blockpos = StructureTemplate.transform(pStructureBlockPos.offset(pOffset), Mirror.NONE, pRotation, pStructureBlockPos);
        pServerLevel.setBlockAndUpdate(blockpos, Blocks.COMMAND_BLOCK.defaultBlockState());
        CommandBlockEntity commandblockentity = (CommandBlockEntity)pServerLevel.getBlockEntity(blockpos);
        commandblockentity.getCommandBlock().setCommand("test runclosest");
        BlockPos blockpos1 = StructureTemplate.transform(blockpos.offset(0, 0, -1), Mirror.NONE, pRotation, blockpos);
        pServerLevel.setBlockAndUpdate(blockpos1, Blocks.STONE_BUTTON.defaultBlockState().rotate(pRotation));
    }

    public static void createNewEmptyStructureBlock(String pStructureName, BlockPos pPos, Vec3i pSize, Rotation pRotation, ServerLevel pServerLevel) {
        BoundingBox boundingbox = getStructureBoundingBox(pPos.above(), pSize, pRotation);
        clearSpaceForStructure(boundingbox, pServerLevel);
        pServerLevel.setBlockAndUpdate(pPos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureblockentity = (StructureBlockEntity)pServerLevel.getBlockEntity(pPos);
        structureblockentity.setIgnoreEntities(false);
        structureblockentity.setStructureName(new ResourceLocation(pStructureName));
        structureblockentity.setStructureSize(pSize);
        structureblockentity.setMode(StructureMode.SAVE);
        structureblockentity.setShowBoundingBox(true);
    }

    public static StructureBlockEntity m_305479_(GameTestInfo p_311701_, BlockPos p_311042_, Rotation p_310584_, ServerLevel p_312330_) {
        Vec3i vec3i = p_312330_.getStructureManager()
            .get(new ResourceLocation(p_311701_.getStructureName()))
            .orElseThrow(() -> new IllegalStateException("Missing test structure: " + p_311701_.getStructureName()))
            .getSize();
        BoundingBox boundingbox = getStructureBoundingBox(p_311042_, vec3i, p_310584_);
        BlockPos blockpos;
        if (p_310584_ == Rotation.NONE) {
            blockpos = p_311042_;
        } else if (p_310584_ == Rotation.CLOCKWISE_90) {
            blockpos = p_311042_.offset(vec3i.getZ() - 1, 0, 0);
        } else if (p_310584_ == Rotation.CLOCKWISE_180) {
            blockpos = p_311042_.offset(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
        } else {
            if (p_310584_ != Rotation.COUNTERCLOCKWISE_90) {
                throw new IllegalArgumentException("Invalid rotation: " + p_310584_);
            }

            blockpos = p_311042_.offset(0, 0, vec3i.getX() - 1);
        }

        forceLoadChunks(boundingbox, p_312330_);
        clearSpaceForStructure(boundingbox, p_312330_);
        return createStructureBlock(p_311701_, blockpos.below(), p_310584_, p_312330_);
    }

    public static void m_320357_(AABB p_330422_, ServerLevel p_331249_, boolean p_328180_) {
        BlockPos blockpos = BlockPos.containing(p_330422_.minX, p_330422_.minY, p_330422_.minZ).offset(-1, 0, -1);
        BlockPos blockpos1 = BlockPos.containing(p_330422_.maxX, p_330422_.maxY, p_330422_.maxZ);
        BlockPos.betweenClosedStream(blockpos, blockpos1)
            .forEach(
                p_325964_ -> {
                    boolean flag = p_325964_.getX() == blockpos.getX()
                        || p_325964_.getX() == blockpos1.getX()
                        || p_325964_.getZ() == blockpos.getZ()
                        || p_325964_.getZ() == blockpos1.getZ();
                    boolean flag1 = p_325964_.getY() == blockpos1.getY();
                    if (flag || flag1 && p_328180_) {
                        p_331249_.setBlockAndUpdate(p_325964_, Blocks.BARRIER.defaultBlockState());
                    }
                }
            );
    }

    public static void m_319541_(AABB p_336061_, ServerLevel p_334551_) {
        BlockPos blockpos = BlockPos.containing(p_336061_.minX, p_336061_.minY, p_336061_.minZ).offset(-1, 0, -1);
        BlockPos blockpos1 = BlockPos.containing(p_336061_.maxX, p_336061_.maxY, p_336061_.maxZ);
        BlockPos.betweenClosedStream(blockpos, blockpos1)
            .forEach(
                p_325970_ -> {
                    boolean flag = p_325970_.getX() == blockpos.getX()
                        || p_325970_.getX() == blockpos1.getX()
                        || p_325970_.getZ() == blockpos.getZ()
                        || p_325970_.getZ() == blockpos1.getZ();
                    boolean flag1 = p_325970_.getY() == blockpos1.getY();
                    if (p_334551_.getBlockState(p_325970_).is(Blocks.BARRIER) && (flag || flag1)) {
                        p_334551_.setBlockAndUpdate(p_325970_, Blocks.AIR.defaultBlockState());
                    }
                }
            );
    }

    private static void forceLoadChunks(BoundingBox p_312219_, ServerLevel pServerLevel) {
        p_312219_.m_306717_().forEach(p_308541_ -> pServerLevel.setChunkForced(p_308541_.x, p_308541_.z, true));
    }

    public static void clearSpaceForStructure(BoundingBox pBoundingBox, ServerLevel pServerLevel) {
        int i = pBoundingBox.minY() - 1;
        BoundingBox boundingbox = new BoundingBox(
            pBoundingBox.minX() - 2,
            pBoundingBox.minY() - 3,
            pBoundingBox.minZ() - 3,
            pBoundingBox.maxX() + 3,
            pBoundingBox.maxY() + 20,
            pBoundingBox.maxZ() + 3
        );
        BlockPos.betweenClosedStream(boundingbox).forEach(p_177748_ -> clearBlock(i, p_177748_, pServerLevel));
        pServerLevel.getBlockTicks().clearArea(boundingbox);
        pServerLevel.clearBlockEvents(boundingbox);
        AABB aabb = new AABB(
            (double)boundingbox.minX(),
            (double)boundingbox.minY(),
            (double)boundingbox.minZ(),
            (double)boundingbox.maxX(),
            (double)boundingbox.maxY(),
            (double)boundingbox.maxZ()
        );
        List<Entity> list = pServerLevel.getEntitiesOfClass(Entity.class, aabb, p_177750_ -> !(p_177750_ instanceof Player));
        list.forEach(Entity::discard);
    }

    public static BlockPos m_305507_(BlockPos p_310098_, Vec3i p_312132_, Rotation p_309587_) {
        BlockPos blockpos = p_310098_.offset(p_312132_).offset(-1, -1, -1);
        return StructureTemplate.transform(blockpos, Mirror.NONE, p_309587_, p_310098_);
    }

    public static BoundingBox getStructureBoundingBox(BlockPos pPos, Vec3i pOffset, Rotation pRotation) {
        BlockPos blockpos = m_305507_(pPos, pOffset, pRotation);
        BoundingBox boundingbox = BoundingBox.fromCorners(pPos, blockpos);
        int i = Math.min(boundingbox.minX(), boundingbox.maxX());
        int j = Math.min(boundingbox.minZ(), boundingbox.maxZ());
        return boundingbox.move(pPos.getX() - i, 0, pPos.getZ() - j);
    }

    public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos pPos, int pRadius, ServerLevel pServerLevel) {
        return findStructureBlocks(pPos, pRadius, pServerLevel).filter(p_177756_ -> doesStructureContain(p_177756_, pPos, pServerLevel)).findFirst();
    }

    public static Optional<BlockPos> findNearestStructureBlock(BlockPos pPos, int pRadius, ServerLevel pServerLevel) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(p_177759_ -> p_177759_.distManhattan(pPos));
        return findStructureBlocks(pPos, pRadius, pServerLevel).min(comparator);
    }

    public static Stream<BlockPos> m_322152_(BlockPos p_333272_, int p_332388_, ServerLevel p_333747_, String p_332891_) {
        return findStructureBlocks(p_333272_, p_332388_, p_333747_)
            .map(p_325972_ -> (StructureBlockEntity)p_333747_.getBlockEntity(p_325972_))
            .filter(Objects::nonNull)
            .filter(p_325954_ -> Objects.equals(p_325954_.getStructureName(), p_332891_))
            .map(BlockEntity::getBlockPos)
            .map(BlockPos::immutable);
    }

    public static Stream<BlockPos> findStructureBlocks(BlockPos pPos, int pRadius, ServerLevel pServerLevel) {
        BoundingBox boundingbox = m_319242_(pPos, pRadius, pServerLevel);
        return BlockPos.betweenClosedStream(boundingbox).filter(p_325959_ -> pServerLevel.getBlockState(p_325959_).is(Blocks.STRUCTURE_BLOCK)).map(BlockPos::immutable);
    }

    private static StructureBlockEntity createStructureBlock(GameTestInfo p_309598_, BlockPos pPos, Rotation pRotation, ServerLevel pServerLevel) {
        pServerLevel.setBlockAndUpdate(pPos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureblockentity = (StructureBlockEntity)pServerLevel.getBlockEntity(pPos);
        structureblockentity.setMode(StructureMode.LOAD);
        structureblockentity.setRotation(pRotation);
        structureblockentity.setIgnoreEntities(false);
        structureblockentity.setStructureName(new ResourceLocation(p_309598_.getStructureName()));
        structureblockentity.setMetaData(p_309598_.getTestName());
        if (!structureblockentity.m_305135_(pServerLevel)) {
            throw new RuntimeException("Failed to load structure info for test: " + p_309598_.getTestName() + ". Structure name: " + p_309598_.getStructureName());
        } else {
            return structureblockentity;
        }
    }

    private static BoundingBox m_319242_(BlockPos p_329849_, int p_332427_, ServerLevel p_328726_) {
        BlockPos blockpos = BlockPos.containing(
            (double)p_329849_.getX(), (double)p_328726_.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, p_329849_).getY(), (double)p_329849_.getZ()
        );
        return new BoundingBox(blockpos).m_320482_(p_332427_, 10, p_332427_);
    }

    public static Stream<BlockPos> m_320514_(BlockPos p_333762_, Entity p_333965_, ServerLevel p_336162_) {
        int i = 200;
        Vec3 vec3 = p_333965_.getEyePosition();
        Vec3 vec31 = vec3.add(p_333965_.getLookAngle().scale(200.0));
        return findStructureBlocks(p_333762_, 200, p_336162_)
            .map(p_325966_ -> p_336162_.getBlockEntity(p_325966_, BlockEntityType.STRUCTURE_BLOCK))
            .flatMap(Optional::stream)
            .filter(p_325957_ -> getStructureBounds(p_325957_).clip(vec3, vec31).isPresent())
            .map(BlockEntity::getBlockPos)
            .sorted(Comparator.comparing(p_333762_::distSqr))
            .limit(1L);
    }

    private static void clearBlock(int pStructureBlockY, BlockPos pPos, ServerLevel pServerLevel) {
        BlockState blockstate;
        if (pPos.getY() < pStructureBlockY) {
            blockstate = Blocks.STONE.defaultBlockState();
        } else {
            blockstate = Blocks.AIR.defaultBlockState();
        }

        BlockInput blockinput = new BlockInput(blockstate, Collections.emptySet(), null);
        blockinput.place(pServerLevel, pPos, 2);
        pServerLevel.blockUpdated(pPos, blockstate.getBlock());
    }

    private static boolean doesStructureContain(BlockPos pStructureBlockPos, BlockPos pPosToTest, ServerLevel pServerLevel) {
        StructureBlockEntity structureblockentity = (StructureBlockEntity)pServerLevel.getBlockEntity(pStructureBlockPos);
        return getStructureBoundingBox(structureblockentity).isInside(pPosToTest);
    }
}