package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DispenserBlock extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<DispenserBlock> f_302710_ = m_306223_(DispenserBlock::new);
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final DefaultDispenseItemBehavior f_315203_ = new DefaultDispenseItemBehavior();
    public static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = Util.make(
        new Object2ObjectOpenHashMap<>(), p_327262_ -> p_327262_.defaultReturnValue(f_315203_)
    );
    private static final int TRIGGER_DURATION = 4;

    @Override
    public MapCodec<? extends DispenserBlock> m_304657_() {
        return f_302710_;
    }

    public static void registerBehavior(ItemLike pItem, DispenseItemBehavior pBehavior) {
        DISPENSER_REGISTRY.put(pItem.asItem(), pBehavior);
    }

    public static void m_320828_(ItemLike p_329878_) {
        DISPENSER_REGISTRY.put(p_329878_.asItem(), new ProjectileDispenseBehavior(p_329878_.asItem()));
    }

    public DispenserBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof DispenserBlockEntity) {
                pPlayer.openMenu((DispenserBlockEntity)blockentity);
                if (blockentity instanceof DropperBlockEntity) {
                    pPlayer.awardStat(Stats.INSPECT_DROPPER);
                } else {
                    pPlayer.awardStat(Stats.INSPECT_DISPENSER);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    protected void dispenseFrom(ServerLevel pLevel, BlockState pState, BlockPos pPos) {
        DispenserBlockEntity dispenserblockentity = pLevel.getBlockEntity(pPos, BlockEntityType.DISPENSER).orElse(null);
        if (dispenserblockentity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", pPos);
        } else {
            BlockSource blocksource = new BlockSource(pLevel, pPos, pState, dispenserblockentity);
            int i = dispenserblockentity.getRandomSlot(pLevel.random);
            if (i < 0) {
                pLevel.levelEvent(1001, pPos, 0);
                pLevel.m_322719_(GameEvent.BLOCK_ACTIVATE, pPos, GameEvent.Context.of(dispenserblockentity.getBlockState()));
            } else {
                ItemStack itemstack = dispenserblockentity.getItem(i);
                DispenseItemBehavior dispenseitembehavior = this.getDispenseMethod(pLevel, itemstack);
                if (dispenseitembehavior != DispenseItemBehavior.NOOP) {
                    dispenserblockentity.setItem(i, dispenseitembehavior.dispense(blocksource, itemstack));
                }
            }
        }
    }

    protected DispenseItemBehavior getDispenseMethod(Level p_328928_, ItemStack pStack) {
        return (DispenseItemBehavior)(!pStack.isItemEnabled(p_328928_.enabledFeatures()) ? f_315203_ : DISPENSER_REGISTRY.get(pStack.getItem()));
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos) || pLevel.hasNeighborSignal(pPos.above());
        boolean flag1 = pState.getValue(TRIGGERED);
        if (flag && !flag1) {
            pLevel.scheduleTick(pPos, this, 4);
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
        } else if (!flag && flag1) {
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.valueOf(false)), 2);
        }
    }

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.dispenseFrom(pLevel, pState, pPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DispenserBlockEntity(pPos, pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        Containers.m_307148_(pState, pNewState, pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public static Position getDispensePosition(BlockSource pBlockSource) {
        return m_321992_(pBlockSource, 0.7, Vec3.ZERO);
    }

    public static Position m_321992_(BlockSource p_330786_, double p_333084_, Vec3 p_335028_) {
        Direction direction = p_330786_.state().getValue(FACING);
        return p_330786_.center()
            .add(
                p_333084_ * (double)direction.getStepX() + p_335028_.x(),
                p_333084_ * (double)direction.getStepY() + p_335028_.y(),
                p_333084_ * (double)direction.getStepZ() + p_335028_.z()
            );
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TRIGGERED);
    }
}