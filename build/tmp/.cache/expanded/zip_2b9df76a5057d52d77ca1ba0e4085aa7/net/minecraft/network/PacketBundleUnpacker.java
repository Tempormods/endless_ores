package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
    private final BundlerInfo f_314046_;

    public PacketBundleUnpacker(BundlerInfo p_335271_) {
        this.f_314046_ = p_335271_;
    }

    protected void encode(ChannelHandlerContext pContext, Packet<?> pPacket, List<Object> p_265735_) throws Exception {
        this.f_314046_.unbundlePacket(pPacket, p_265735_::add);
        if (pPacket.m_319635_()) {
            pContext.pipeline().remove(pContext.name());
        }
    }
}