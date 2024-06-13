package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.IdDispatchCodec;
import net.minecraft.network.codec.StreamCodec;

public class ProtocolCodecBuilder<B extends ByteBuf, L extends PacketListener> {
    private final IdDispatchCodec.Builder<B, Packet<? super L>, PacketType<? extends Packet<? super L>>> f_314423_ = IdDispatchCodec.m_323921_(Packet::write);
    private final PacketFlow f_315095_;

    public ProtocolCodecBuilder(PacketFlow p_334440_) {
        this.f_315095_ = p_334440_;
    }

    public <T extends Packet<? super L>> ProtocolCodecBuilder<B, L> m_320599_(PacketType<T> p_331162_, StreamCodec<? super B, T> p_335909_) {
        if (p_331162_.f_314819_() != this.f_315095_) {
            throw new IllegalArgumentException("Invalid packet flow for packet " + p_331162_ + ", expected " + this.f_315095_.name());
        } else {
            this.f_314423_.m_321255_(p_331162_, p_335909_);
            return this;
        }
    }

    public StreamCodec<B, Packet<? super L>> m_324692_() {
        return this.f_314423_.m_324285_();
    }
}