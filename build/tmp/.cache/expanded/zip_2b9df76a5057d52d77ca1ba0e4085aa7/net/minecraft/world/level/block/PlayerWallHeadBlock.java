package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerWallHeadBlock extends WallSkullBlock {
    public static final MapCodec<PlayerWallHeadBlock> f_302922_ = m_306223_(PlayerWallHeadBlock::new);

    @Override
    public MapCodec<PlayerWallHeadBlock> m_304657_() {
        return f_302922_;
    }

    public PlayerWallHeadBlock(BlockBehaviour.Properties p_55185_) {
        super(SkullBlock.Types.PLAYER, p_55185_);
    }
}