package net.minecraft.network.protocol.common.custom;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;

public record BreezeDebugPayload(BreezeDebugPayload.BreezeInfo f_303745_) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BreezeDebugPayload> f_316917_ = CustomPacketPayload.m_320054_(
        BreezeDebugPayload::m_305357_, BreezeDebugPayload::new
    );
    public static final CustomPacketPayload.Type<BreezeDebugPayload> f_316538_ = CustomPacketPayload.m_319865_("debug/breeze");

    private BreezeDebugPayload(FriendlyByteBuf p_309515_) {
        this(new BreezeDebugPayload.BreezeInfo(p_309515_));
    }

    private void m_305357_(FriendlyByteBuf p_309794_) {
        this.f_303745_.m_306592_(p_309794_);
    }

    @Override
    public CustomPacketPayload.Type<BreezeDebugPayload> m_293297_() {
        return f_316538_;
    }

    public static record BreezeInfo(UUID f_303634_, int f_302477_, Integer f_303058_, BlockPos f_302733_) {
        public BreezeInfo(FriendlyByteBuf p_311987_) {
            this(p_311987_.readUUID(), p_311987_.readInt(), p_311987_.readNullable(FriendlyByteBuf::readInt), p_311987_.readNullable(BlockPos.f_316462_));
        }

        public void m_306592_(FriendlyByteBuf p_312731_) {
            p_312731_.writeUUID(this.f_303634_);
            p_312731_.writeInt(this.f_302477_);
            p_312731_.m_321806_(this.f_303058_, FriendlyByteBuf::writeInt);
            p_312731_.m_321806_(this.f_302733_, BlockPos.f_316462_);
        }

        public String m_305190_() {
            return DebugEntityNameGenerator.getEntityName(this.f_303634_);
        }

        @Override
        public String toString() {
            return this.m_305190_();
        }
    }
}