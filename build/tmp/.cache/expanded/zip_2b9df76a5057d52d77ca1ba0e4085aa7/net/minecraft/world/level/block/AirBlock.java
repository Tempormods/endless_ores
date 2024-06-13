package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirBlock extends Block {
    public static final MapCodec<AirBlock> f_302534_ = m_306223_(AirBlock::new);

    @Override
    public MapCodec<AirBlock> m_304657_() {
        return f_302534_;
    }

    public AirBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }
}