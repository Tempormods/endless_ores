package net.minecraft.world.level.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<StemBlock> f_302223_ = RecordCodecBuilder.mapCodec(
        p_311216_ -> p_311216_.group(
                    ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(p_312514_ -> p_312514_.fruit),
                    ResourceKey.codec(Registries.BLOCK).fieldOf("attached_stem").forGetter(p_309847_ -> p_309847_.f_302609_),
                    ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(p_311480_ -> p_311480_.f_302863_),
                    m_305607_()
                )
                .apply(p_311216_, StemBlock::new)
    );
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
        Block.box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
        Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
    };
    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> f_302609_;
    private final ResourceKey<Item> f_302863_;

    @Override
    public MapCodec<StemBlock> m_304657_() {
        return f_302223_;
    }

    public StemBlock(ResourceKey<Block> p_310213_, ResourceKey<Block> p_312966_, ResourceKey<Item> p_312034_, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.fruit = p_310213_;
        this.f_302609_ = p_312966_;
        this.f_302863_ = p_312034_;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AGE[pState.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(Blocks.FARMLAND);
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pLevel.getRawBrightness(pPos, 0) >= 9) {
            float f = CropBlock.getGrowthSpeed(this, pLevel, pPos);
            var vanilla = pRandom.nextInt((int)(25.0F / f) + 1) == 0;
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(pLevel, pPos, pState, vanilla)) {
                int i = pState.getValue(AGE);
                if (i < 7) {
                    pState = pState.setValue(AGE, Integer.valueOf(i + 1));
                    pLevel.setBlock(pPos, pState, 2);
                } else {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
                    BlockPos blockpos = pPos.relative(direction);
                    BlockState blockstate = pLevel.getBlockState(blockpos.below());
                    if (pLevel.getBlockState(blockpos).isAir() && (blockstate.is(Blocks.FARMLAND) || blockstate.is(BlockTags.DIRT))) {
                        Registry<Block> registry = pLevel.registryAccess().registryOrThrow(Registries.BLOCK);
                        Optional<Block> optional = registry.getOptional(this.fruit);
                        Optional<Block> optional1 = registry.getOptional(this.f_302609_);
                        if (optional.isPresent() && optional1.isPresent()) {
                            pLevel.setBlockAndUpdate(blockpos, optional.get().defaultBlockState());
                            pLevel.setBlockAndUpdate(pPos, optional1.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
                        }
                    }
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader p_312829_, BlockPos pPos, BlockState pState) {
        return new ItemStack(DataFixUtils.orElse(p_312829_.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.f_302863_), this));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return pState.getValue(AGE) != 7;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        int i = Math.min(7, pState.getValue(AGE) + Mth.nextInt(pLevel.random, 2, 5));
        BlockState blockstate = pState.setValue(AGE, Integer.valueOf(i));
        pLevel.setBlock(pPos, blockstate, 2);
        if (i == 7) {
            blockstate.randomTick(pLevel, pPos, pLevel.random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    @Override
    public net.minecraftforge.common.PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return net.minecraftforge.common.PlantType.CROP;
    }
}
