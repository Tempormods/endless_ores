package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperStairBlock extends StairBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperStairBlock> f_303046_ = RecordCodecBuilder.mapCodec(
        p_311618_ -> p_311618_.group(
                    WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
                    BlockState.CODEC.fieldOf("base_state").forGetter(p_312323_ -> p_312323_.baseState),
                    m_305607_()
                )
                .apply(p_311618_, WeatheringCopperStairBlock::new)
    );
    private final WeatheringCopper.WeatherState weatherState;

    @Override
    public MapCodec<WeatheringCopperStairBlock> m_304657_() {
        return f_303046_;
    }

    public WeatheringCopperStairBlock(WeatheringCopper.WeatherState pWeatherState, BlockState pBaseState, BlockBehaviour.Properties pProperties) {
        super(pBaseState, pProperties);
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