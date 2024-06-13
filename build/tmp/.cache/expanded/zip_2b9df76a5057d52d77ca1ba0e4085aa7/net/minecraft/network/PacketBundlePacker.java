package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundlePacker extends MessageToMessageDecoder<Packet<?>> {
    private final BundlerInfo f_315593_;
    @Nullable
    private BundlerInfo.Bundler currentBundler;

    public PacketBundlePacker(BundlerInfo p_333768_) {
        this.f_315593_ = p_333768_;
    }

    protected void decode(ChannelHandlerContext pContext, Packet<?> pPacket, List<Object> p_265368_) throws Exception {
        if (this.currentBundler != null) {
            m_320906_(pPacket);
            Packet<?> packet = this.currentBundler.addPacket(pPacket);
            if (packet != null) {
                this.currentBundler = null;
                p_265368_.add(packet);
            }
        } else {
            BundlerInfo.Bundler bundlerinfo$bundler = this.f_315593_.startPacketBundling(pPacket);
            if (bundlerinfo$bundler != null) {
                m_320906_(pPacket);
                this.currentBundler = bundlerinfo$bundler;
            } else {
                p_265368_.add(pPacket);
                if (pPacket.m_319635_()) {
                    pContext.pipeline().remove(pContext.name());
                }
            }
        }
    }

    private static void m_320906_(Packet<?> p_329638_) {
        if (p_329638_.m_319635_()) {
            throw new DecoderException("Terminal message received in bundle");
        }
    }
}