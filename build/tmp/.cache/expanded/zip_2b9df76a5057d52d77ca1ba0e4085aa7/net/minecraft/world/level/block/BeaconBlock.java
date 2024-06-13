package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
    public static final MapCodec<BeaconBlock> f_303640_ = m_306223_(BeaconBlock::new);

    @Override
    public MapCodec<BeaconBlock> m_304657_() {
        return f_303640_;
    }

    public BeaconBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BeaconBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityType.BEACON, BeaconBlockEntity::tick);
    }

    @Override
    protected InteractionResult use(BlockState p_335352_, Level p_329169_, BlockPos p_333104_, Player p_330505_, BlockHitResult p_335231_) {
        if (p_329169_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (p_329169_.getBlockEntity(p_333104_) instanceof BeaconBlockEntity beaconblockentity) {
                p_330505_.openMenu(beaconblockentity);
                p_330505_.awardStat(Stats.INTERACT_WITH_BEACON);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}