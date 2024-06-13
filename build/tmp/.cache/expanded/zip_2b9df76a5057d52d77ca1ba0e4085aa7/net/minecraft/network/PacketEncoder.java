package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

/**
 * Main netty packet encoder. Writes the packet ID as a VarInt based on the current {@link ConnectionProtocol} as well
 * as the packet's data.
 */
public class PacketEncoder<T extends PacketListener> extends MessageToByteEncoder<Packet<T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ProtocolInfo<T> f_315245_;

    public PacketEncoder(ProtocolInfo<T> p_327768_) {
        this.f_315245_ = p_327768_;
    }

    protected void encode(ChannelHandlerContext p_130545_, Packet<T> p_130546_, ByteBuf p_130547_) throws Exception {
        PacketType<? extends Packet<? super T>> packettype = p_130546_.write();

        try {
            this.f_315245_.m_319098_().m_318638_(p_130547_, p_130546_);
            int i = p_130547_.readableBytes();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {} -> {} bytes", this.f_315245_.m_320326_().id(), packettype, p_130546_.getClass().getName(), i
                );
            }

            JvmProfiler.INSTANCE.onPacketSent(this.f_315245_.m_320326_(), packettype, p_130545_.channel().remoteAddress(), i);
        } catch (Throwable throwable) {
            LOGGER.error("Error sending packet {}", packettype, throwable);
            if (p_130546_.isSkippable()) {
                throw new SkipPacketException(throwable);
            }

            throw throwable;
        } finally {
            ProtocolSwapHandler.m_323470_(p_130545_, p_130546_);
        }
    }
}