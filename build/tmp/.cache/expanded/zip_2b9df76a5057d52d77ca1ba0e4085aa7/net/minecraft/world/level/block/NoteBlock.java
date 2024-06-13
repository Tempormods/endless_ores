package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
    public static final MapCodec<NoteBlock> f_302849_ = m_306223_(NoteBlock::new);
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty NOTE = BlockStateProperties.NOTE;
    public static final int NOTE_VOLUME = 3;

    @Override
    public MapCodec<NoteBlock> m_304657_() {
        return f_302849_;
    }

    public NoteBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(INSTRUMENT, NoteBlockInstrument.HARP)
                .setValue(NOTE, Integer.valueOf(0))
                .setValue(POWERED, Boolean.valueOf(false))
        );
    }

    private BlockState setInstrument(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        NoteBlockInstrument noteblockinstrument = pLevel.getBlockState(pPos.above()).instrument();
        if (noteblockinstrument.worksAboveNoteBlock()) {
            return pState.setValue(INSTRUMENT, noteblockinstrument);
        } else {
            NoteBlockInstrument noteblockinstrument1 = pLevel.getBlockState(pPos.below()).instrument();
            NoteBlockInstrument noteblockinstrument2 = noteblockinstrument1.worksAboveNoteBlock() ? NoteBlockInstrument.HARP : noteblockinstrument1;
            return pState.setValue(INSTRUMENT, noteblockinstrument2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.setInstrument(pContext.getLevel(), pContext.getClickedPos(), this.defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        boolean flag = pFacing.getAxis() == Direction.Axis.Y;
        return flag ? this.setInstrument(pLevel, pCurrentPos, pState) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos);
        if (flag != pState.getValue(POWERED)) {
            if (flag) {
                this.playNote(null, pState, pLevel, pPos);
            }

            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    private void playNote(@Nullable Entity pEntity, BlockState pState, Level pLevel, BlockPos pPos) {
        if (pState.getValue(INSTRUMENT).worksAboveNoteBlock() || pLevel.getBlockState(pPos.above()).isAir()) {
            pLevel.blockEvent(pPos, this, 0, 0);
            pLevel.gameEvent(pEntity, GameEvent.NOTE_BLOCK_PLAY, pPos);
        }
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_330444_, BlockState p_329477_, Level p_331069_, BlockPos p_335878_, Player p_329474_, InteractionHand p_328196_, BlockHitResult p_334403_
    ) {
        return p_330444_.is(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS) && p_334403_.getDirection() == Direction.UP
            ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
            : super.m_51273_(p_330444_, p_329477_, p_331069_, p_335878_, p_329474_, p_328196_, p_334403_);
    }

    @Override
    protected InteractionResult use(BlockState p_331116_, Level p_332131_, BlockPos p_333586_, Player p_329332_, BlockHitResult p_331978_) {
        if (p_332131_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            int _new = net.minecraftforge.common.ForgeHooks.onNoteChange(p_332131_, p_333586_, p_331116_, p_331116_.getValue(NOTE), p_331116_.cycle(NOTE).getValue(NOTE));
            if (_new == -1) return InteractionResult.FAIL;
            p_331116_ = p_331116_.setValue(NOTE, _new);
            p_332131_.setBlock(p_333586_, p_331116_, 3);
            this.playNote(p_329332_, p_331116_, p_332131_, p_333586_);
            p_329332_.awardStat(Stats.TUNE_NOTEBLOCK);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (!pLevel.isClientSide) {
            this.playNote(pPlayer, pState, pLevel, pPos);
            pPlayer.awardStat(Stats.PLAY_NOTEBLOCK);
        }
    }

    public static float getPitchFromNote(int pNote) {
        return (float)Math.pow(2.0, (double)(pNote - 12) / 12.0);
    }

    @Override
    protected boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        var event = net.minecraftforge.event.ForgeEventFactory.onNotePlay(pLevel, pPos, pState, pState.getValue(NOTE), pState.getValue(INSTRUMENT));
        if (event.isCanceled()) return false;
        pState = pState.setValue(NOTE, event.getVanillaNoteId()).setValue(INSTRUMENT, event.getInstrument());
        NoteBlockInstrument noteblockinstrument = pState.getValue(INSTRUMENT);
        float f;
        if (noteblockinstrument.isTunable()) {
            int i = pState.getValue(NOTE);
            f = getPitchFromNote(i);
            pLevel.addParticle(
                ParticleTypes.NOTE,
                (double)pPos.getX() + 0.5,
                (double)pPos.getY() + 1.2,
                (double)pPos.getZ() + 0.5,
                (double)i / 24.0,
                0.0,
                0.0
            );
        } else {
            f = 1.0F;
        }

        Holder<SoundEvent> holder;
        if (noteblockinstrument.hasCustomSound()) {
            ResourceLocation resourcelocation = this.getCustomSoundId(pLevel, pPos);
            if (resourcelocation == null) {
                return false;
            }

            holder = Holder.direct(SoundEvent.createVariableRangeEvent(resourcelocation));
        } else {
            holder = noteblockinstrument.getSoundEvent();
        }

        pLevel.playSeededSound(
            null,
            (double)pPos.getX() + 0.5,
            (double)pPos.getY() + 0.5,
            (double)pPos.getZ() + 0.5,
            holder,
            SoundSource.RECORDS,
            3.0F,
            f,
            pLevel.random.nextLong()
        );
        return true;
    }

    @Nullable
    private ResourceLocation getCustomSoundId(Level pLevel, BlockPos pPos) {
        return pLevel.getBlockEntity(pPos.above()) instanceof SkullBlockEntity skullblockentity ? skullblockentity.getNoteBlockSound() : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(INSTRUMENT, POWERED, NOTE);
    }
}
