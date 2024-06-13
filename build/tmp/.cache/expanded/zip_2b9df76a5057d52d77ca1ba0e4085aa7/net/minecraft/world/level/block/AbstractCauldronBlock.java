package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCauldronBlock extends Block {
    private static final int SIDE_THICKNESS = 2;
    private static final int LEG_WIDTH = 4;
    private static final int LEG_HEIGHT = 3;
    private static final int LEG_DEPTH = 2;
    protected static final int FLOOR_LEVEL = 4;
    private static final VoxelShape INSIDE = box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHAPE = Shapes.join(
        Shapes.block(),
        Shapes.or(box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE),
        BooleanOp.ONLY_FIRST
    );
    protected final CauldronInteraction.InteractionMap interactions;

    @Override
    protected abstract MapCodec<? extends AbstractCauldronBlock> m_304657_();

    public AbstractCauldronBlock(BlockBehaviour.Properties pProperties, CauldronInteraction.InteractionMap p_312076_) {
        super(pProperties);
        this.interactions = p_312076_;
    }

    protected double getContentHeight(BlockState pState) {
        return 0.0;
    }

    protected boolean isEntityInsideContent(BlockState pState, BlockPos pPos, Entity pEntity) {
        return pEntity.getY() < (double)pPos.getY() + this.getContentHeight(pState)
            && pEntity.getBoundingBox().maxY > (double)pPos.getY() + 0.25;
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_332320_, BlockState p_328545_, Level p_336157_, BlockPos p_336208_, Player p_329973_, InteractionHand p_331424_, BlockHitResult p_333720_
    ) {
        CauldronInteraction cauldroninteraction = this.interactions.f_303346_().get(p_332320_.getItem());
        return cauldroninteraction.interact(p_328545_, p_336157_, p_336208_, p_329973_, p_331424_, p_332320_);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return INSIDE;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }

    public abstract boolean isFull(BlockState pState);

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockPos blockpos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(pLevel, pPos);
        if (blockpos != null) {
            Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(pLevel, blockpos);
            if (fluid != Fluids.EMPTY && this.canReceiveStalactiteDrip(fluid)) {
                this.receiveStalactiteDrip(pState, pLevel, pPos, fluid);
            }
        }
    }

    protected boolean canReceiveStalactiteDrip(Fluid pFluid) {
        return false;
    }

    protected void receiveStalactiteDrip(BlockState pState, Level pLevel, BlockPos pPos, Fluid pFluid) {
    }
}