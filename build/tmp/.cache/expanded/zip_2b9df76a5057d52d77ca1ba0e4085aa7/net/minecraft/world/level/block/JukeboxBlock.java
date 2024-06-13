package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
    public static final MapCodec<JukeboxBlock> f_303074_ = m_306223_(JukeboxBlock::new);
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

    @Override
    public MapCodec<JukeboxBlock> m_304657_() {
        return f_303074_;
    }

    public JukeboxBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        CustomData customdata = pStack.m_322304_(DataComponents.f_316520_, CustomData.f_317060_);
        if (customdata.m_323290_("RecordItem")) {
            pLevel.setBlock(pPos, pState.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
        }
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pState.getValue(HAS_RECORD) && pLevel.getBlockEntity(pPos) instanceof JukeboxBlockEntity jukeboxblockentity) {
            jukeboxblockentity.popOutRecord();
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof JukeboxBlockEntity jukeboxblockentity) {
                jukeboxblockentity.popOutRecord();
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new JukeboxBlockEntity(pPos, pState);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        if (pLevel.getBlockEntity(pPos) instanceof JukeboxBlockEntity jukeboxblockentity && jukeboxblockentity.isRecordPlaying()) {
            return 15;
        }

        return 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof JukeboxBlockEntity jukeboxblockentity
            && jukeboxblockentity.m_306082_().getItem() instanceof RecordItem recorditem) {
            return recorditem.getAnalogOutput();
        }

        return 0;
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_RECORD);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pState.getValue(HAS_RECORD) ? createTickerHelper(pBlockEntityType, BlockEntityType.JUKEBOX, JukeboxBlockEntity::playRecordTick) : null;
    }
}