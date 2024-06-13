package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceBlock extends Block {
    public static final MapCodec<DropExperienceBlock> f_303239_ = RecordCodecBuilder.mapCodec(
        p_311183_ -> p_311183_.group(IntProvider.codec(0, 10).fieldOf("experience").forGetter(p_311138_ -> p_311138_.xpRange), m_305607_())
                .apply(p_311183_, DropExperienceBlock::new)
    );
    private final IntProvider xpRange;

    @Override
    public MapCodec<? extends DropExperienceBlock> m_304657_() {
        return f_303239_;
    }

    public DropExperienceBlock(IntProvider pXpRange, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.xpRange = pXpRange;
    }

    @Override
    protected void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean pDropExperience) {
        super.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);
        if (false && pDropExperience) { // Forge: moved to getExpDrop
            this.tryDropExperience(pLevel, pPos, pStack, this.xpRange);
        }
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
       return silkTouchLevel == 0 ? this.xpRange.sample(randomSource) : 0;
    }
}
