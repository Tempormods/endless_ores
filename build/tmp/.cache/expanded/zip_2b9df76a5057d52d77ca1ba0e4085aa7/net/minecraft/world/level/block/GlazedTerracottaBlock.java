package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<GlazedTerracottaBlock> f_302246_ = m_306223_(GlazedTerracottaBlock::new);

    @Override
    public MapCodec<GlazedTerracottaBlock> m_304657_() {
        return f_302246_;
    }

    public GlazedTerracottaBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}