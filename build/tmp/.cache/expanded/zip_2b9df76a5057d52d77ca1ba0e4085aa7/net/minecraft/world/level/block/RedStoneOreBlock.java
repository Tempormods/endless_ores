package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RedStoneOreBlock extends Block {
    public static final MapCodec<RedStoneOreBlock> f_303052_ = m_306223_(RedStoneOreBlock::new);
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    @Override
    public MapCodec<RedStoneOreBlock> m_304657_() {
        return f_303052_;
    }

    public RedStoneOreBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }

    @Override
    protected void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        interact(pState, pLevel, pPos);
        super.attack(pState, pLevel, pPos, pPlayer);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pEntity.isSteppingCarefully()) {
            interact(pState, pLevel, pPos);
        }

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_331692_, BlockState p_328847_, Level p_334994_, BlockPos p_336118_, Player p_329891_, InteractionHand p_331867_, BlockHitResult p_329149_
    ) {
        if (p_334994_.isClientSide) {
            spawnParticles(p_334994_, p_336118_);
        } else {
            interact(p_328847_, p_334994_, p_336118_);
        }

        return p_331692_.getItem() instanceof BlockItem && new BlockPlaceContext(p_329891_, p_331867_, p_331692_, p_329149_).canPlace()
            ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
            : ItemInteractionResult.SUCCESS;
    }

    private static void interact(BlockState pState, Level pLevel, BlockPos pPos) {
        spawnParticles(pLevel, pPos);
        if (!pState.getValue(LIT)) {
            pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(true)), 3);
        }
    }

    @Override
    protected boolean m_51695_(BlockState pState) {
        return pState.getValue(LIT);
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(false)), 3);
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean pDropExperience) {
        super.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);
        if (false && pDropExperience && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, pStack) == 0) { // Forge: moved to getExpDrop
            int i = 1 + pLevel.random.nextInt(5);
            this.popExperience(pLevel, pPos, i);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            spawnParticles(pLevel, pPos);
        }
    }

    private static void spawnParticles(Level pLevel, BlockPos pPos) {
        double d0 = 0.5625;
        RandomSource randomsource = pLevel.random;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getStepX() : (double)randomsource.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getStepY() : (double)randomsource.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getStepZ() : (double)randomsource.nextFloat();
                pLevel.addParticle(
                    DustParticleOptions.REDSTONE,
                    (double)pPos.getX() + d1,
                    (double)pPos.getY() + d2,
                    (double)pPos.getZ() + d3,
                    0.0,
                    0.0,
                    0.0
                );
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
       return silktouch == 0 ? 1 + randomSource.nextInt(5) : 0;
    }

}
