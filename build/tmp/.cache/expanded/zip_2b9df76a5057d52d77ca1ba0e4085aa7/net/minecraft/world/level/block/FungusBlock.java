package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<FungusBlock> f_302243_ = RecordCodecBuilder.mapCodec(
        p_309284_ -> p_309284_.group(
                    ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter(p_309283_ -> p_309283_.feature),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("grows_on").forGetter(p_309285_ -> p_309285_.requiredBlock),
                    m_305607_()
                )
                .apply(p_309284_, FungusBlock::new)
    );
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
    private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4;
    private final Block requiredBlock;
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;

    @Override
    public MapCodec<FungusBlock> m_304657_() {
        return f_302243_;
    }

    public FungusBlock(ResourceKey<ConfiguredFeature<?, ?>> pFeature, Block pRequiredBlock, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.feature = pFeature;
        this.requiredBlock = pRequiredBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(BlockTags.NYLIUM)
            || pState.is(Blocks.MYCELIUM)
            || pState.is(Blocks.SOUL_SOIL)
            || super.mayPlaceOn(pState, pLevel, pPos);
    }

    private Optional<? extends Holder<ConfiguredFeature<?, ?>>> getFeature(LevelReader pLevel) {
        return pLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.feature);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        return blockstate.is(this.requiredBlock);
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return (double)pRandom.nextFloat() < 0.4;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        this.getFeature(pLevel).ifPresent(p_256352_ -> {
            var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(pLevel, pRandom, pPos, p_256352_);
            if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY)) return;
            event.getFeature().value().place(pLevel, pLevel.getChunkSource().getGenerator(), pRandom, pPos);
        });
    }
}