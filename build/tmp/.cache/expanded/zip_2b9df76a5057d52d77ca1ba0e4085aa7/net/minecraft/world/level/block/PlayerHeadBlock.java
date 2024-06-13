package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerHeadBlock extends SkullBlock {
    public static final MapCodec<PlayerHeadBlock> f_303392_ = m_306223_(PlayerHeadBlock::new);

    @Override
    public MapCodec<PlayerHeadBlock> m_304657_() {
        return f_303392_;
    }

    public PlayerHeadBlock(BlockBehaviour.Properties p_55177_) {
        super(SkullBlock.Types.PLAYER, p_55177_);
    }
}