package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperFullBlock extends Block implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperFullBlock> f_303224_ = RecordCodecBuilder.mapCodec(
        p_312748_ -> p_312748_.group(WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), m_305607_())
                .apply(p_312748_, WeatheringCopperFullBlock::new)
    );
    private final WeatheringCopper.WeatherState weatherState;

    @Override
    public MapCodec<WeatheringCopperFullBlock> m_304657_() {
        return f_303224_;
    }

    public WeatheringCopperFullBlock(WeatheringCopper.WeatherState pWeatherState, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.weatherState = pWeatherState;
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.m_306166_(pState, pLevel, pPos, pRandom);
    }

    @Override
    protected boolean m_51695_(BlockState pState) {
        return WeatheringCopper.getNext(pState.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}