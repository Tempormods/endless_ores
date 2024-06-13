package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock extends BaseEntityBlock {
    public static final MapCodec<BeehiveBlock> f_303500_ = m_306223_(BeehiveBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
    public static final int MAX_HONEY_LEVELS = 5;
    private static final int SHEARED_HONEYCOMB_COUNT = 3;

    @Override
    public MapCodec<BeehiveBlock> m_304657_() {
        return f_303500_;
    }

    public BeehiveBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, Integer.valueOf(0)).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return pBlockState.getValue(HONEY_LEVEL);
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
        if (!pLevel.isClientSide && pTe instanceof BeehiveBlockEntity beehiveblockentity) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, pStack) == 0) {
                beehiveblockentity.emptyAllLivingFromHive(pPlayer, pState, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
                this.angerNearbyBees(pLevel, pPos);
            }

            CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)pPlayer, pState, pStack, beehiveblockentity.getOccupantCount());
        }
    }

    private void angerNearbyBees(Level pLevel, BlockPos pPos) {
        AABB aabb = new AABB(pPos).inflate(8.0, 6.0, 8.0);
        List<Bee> list = pLevel.getEntitiesOfClass(Bee.class, aabb);
        if (!list.isEmpty()) {
            List<Player> list1 = pLevel.getEntitiesOfClass(Player.class, aabb);
            if (list1.isEmpty()) {
                return;
            }

            for (Bee bee : list) {
                if (bee.getTarget() == null) {
                    Player player = Util.getRandom(list1, pLevel.random);
                    bee.setTarget(player);
                }
            }
        }
    }

    public static void dropHoneycomb(Level pLevel, BlockPos pPos) {
        popResource(pLevel, pPos, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_331014_, BlockState p_328141_, Level p_329187_, BlockPos p_335985_, Player p_336201_, InteractionHand p_333071_, BlockHitResult p_331246_
    ) {
        int i = p_328141_.getValue(HONEY_LEVEL);
        boolean flag = false;
        if (i >= 5) {
            Item item = p_331014_.getItem();
            if (p_331014_.canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_HARVEST)) {
                p_329187_.playSound(
                    p_336201_, p_336201_.getX(), p_336201_.getY(), p_336201_.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F
                );
                dropHoneycomb(p_329187_, p_335985_);
                p_331014_.hurtAndBreak(1, p_336201_, LivingEntity.m_322775_(p_333071_));
                flag = true;
                p_329187_.gameEvent(p_336201_, GameEvent.SHEAR, p_335985_);
            } else if (p_331014_.is(Items.GLASS_BOTTLE)) {
                p_331014_.shrink(1);
                p_329187_.playSound(
                    p_336201_, p_336201_.getX(), p_336201_.getY(), p_336201_.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F
                );
                if (p_331014_.isEmpty()) {
                    p_336201_.setItemInHand(p_333071_, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!p_336201_.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
                    p_336201_.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                flag = true;
                p_329187_.gameEvent(p_336201_, GameEvent.FLUID_PICKUP, p_335985_);
            }

            if (!p_329187_.isClientSide() && flag) {
                p_336201_.awardStat(Stats.ITEM_USED.get(item));
            }
        }

        if (flag) {
            if (!CampfireBlock.isSmokeyPos(p_329187_, p_335985_)) {
                if (this.hiveContainsBees(p_329187_, p_335985_)) {
                    this.angerNearbyBees(p_329187_, p_335985_);
                }

                this.releaseBeesAndResetHoneyLevel(p_329187_, p_328141_, p_335985_, p_336201_, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            } else {
                this.resetHoneyLevel(p_329187_, p_328141_, p_335985_);
            }

            return ItemInteractionResult.m_322455_(p_329187_.isClientSide);
        } else {
            return super.m_51273_(p_331014_, p_328141_, p_329187_, p_335985_, p_336201_, p_333071_, p_331246_);
        }
    }

    private boolean hiveContainsBees(Level pLevel, BlockPos pPos) {
        return pLevel.getBlockEntity(pPos) instanceof BeehiveBlockEntity beehiveblockentity ? !beehiveblockentity.isEmpty() : false;
    }

    public void releaseBeesAndResetHoneyLevel(Level pLevel, BlockState pState, BlockPos pPos, @Nullable Player pPlayer, BeehiveBlockEntity.BeeReleaseStatus pBeeReleaseStatus) {
        this.resetHoneyLevel(pLevel, pState, pPos);
        if (pLevel.getBlockEntity(pPos) instanceof BeehiveBlockEntity beehiveblockentity) {
            beehiveblockentity.emptyAllLivingFromHive(pPlayer, pState, pBeeReleaseStatus);
        }
    }

    public void resetHoneyLevel(Level pLevel, BlockState pState, BlockPos pPos) {
        pLevel.setBlock(pPos, pState.setValue(HONEY_LEVEL, Integer.valueOf(0)), 3);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(HONEY_LEVEL) >= 5) {
            for (int i = 0; i < pRandom.nextInt(1) + 1; i++) {
                this.trySpawnDripParticles(pLevel, pPos, pState);
            }
        }
    }

    private void trySpawnDripParticles(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pState.getFluidState().isEmpty() && !(pLevel.random.nextFloat() < 0.3F)) {
            VoxelShape voxelshape = pState.getCollisionShape(pLevel, pPos);
            double d0 = voxelshape.max(Direction.Axis.Y);
            if (d0 >= 1.0 && !pState.is(BlockTags.IMPERMEABLE)) {
                double d1 = voxelshape.min(Direction.Axis.Y);
                if (d1 > 0.0) {
                    this.spawnParticle(pLevel, pPos, voxelshape, (double)pPos.getY() + d1 - 0.05);
                } else {
                    BlockPos blockpos = pPos.below();
                    BlockState blockstate = pLevel.getBlockState(blockpos);
                    VoxelShape voxelshape1 = blockstate.getCollisionShape(pLevel, blockpos);
                    double d2 = voxelshape1.max(Direction.Axis.Y);
                    if ((d2 < 1.0 || !blockstate.isCollisionShapeFullBlock(pLevel, blockpos)) && blockstate.getFluidState().isEmpty()) {
                        this.spawnParticle(pLevel, pPos, voxelshape, (double)pPos.getY() - 0.05);
                    }
                }
            }
        }
    }

    private void spawnParticle(Level pLevel, BlockPos pPos, VoxelShape pShape, double pY) {
        this.spawnFluidParticle(
            pLevel,
            (double)pPos.getX() + pShape.min(Direction.Axis.X),
            (double)pPos.getX() + pShape.max(Direction.Axis.X),
            (double)pPos.getZ() + pShape.min(Direction.Axis.Z),
            (double)pPos.getZ() + pShape.max(Direction.Axis.Z),
            pY
        );
    }

    private void spawnFluidParticle(Level pParticleData, double pX1, double pX2, double pZ1, double pZ2, double pY) {
        pParticleData.addParticle(
            ParticleTypes.DRIPPING_HONEY,
            Mth.lerp(pParticleData.random.nextDouble(), pX1, pX2),
            pY,
            Mth.lerp(pParticleData.random.nextDouble(), pZ1, pZ2),
            0.0,
            0.0,
            0.0
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HONEY_LEVEL, FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BeehiveBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide
            && pPlayer.isCreative()
            && pLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
            && pLevel.getBlockEntity(pPos) instanceof BeehiveBlockEntity beehiveblockentity) {
            int i = pState.getValue(HONEY_LEVEL);
            boolean flag = !beehiveblockentity.isEmpty();
            if (flag || i > 0) {
                ItemStack itemstack = new ItemStack(this);
                itemstack.m_323474_(beehiveblockentity.m_321843_());
                itemstack.m_322496_(DataComponents.f_315479_, BlockItemStateProperties.f_314882_.m_323763_(HONEY_LEVEL, i));
                ItemEntity itementity = new ItemEntity(
                    pLevel, (double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), itemstack
                );
                itementity.setDefaultPickUpDelay();
                pLevel.addFreshEntity(itementity);
            }
        }

        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        Entity entity = pParams.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof PrimedTnt
            || entity instanceof Creeper
            || entity instanceof WitherSkull
            || entity instanceof WitherBoss
            || entity instanceof MinecartTNT) {
            BlockEntity blockentity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockentity instanceof BeehiveBlockEntity beehiveblockentity) {
                beehiveblockentity.emptyAllLivingFromHive(null, pState, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            }
        }

        return super.getDrops(pState, pParams);
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pLevel.getBlockState(pFacingPos).getBlock() instanceof FireBlock && pLevel.getBlockEntity(pCurrentPos) instanceof BeehiveBlockEntity beehiveblockentity) {
            beehiveblockentity.emptyAllLivingFromHive(null, pState, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public BlockState rotate(BlockState p_309863_, Rotation p_310613_) {
        return p_309863_.setValue(FACING, p_310613_.rotate(p_309863_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_311137_, Mirror p_310463_) {
        return p_311137_.rotate(p_310463_.getRotation(p_311137_.getValue(FACING)));
    }
}
