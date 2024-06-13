package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class ChiseledBookShelfBlock extends BaseEntityBlock {
    public static final MapCodec<ChiseledBookShelfBlock> f_303870_ = m_306223_(ChiseledBookShelfBlock::new);
    private static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED,
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED,
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED,
        BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED
    );

    @Override
    public MapCodec<ChiseledBookShelfBlock> m_304657_() {
        return f_303870_;
    }

    public ChiseledBookShelfBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        BlockState blockstate = this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

        for (BooleanProperty booleanproperty : SLOT_OCCUPIED_PROPERTIES) {
            blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(false));
        }

        this.registerDefaultState(blockstate);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_336113_, BlockState p_329797_, Level p_331003_, BlockPos p_335104_, Player p_334454_, InteractionHand p_336011_, BlockHitResult p_329086_
    ) {
        if (p_331003_.getBlockEntity(p_335104_) instanceof ChiseledBookShelfBlockEntity chiseledbookshelfblockentity) {
            if (!p_336113_.is(ItemTags.BOOKSHELF_BOOKS)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
                OptionalInt optionalint = this.getHitSlot(p_329086_, p_329797_);
                if (optionalint.isEmpty()) {
                    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                } else if (p_329797_.getValue(SLOT_OCCUPIED_PROPERTIES.get(optionalint.getAsInt()))) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                } else {
                    addBook(p_331003_, p_335104_, p_334454_, chiseledbookshelfblockentity, p_336113_, optionalint.getAsInt());
                    return ItemInteractionResult.m_322455_(p_331003_.isClientSide);
                }
            }
        } else {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult use(BlockState p_335003_, Level p_333933_, BlockPos p_333604_, Player p_334275_, BlockHitResult p_334482_) {
        if (p_333933_.getBlockEntity(p_333604_) instanceof ChiseledBookShelfBlockEntity chiseledbookshelfblockentity) {
            OptionalInt optionalint = this.getHitSlot(p_334482_, p_335003_);
            if (optionalint.isEmpty()) {
                return InteractionResult.PASS;
            } else if (!p_335003_.getValue(SLOT_OCCUPIED_PROPERTIES.get(optionalint.getAsInt()))) {
                return InteractionResult.CONSUME;
            } else {
                removeBook(p_333933_, p_333604_, p_334275_, chiseledbookshelfblockentity, optionalint.getAsInt());
                return InteractionResult.sidedSuccess(p_333933_.isClientSide);
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private OptionalInt getHitSlot(BlockHitResult p_333742_, BlockState p_330398_) {
        return getRelativeHitCoordinatesForBlockFace(p_333742_, p_330398_.getValue(HorizontalDirectionalBlock.FACING)).map(p_327255_ -> {
            int i = p_327255_.y >= 0.5F ? 0 : 1;
            int j = getSection(p_327255_.x);
            return OptionalInt.of(j + i * 3);
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult pHitResult, Direction pFace) {
        Direction direction = pHitResult.getDirection();
        if (pFace != direction) {
            return Optional.empty();
        } else {
            BlockPos blockpos = pHitResult.getBlockPos().relative(direction);
            Vec3 vec3 = pHitResult.getLocation().subtract((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2((float)(1.0 - d0), (float)d1));
                case SOUTH -> Optional.of(new Vec2((float)d0, (float)d1));
                case WEST -> Optional.of(new Vec2((float)d2, (float)d1));
                case EAST -> Optional.of(new Vec2((float)(1.0 - d2), (float)d1));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    private static int getSection(float pX) {
        float f = 0.0625F;
        float f1 = 0.375F;
        if (pX < 0.375F) {
            return 0;
        } else {
            float f2 = 0.6875F;
            return pX < 0.6875F ? 1 : 2;
        }
    }

    private static void addBook(
        Level pLevel, BlockPos pPos, Player pPlayer, ChiseledBookShelfBlockEntity pBlockEntity, ItemStack pBookStack, int pSlot
    ) {
        if (!pLevel.isClientSide) {
            pPlayer.awardStat(Stats.ITEM_USED.get(pBookStack.getItem()));
            SoundEvent soundevent = pBookStack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
            pBlockEntity.setItem(pSlot, pBookStack.split(1));
            pLevel.playSound(null, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (pPlayer.isCreative()) {
                pBookStack.grow(1);
            }
        }
    }

    private static void removeBook(Level pLevel, BlockPos pPos, Player pPlayer, ChiseledBookShelfBlockEntity pBlockEntity, int pSlot) {
        if (!pLevel.isClientSide) {
            ItemStack itemstack = pBlockEntity.removeItem(pSlot, 1);
            SoundEvent soundevent = itemstack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
            pLevel.playSound(null, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!pPlayer.getInventory().add(itemstack)) {
                pPlayer.drop(itemstack, false);
            }

            pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChiseledBookShelfBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HorizontalDirectionalBlock.FACING);
        SLOT_OCCUPIED_PROPERTIES.forEach(p_261456_ -> pBuilder.add(p_261456_));
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof ChiseledBookShelfBlockEntity chiseledbookshelfblockentity && !chiseledbookshelfblockentity.isEmpty()) {
                for (int i = 0; i < 6; i++) {
                    ItemStack itemstack = chiseledbookshelfblockentity.getItem(i);
                    if (!itemstack.isEmpty()) {
                        Containers.dropItemStack(pLevel, (double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), itemstack);
                    }
                }

                chiseledbookshelfblockentity.clearContent();
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRotation.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        if (pLevel.isClientSide()) {
            return 0;
        } else {
            return pLevel.getBlockEntity(pPos) instanceof ChiseledBookShelfBlockEntity chiseledbookshelfblockentity
                ? chiseledbookshelfblockentity.getLastInteractedSlot() + 1
                : 0;
        }
    }
}