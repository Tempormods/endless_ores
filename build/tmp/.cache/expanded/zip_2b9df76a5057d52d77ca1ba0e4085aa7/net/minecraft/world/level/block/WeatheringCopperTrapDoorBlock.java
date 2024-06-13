package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class WeatheringCopperTrapDoorBlock extends TrapDoorBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperTrapDoorBlock> f_302581_ = RecordCodecBuilder.mapCodec(
        p_311951_ -> p_311951_.group(
                    BlockSetType.f_303023_.fieldOf("block_set_type").forGetter(TrapDoorBlock::m_306287_),
                    WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(WeatheringCopperTrapDoorBlock::getAge),
                    m_305607_()
                )
                .apply(p_311951_, WeatheringCopperTrapDoorBlock::new)
    );
    private final WeatheringCopper.WeatherState f_303726_;

    @Override
    public MapCodec<WeatheringCopperTrapDoorBlock> m_304657_() {
        return f_302581_;
    }

    public WeatheringCopperTrapDoorBlock(BlockSetType p_310902_, WeatheringCopper.WeatherState p_310376_, BlockBehaviour.Properties p_311219_) {
        super(p_310902_, p_311219_);
        this.f_303726_ = p_310376_;
    }

    @Override
    protected void randomTick(BlockState p_311400_, ServerLevel p_310287_, BlockPos p_310085_, RandomSource p_311069_) {
        this.m_306166_(p_311400_, p_310287_, p_310085_, p_311069_);
    }

    @Override
    protected boolean m_51695_(BlockState p_312946_) {
        return WeatheringCopper.getNext(p_312946_.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.f_303726_;
    }
}