package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperGrateBlock extends WaterloggedTransparentBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperGrateBlock> f_302539_ = RecordCodecBuilder.mapCodec(
        p_313130_ -> p_313130_.group(
                    WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(WeatheringCopperGrateBlock::getAge), m_305607_()
                )
                .apply(p_313130_, WeatheringCopperGrateBlock::new)
    );
    private final WeatheringCopper.WeatherState f_302631_;

    @Override
    protected MapCodec<WeatheringCopperGrateBlock> m_304657_() {
        return f_302539_;
    }

    public WeatheringCopperGrateBlock(WeatheringCopper.WeatherState p_311827_, BlockBehaviour.Properties p_311858_) {
        super(p_311858_);
        this.f_302631_ = p_311827_;
    }

    @Override
    protected void randomTick(BlockState p_309962_, ServerLevel p_309911_, BlockPos p_311585_, RandomSource p_310772_) {
        this.m_306166_(p_309962_, p_309911_, p_311585_, p_310772_);
    }

    @Override
    protected boolean m_51695_(BlockState p_310531_) {
        return WeatheringCopper.getNext(p_310531_.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.f_302631_;
    }
}