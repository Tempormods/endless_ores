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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class WeatheringCopperDoorBlock extends DoorBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperDoorBlock> f_302749_ = RecordCodecBuilder.mapCodec(
        p_312483_ -> p_312483_.group(
                    BlockSetType.f_303023_.fieldOf("block_set_type").forGetter(DoorBlock::type),
                    WeatheringCopper.WeatherState.f_302372_.fieldOf("weathering_state").forGetter(WeatheringCopperDoorBlock::getAge),
                    m_305607_()
                )
                .apply(p_312483_, WeatheringCopperDoorBlock::new)
    );
    private final WeatheringCopper.WeatherState f_302235_;

    @Override
    public MapCodec<WeatheringCopperDoorBlock> m_304657_() {
        return f_302749_;
    }

    public WeatheringCopperDoorBlock(BlockSetType p_312938_, WeatheringCopper.WeatherState p_310944_, BlockBehaviour.Properties p_312284_) {
        super(p_312938_, p_312284_);
        this.f_302235_ = p_310944_;
    }

    @Override
    protected void randomTick(BlockState p_312732_, ServerLevel p_309748_, BlockPos p_312849_, RandomSource p_311578_) {
        if (p_312732_.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            this.m_306166_(p_312732_, p_309748_, p_312849_, p_311578_);
        }
    }

    @Override
    protected boolean m_51695_(BlockState p_312606_) {
        return WeatheringCopper.getNext(p_312606_.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.f_302235_;
    }
}