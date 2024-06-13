package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

/**
 * Main netty packet decoder. Reads the packet ID as a VarInt and creates the corresponding packet
 * based on the current {@link ConnectionProtocol}.
 */
public class PacketDecoder<T extends PacketListener> extends ByteToMessageDecoder implements ProtocolSwapHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ProtocolInfo<T> f_314348_;

    public PacketDecoder(ProtocolInfo<T> p_336253_) {
        this.f_314348_ = p_336253_;
    }

    @Override
    protected void decode(ChannelHandlerContext pContext, ByteBuf pIn, List<Object> pOut) throws Exception {
        int i = pIn.readableBytes();
        if (i != 0) {
            Packet<? super T> packet = this.f_314348_.m_319098_().m_318688_(pIn);
            PacketType<? extends Packet<? super T>> packettype = packet.write();
            JvmProfiler.INSTANCE.onPacketReceived(this.f_314348_.m_320326_(), packettype, pContext.channel().remoteAddress(), i);
            if (pIn.readableBytes() > 0) {
                throw new IOException(
                    "Packet "
                        + this.f_314348_.m_320326_().id()
                        + "/"
                        + packettype
                        + " ("
                        + packet.getClass().getSimpleName()
                        + ") was larger than I expected, found "
                        + pIn.readableBytes()
                        + " bytes extra whilst reading packet "
                        + packettype
                );
            } else {
                pOut.add(packet);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                        Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {} -> {} bytes", this.f_314348_.m_320326_().id(), packettype, packet.getClass().getName(), i
                    );
                }

                ProtocolSwapHandler.m_319640_(pContext, packet);
            }
        }
    }
}