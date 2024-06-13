package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperBulbBlock extends CopperBulbBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperBulbBlock> f_303367_ = RecordCodecBuilder.mapCodec(
        p_311316_ -> p_311316_.group(
                    WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(WeatheringCopperBulbBlock::getAge), m_305607_()
                )
                .apply(p_311316_, WeatheringCopperBulbBlock::new)
    );
    private final WeatheringCopper.WeatherState f_302561_;

    @Override
    protected MapCodec<WeatheringCopperBulbBlock> m_304657_() {
        return f_303367_;
    }

    public WeatheringCopperBulbBlock(WeatheringCopper.WeatherState p_309695_, BlockBehaviour.Properties p_311798_) {
        super(p_311798_);
        this.f_302561_ = p_309695_;
    }

    @Override
    protected void randomTick(BlockState p_311293_, ServerLevel p_312278_, BlockPos p_309441_, RandomSource p_312720_) {
        this.m_306166_(p_311293_, p_312278_, p_309441_, p_312720_);
    }

    @Override
    protected boolean m_51695_(BlockState p_310542_) {
        return WeatheringCopper.getNext(p_310542_.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.f_302561_;
    }
}