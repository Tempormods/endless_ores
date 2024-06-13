package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class LayeredCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<LayeredCauldronBlock> f_303033_ = RecordCodecBuilder.mapCodec(
        p_309287_ -> p_309287_.group(
                    Biome.Precipitation.f_303531_.fieldOf("precipitation").forGetter(p_309289_ -> p_309289_.f_302573_),
                    CauldronInteraction.f_303824_.fieldOf("interactions").forGetter(p_309288_ -> p_309288_.interactions),
                    m_305607_()
                )
                .apply(p_309287_, LayeredCauldronBlock::new)
    );
    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    private static final int BASE_CONTENT_HEIGHT = 6;
    private static final double HEIGHT_PER_LEVEL = 3.0;
    private final Biome.Precipitation f_302573_;

    @Override
    public MapCodec<LayeredCauldronBlock> m_304657_() {
        return f_303033_;
    }

    public LayeredCauldronBlock(Biome.Precipitation p_310517_, CauldronInteraction.InteractionMap p_313151_, BlockBehaviour.Properties pProperties) {
        super(pProperties, p_313151_);
        this.f_302573_ = p_310517_;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(1)));
    }

    @Override
    public boolean isFull(BlockState pState) {
        return pState.getValue(LEVEL) == 3;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid pFluid) {
        return pFluid == Fluids.WATER && this.f_302573_ == Biome.Precipitation.RAIN;
    }

    @Override
    protected double getContentHeight(BlockState pState) {
        return (6.0 + (double)pState.getValue(LEVEL).intValue() * 3.0) / 16.0;
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide && pEntity.isOnFire() && this.isEntityInsideContent(pState, pPos, pEntity)) {
            pEntity.clearFire();
            if (pEntity.mayInteract(pLevel, pPos)) {
                this.handleEntityOnFireInside(pState, pLevel, pPos);
            }
        }
    }

    private void handleEntityOnFireInside(BlockState pState, Level pLevel, BlockPos pPos) {
        if (this.f_302573_ == Biome.Precipitation.SNOW) {
            lowerFillLevel(Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, pState.getValue(LEVEL)), pLevel, pPos);
        } else {
            lowerFillLevel(pState, pLevel, pPos);
        }
    }

    public static void lowerFillLevel(BlockState pState, Level pLevel, BlockPos pPos) {
        int i = pState.getValue(LEVEL) - 1;
        BlockState blockstate = i == 0 ? Blocks.CAULDRON.defaultBlockState() : pState.setValue(LEVEL, Integer.valueOf(i));
        pLevel.setBlockAndUpdate(pPos, blockstate);
        pLevel.m_322719_(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(blockstate));
    }

    @Override
    public void handlePrecipitation(BlockState pState, Level pLevel, BlockPos pPos, Biome.Precipitation pPrecipitation) {
        if (CauldronBlock.shouldHandlePrecipitation(pLevel, pPrecipitation) && pState.getValue(LEVEL) != 3 && pPrecipitation == this.f_302573_) {
            BlockState blockstate = pState.cycle(LEVEL);
            pLevel.setBlockAndUpdate(pPos, blockstate);
            pLevel.m_322719_(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(blockstate));
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return pState.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

    @Override
    protected void receiveStalactiteDrip(BlockState pState, Level pLevel, BlockPos pPos, Fluid pFluid) {
        if (!this.isFull(pState)) {
            BlockState blockstate = pState.setValue(LEVEL, Integer.valueOf(pState.getValue(LEVEL) + 1));
            pLevel.setBlockAndUpdate(pPos, blockstate);
            pLevel.m_322719_(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(blockstate));
            pLevel.levelEvent(1047, pPos, 0);
        }
    }
}