package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFossilStructure extends Structure {
    public static final MapCodec<NetherFossilStructure> CODEC = RecordCodecBuilder.mapCodec(
        p_228585_ -> p_228585_.group(settingsCodec(p_228585_), HeightProvider.CODEC.fieldOf("height").forGetter(p_228583_ -> p_228583_.height))
                .apply(p_228585_, NetherFossilStructure::new)
    );
    public final HeightProvider height;

    public NetherFossilStructure(Structure.StructureSettings p_228573_, HeightProvider p_228574_) {
        super(p_228573_);
        this.height = p_228574_;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        WorldgenRandom worldgenrandom = pContext.random();
        int i = pContext.chunkPos().getMinBlockX() + worldgenrandom.nextInt(16);
        int j = pContext.chunkPos().getMinBlockZ() + worldgenrandom.nextInt(16);
        int k = pContext.chunkGenerator().getSeaLevel();
        WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(pContext.chunkGenerator(), pContext.heightAccessor());
        int l = this.height.sample(worldgenrandom, worldgenerationcontext);
        NoiseColumn noisecolumn = pContext.chunkGenerator().getBaseColumn(i, j, pContext.heightAccessor(), pContext.randomState());
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i, l, j);

        while (l > k) {
            BlockState blockstate = noisecolumn.getBlock(l);
            BlockState blockstate1 = noisecolumn.getBlock(--l);
            if (blockstate.isAir()
                && (
                    blockstate1.is(Blocks.SOUL_SAND)
                        || blockstate1.isFaceSturdy(EmptyBlockGetter.INSTANCE, blockpos$mutableblockpos.setY(l), Direction.UP)
                )) {
                break;
            }
        }

        if (l <= k) {
            return Optional.empty();
        } else {
            BlockPos blockpos = new BlockPos(i, l, j);
            return Optional.of(
                new Structure.GenerationStub(blockpos, p_228581_ -> NetherFossilPieces.addPieces(pContext.structureTemplateManager(), p_228581_, worldgenrandom, blockpos))
            );
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.NETHER_FOSSIL;
    }
}