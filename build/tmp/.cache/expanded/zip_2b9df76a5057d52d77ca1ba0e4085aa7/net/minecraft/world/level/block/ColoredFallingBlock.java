package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredFallingBlock extends FallingBlock {
    public static final MapCodec<ColoredFallingBlock> f_303304_ = RecordCodecBuilder.mapCodec(
        p_311859_ -> p_311859_.group(ColorRGBA.f_303313_.fieldOf("falling_dust_color").forGetter(p_309656_ -> p_309656_.f_303066_), m_305607_())
                .apply(p_311859_, ColoredFallingBlock::new)
    );
    private final ColorRGBA f_303066_;

    @Override
    public MapCodec<ColoredFallingBlock> m_304657_() {
        return f_303304_;
    }

    public ColoredFallingBlock(ColorRGBA p_310631_, BlockBehaviour.Properties p_312848_) {
        super(p_312848_);
        this.f_303066_ = p_310631_;
    }

    @Override
    public int getDustColor(BlockState p_309534_, BlockGetter p_310029_, BlockPos p_312470_) {
        return this.f_303066_.f_303724_();
    }
}