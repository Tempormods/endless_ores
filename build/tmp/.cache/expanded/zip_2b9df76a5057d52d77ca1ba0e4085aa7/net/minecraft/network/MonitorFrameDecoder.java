package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorFrameDecoder extends ChannelInboundHandlerAdapter {
    private final BandwidthDebugMonitor f_316911_;

    public MonitorFrameDecoder(BandwidthDebugMonitor p_331226_) {
        this.f_316911_ = p_331226_;
    }

    @Override
    public void channelRead(ChannelHandlerContext p_328985_, Object p_332208_) {
        if (p_332208_ instanceof ByteBuf bytebuf) {
            this.f_316911_.onReceive(bytebuf.readableBytes());
        }

        p_328985_.fireChannelRead(p_332208_);
    }
}