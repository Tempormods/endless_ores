package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralFanBlock extends BaseCoralPlantTypeBlock {
    public static final MapCodec<BaseCoralFanBlock> f_302471_ = m_306223_(BaseCoralFanBlock::new);
    private static final VoxelShape AABB = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    @Override
    public MapCodec<? extends BaseCoralFanBlock> m_304657_() {
        return f_302471_;
    }

    public BaseCoralFanBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABB;
    }
}