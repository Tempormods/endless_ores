package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock {
    public static final MapCodec<EndPortalBlock> f_302588_ = m_306223_(EndPortalBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

    @Override
    public MapCodec<EndPortalBlock> m_304657_() {
        return f_302588_;
    }

    public EndPortalBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TheEndPortalBlockEntity(pPos, pState);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pLevel instanceof ServerLevel
            && pEntity.canChangeDimensions()
            && Shapes.joinIsNotEmpty(
                Shapes.create(pEntity.getBoundingBox().move((double)(-pPos.getX()), (double)(-pPos.getY()), (double)(-pPos.getZ()))),
                pState.getShape(pLevel, pPos),
                BooleanOp.AND
            )) {
            ResourceKey<Level> resourcekey = pLevel.dimension() == Level.END ? Level.OVERWORLD : Level.END;
            ServerLevel serverlevel = ((ServerLevel)pLevel).getServer().getLevel(resourcekey);
            if (serverlevel == null) {
                return;
            }

            pEntity.changeDimension(serverlevel);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double)pPos.getX() + pRandom.nextDouble();
        double d1 = (double)pPos.getY() + 0.8;
        double d2 = (double)pPos.getZ() + pRandom.nextDouble();
        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader p_310938_, BlockPos pPos, BlockState pState) {
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean canBeReplaced(BlockState pState, Fluid pFluid) {
        return false;
    }
}