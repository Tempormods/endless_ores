package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock extends Block {
    public static final MapCodec<TntBlock> f_303576_ = m_306223_(TntBlock::new);
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    @Override
    public MapCodec<TntBlock> m_304657_() {
        return f_303576_;
    }

    public TntBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.valueOf(false)));
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock())) {
            if (pLevel.hasNeighborSignal(pPos)) {
                onCaughtFire(pState, pLevel, pPos, null, null);
                pLevel.removeBlock(pPos, false);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.hasNeighborSignal(pPos)) {
            onCaughtFire(pState, pLevel, pPos, null, null);
            pLevel.removeBlock(pPos, false);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide() && !pPlayer.isCreative() && pState.getValue(UNSTABLE)) {
            onCaughtFire(pState, pLevel, pPos, null, null);
        }

        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        if (!pLevel.isClientSide) {
            PrimedTnt primedtnt = new PrimedTnt(
                pLevel, (double)pPos.getX() + 0.5, (double)pPos.getY(), (double)pPos.getZ() + 0.5, pExplosion.getIndirectSourceEntity()
            );
            int i = primedtnt.getFuse();
            primedtnt.setFuse((short)(pLevel.random.nextInt(i / 4) + i / 8));
            pLevel.addFreshEntity(primedtnt);
        }
    }

    @Deprecated //Forge: Prefer using IForgeBlock#catchFire
    public static void explode(Level pLevel, BlockPos pPos) {
        explode(pLevel, pPos, null);
    }

    @Deprecated //Forge: Prefer using IForgeBlock#catchFire
    private static void explode(Level pLevel, BlockPos pPos, @Nullable LivingEntity pEntity) {
        if (!pLevel.isClientSide) {
            PrimedTnt primedtnt = new PrimedTnt(
                pLevel, (double)pPos.getX() + 0.5, (double)pPos.getY(), (double)pPos.getZ() + 0.5, pEntity
            );
            pLevel.addFreshEntity(primedtnt);
            pLevel.playSound(null, primedtnt.getX(), primedtnt.getY(), primedtnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.gameEvent(pEntity, GameEvent.PRIME_FUSE, pPos);
        }
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_329029_, BlockState p_331413_, Level p_329038_, BlockPos p_333168_, Player p_335932_, InteractionHand p_331663_, BlockHitResult p_330020_
    ) {
        if (!p_329029_.is(Items.FLINT_AND_STEEL) && !p_329029_.is(Items.FIRE_CHARGE)) {
            return super.m_51273_(p_329029_, p_331413_, p_329038_, p_333168_, p_335932_, p_331663_, p_330020_);
        } else {
            onCaughtFire(p_331413_, p_329038_, p_333168_, p_330020_.getDirection(), p_335932_);
            p_329038_.setBlock(p_333168_, Blocks.AIR.defaultBlockState(), 11);
            Item item = p_329029_.getItem();
            if (!p_335932_.isCreative()) {
                if (p_329029_.is(Items.FLINT_AND_STEEL)) {
                    p_329029_.hurtAndBreak(1, p_335932_, LivingEntity.m_322775_(p_331663_));
                } else {
                    p_329029_.shrink(1);
                }
            }

            p_335932_.awardStat(Stats.ITEM_USED.get(item));
            return ItemInteractionResult.m_322455_(p_329038_.isClientSide);
        }
    }

    @Override
    protected void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        if (!pLevel.isClientSide) {
            BlockPos blockpos = pHit.getBlockPos();
            Entity entity = pProjectile.getOwner();
            if (pProjectile.isOnFire() && pProjectile.mayInteract(pLevel, blockpos)) {
                onCaughtFire(pState, pLevel, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                pLevel.removeBlock(blockpos, false);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion pExplosion) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UNSTABLE);
    }

    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable net.minecraft.core.Direction face, @Nullable LivingEntity igniter) {
        explode(world, pos, igniter);
    }
}
