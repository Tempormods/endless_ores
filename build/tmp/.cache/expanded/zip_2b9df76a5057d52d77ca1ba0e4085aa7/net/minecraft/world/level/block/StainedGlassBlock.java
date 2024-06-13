package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassBlock extends TransparentBlock implements BeaconBeamBlock {
    public static final MapCodec<StainedGlassBlock> f_303356_ = RecordCodecBuilder.mapCodec(
        p_312506_ -> p_312506_.group(DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassBlock::getColor), m_305607_())
                .apply(p_312506_, StainedGlassBlock::new)
    );
    private final DyeColor color;

    @Override
    public MapCodec<StainedGlassBlock> m_304657_() {
        return f_303356_;
    }

    public StainedGlassBlock(DyeColor pDyeColor, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.color = pDyeColor;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}