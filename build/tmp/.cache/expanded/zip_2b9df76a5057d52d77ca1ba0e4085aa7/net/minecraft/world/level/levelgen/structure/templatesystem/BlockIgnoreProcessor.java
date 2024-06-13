package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockIgnoreProcessor extends StructureProcessor {
    public static final MapCodec<BlockIgnoreProcessor> CODEC = BlockState.CODEC
        .xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState)
        .listOf()
        .fieldOf("blocks")
        .xmap(BlockIgnoreProcessor::new, p_74062_ -> p_74062_.toIgnore);
    public static final BlockIgnoreProcessor STRUCTURE_BLOCK = new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
    public static final BlockIgnoreProcessor AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR));
    public static final BlockIgnoreProcessor STRUCTURE_AND_AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
    private final ImmutableList<Block> toIgnore;

    public BlockIgnoreProcessor(List<Block> p_74052_) {
        this.toIgnore = ImmutableList.copyOf(p_74052_);
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
        LevelReader pLevel,
        BlockPos pOffset,
        BlockPos pPos,
        StructureTemplate.StructureBlockInfo pBlockInfo,
        StructureTemplate.StructureBlockInfo pRelativeBlockInfo,
        StructurePlaceSettings pSettings
    ) {
        return this.toIgnore.contains(pRelativeBlockInfo.state().getBlock()) ? null : pRelativeBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
}