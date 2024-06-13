package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource implements PositionSource {
    public static final MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec(
        p_157710_ -> p_157710_.group(BlockPos.CODEC.fieldOf("pos").forGetter(p_223611_ -> p_223611_.pos)).apply(p_157710_, BlockPositionSource::new)
    );
    public static final StreamCodec<ByteBuf, BlockPositionSource> f_316003_ = StreamCodec.m_322204_(
        BlockPos.f_316462_, p_327427_ -> p_327427_.pos, BlockPositionSource::new
    );
    private final BlockPos pos;

    public BlockPositionSource(BlockPos p_157703_) {
        this.pos = p_157703_;
    }

    @Override
    public Optional<Vec3> getPosition(Level pLevel) {
        return Optional.of(Vec3.atCenterOf(this.pos));
    }

    @Override
    public PositionSourceType<BlockPositionSource> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type implements PositionSourceType<BlockPositionSource> {
        @Override
        public MapCodec<BlockPositionSource> codec() {
            return BlockPositionSource.CODEC;
        }

        @Override
        public StreamCodec<ByteBuf, BlockPositionSource> m_322720_() {
            return BlockPositionSource.f_316003_;
        }
    }
}