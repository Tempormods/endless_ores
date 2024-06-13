package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseFireBlock extends Block {
    private static final int SECONDS_ON_FIRE = 8;
    private final float fireDamage;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public BaseFireBlock(BlockBehaviour.Properties pProperties, float pFireDamage) {
        super(pProperties);
        this.fireDamage = pFireDamage;
    }

    @Override
    protected abstract MapCodec<? extends BaseFireBlock> m_304657_();

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getState(pContext.getLevel(), pContext.getClickedPos());
    }

    public static BlockState getState(BlockGetter pReader, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pReader.getBlockState(blockpos);
        return SoulFireBlock.canSurviveOnBlock(blockstate) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(pReader, pPos);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DOWN_AABB;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(24) == 0) {
            pLevel.playLocalSound(
                (double)pPos.getX() + 0.5,
                (double)pPos.getY() + 0.5,
                (double)pPos.getZ() + 0.5,
                SoundEvents.FIRE_AMBIENT,
                SoundSource.BLOCKS,
                1.0F + pRandom.nextFloat(),
                pRandom.nextFloat() * 0.7F + 0.3F,
                false
            );
        }

        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (!this.canBurn(blockstate) && !blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP)) {
            if (this.canBurn(pLevel.getBlockState(pPos.west()))) {
                for (int j = 0; j < 2; j++) {
                    double d3 = (double)pPos.getX() + pRandom.nextDouble() * 0.1F;
                    double d8 = (double)pPos.getY() + pRandom.nextDouble();
                    double d13 = (double)pPos.getZ() + pRandom.nextDouble();
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(pLevel.getBlockState(pPos.east()))) {
                for (int k = 0; k < 2; k++) {
                    double d4 = (double)(pPos.getX() + 1) - pRandom.nextDouble() * 0.1F;
                    double d9 = (double)pPos.getY() + pRandom.nextDouble();
                    double d14 = (double)pPos.getZ() + pRandom.nextDouble();
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(pLevel.getBlockState(pPos.north()))) {
                for (int l = 0; l < 2; l++) {
                    double d5 = (double)pPos.getX() + pRandom.nextDouble();
                    double d10 = (double)pPos.getY() + pRandom.nextDouble();
                    double d15 = (double)pPos.getZ() + pRandom.nextDouble() * 0.1F;
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(pLevel.getBlockState(pPos.south()))) {
                for (int i1 = 0; i1 < 2; i1++) {
                    double d6 = (double)pPos.getX() + pRandom.nextDouble();
                    double d11 = (double)pPos.getY() + pRandom.nextDouble();
                    double d16 = (double)(pPos.getZ() + 1) - pRandom.nextDouble() * 0.1F;
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(pLevel.getBlockState(pPos.above()))) {
                for (int j1 = 0; j1 < 2; j1++) {
                    double d7 = (double)pPos.getX() + pRandom.nextDouble();
                    double d12 = (double)(pPos.getY() + 1) - pRandom.nextDouble() * 0.1F;
                    double d17 = (double)pPos.getZ() + pRandom.nextDouble();
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                double d0 = (double)pPos.getX() + pRandom.nextDouble();
                double d1 = (double)pPos.getY() + pRandom.nextDouble() * 0.5 + 0.5;
                double d2 = (double)pPos.getZ() + pRandom.nextDouble();
                pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean canBurn(BlockState pState);

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pEntity.fireImmune()) {
            pEntity.setRemainingFireTicks(pEntity.getRemainingFireTicks() + 1);
            if (pEntity.getRemainingFireTicks() == 0) {
                pEntity.m_322706_(8);
            }
        }

        pEntity.hurt(pLevel.damageSources().inFire(), this.fireDamage);
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock())) {
            if (inPortalDimension(pLevel)) {
                Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(pLevel, pPos, Direction.Axis.X);
                optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(pLevel, pPos, optional);
                if (optional.isPresent()) {
                    optional.get().createPortalBlocks();
                    return;
                }
            }

            if (!pState.canSurvive(pLevel, pPos)) {
                pLevel.removeBlock(pPos, false);
            }
        }
    }

    private static boolean inPortalDimension(Level pLevel) {
        return pLevel.dimension() == Level.OVERWORLD || pLevel.dimension() == Level.NETHER;
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide()) {
            pLevel.levelEvent(null, 1009, pPos, 0);
        }

        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public static boolean canBePlacedAt(Level pLevel, BlockPos pPos, Direction pDirection) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        return !blockstate.isAir() ? false : getState(pLevel, pPos).canSurvive(pLevel, pPos) || isPortal(pLevel, pPos, pDirection);
    }

    private static boolean isPortal(Level pLevel, BlockPos pPos, Direction pDirection) {
        if (!inPortalDimension(pLevel)) {
            return false;
        } else {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();
            boolean flag = false;

            for (Direction direction : Direction.values()) {
                if (pLevel.getBlockState(blockpos$mutableblockpos.set(pPos).move(direction)).isPortalFrame(pLevel, blockpos$mutableblockpos)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                Direction.Axis direction$axis = pDirection.getAxis().isHorizontal()
                    ? pDirection.getCounterClockWise().getAxis()
                    : Direction.Plane.HORIZONTAL.getRandomAxis(pLevel.random);
                return PortalShape.findEmptyPortalShape(pLevel, pPos, direction$axis).isPresent();
            }
        }
    }
}
