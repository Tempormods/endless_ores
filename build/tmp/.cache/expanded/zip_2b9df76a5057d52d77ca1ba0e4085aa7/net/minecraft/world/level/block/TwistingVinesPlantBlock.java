package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesPlantBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<TwistingVinesPlantBlock> f_303779_ = m_306223_(TwistingVinesPlantBlock::new);
    public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    @Override
    public MapCodec<TwistingVinesPlantBlock> m_304657_() {
        return f_303779_;
    }

    public TwistingVinesPlantBlock(BlockBehaviour.Properties p_154873_) {
        super(p_154873_, Direction.UP, SHAPE, false);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock)Blocks.TWISTING_VINES;
    }
}