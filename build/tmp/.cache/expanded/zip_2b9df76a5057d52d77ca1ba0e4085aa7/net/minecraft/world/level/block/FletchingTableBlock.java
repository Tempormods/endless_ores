package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FletchingTableBlock extends CraftingTableBlock {
    public static final MapCodec<FletchingTableBlock> f_303095_ = m_306223_(FletchingTableBlock::new);

    @Override
    public MapCodec<FletchingTableBlock> m_304657_() {
        return f_303095_;
    }

    public FletchingTableBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }
}