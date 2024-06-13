package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassPaneBlock extends IronBarsBlock implements BeaconBeamBlock {
    public static final MapCodec<StainedGlassPaneBlock> f_302418_ = RecordCodecBuilder.mapCodec(
        p_311123_ -> p_311123_.group(DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassPaneBlock::getColor), m_305607_())
                .apply(p_311123_, StainedGlassPaneBlock::new)
    );
    private final DyeColor color;

    @Override
    public MapCodec<StainedGlassPaneBlock> m_304657_() {
        return f_302418_;
    }

    public StainedGlassPaneBlock(DyeColor pColor, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.color = pColor;
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false))
                .setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
        );
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}