package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WorldGenAttemptDebugPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha)
    implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, WorldGenAttemptDebugPayload> f_315328_ = CustomPacketPayload.m_320054_(
        WorldGenAttemptDebugPayload::m_293398_, WorldGenAttemptDebugPayload::new
    );
    public static final CustomPacketPayload.Type<WorldGenAttemptDebugPayload> f_317154_ = CustomPacketPayload.m_319865_("debug/worldgen_attempt");

    private WorldGenAttemptDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBlockPos(), pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat());
    }

    private void m_293398_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeFloat(this.scale);
        pBuffer.writeFloat(this.red);
        pBuffer.writeFloat(this.green);
        pBuffer.writeFloat(this.blue);
        pBuffer.writeFloat(this.alpha);
    }

    @Override
    public CustomPacketPayload.Type<WorldGenAttemptDebugPayload> m_293297_() {
        return f_317154_;
    }
}