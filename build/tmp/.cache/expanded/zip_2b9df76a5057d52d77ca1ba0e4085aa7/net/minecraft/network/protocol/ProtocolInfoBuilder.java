package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.codec.StreamCodec;

public class ProtocolInfoBuilder<T extends PacketListener, B extends ByteBuf> {
    private final ConnectionProtocol f_317112_;
    private final PacketFlow f_315448_;
    private final List<ProtocolInfoBuilder.CodecEntry<T, ?, B>> f_314904_ = new ArrayList<>();
    @Nullable
    private BundlerInfo f_315466_;

    public ProtocolInfoBuilder(ConnectionProtocol p_334175_, PacketFlow p_335651_) {
        this.f_317112_ = p_334175_;
        this.f_315448_ = p_335651_;
    }

    public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B> m_322062_(PacketType<P> p_335373_, StreamCodec<? super B, P> p_333531_) {
        this.f_314904_.add(new ProtocolInfoBuilder.CodecEntry<>(p_335373_, p_333531_));
        return this;
    }

    public <P extends BundlePacket<? super T>, D extends BundleDelimiterPacket<? super T>> ProtocolInfoBuilder<T, B> m_319612_(
        PacketType<P> p_336277_, Function<Iterable<Packet<? super T>>, P> p_331716_, D p_328432_
    ) {
        StreamCodec<ByteBuf, D> streamcodec = StreamCodec.m_323136_(p_328432_);
        PacketType<D> packettype = (PacketType)p_328432_.write();
        this.f_314904_.add(new ProtocolInfoBuilder.CodecEntry<>(packettype, streamcodec));
        this.f_315466_ = BundlerInfo.createForPacket(p_336277_, p_331716_, p_328432_);
        return this;
    }

    private StreamCodec<ByteBuf, Packet<? super T>> m_320976_(Function<ByteBuf, B> p_331741_, List<ProtocolInfoBuilder.CodecEntry<T, ?, B>> p_329135_) {
        ProtocolCodecBuilder<ByteBuf, T> protocolcodecbuilder = new ProtocolCodecBuilder<>(this.f_315448_);

        for (ProtocolInfoBuilder.CodecEntry<T, ?, B> codecentry : p_329135_) {
            codecentry.m_319563_(protocolcodecbuilder, p_331741_);
        }

        return protocolcodecbuilder.m_324692_();
    }

    public ProtocolInfo<T> m_319209_(Function<ByteBuf, B> p_336320_) {
        return new ProtocolInfoBuilder.Implementation<>(this.f_317112_, this.f_315448_, this.m_320976_(p_336320_, this.f_314904_), this.f_315466_);
    }

    public ProtocolInfo.Unbound<T, B> m_318838_() {
        List<ProtocolInfoBuilder.CodecEntry<T, ?, B>> list = List.copyOf(this.f_314904_);
        BundlerInfo bundlerinfo = this.f_315466_;
        return p_334608_ -> new ProtocolInfoBuilder.Implementation<>(this.f_317112_, this.f_315448_, this.m_320976_(p_334608_, list), bundlerinfo);
    }

    private static <L extends PacketListener> ProtocolInfo<L> m_321517_(
        ConnectionProtocol p_334123_, PacketFlow p_327838_, Consumer<ProtocolInfoBuilder<L, FriendlyByteBuf>> p_327993_
    ) {
        ProtocolInfoBuilder<L, FriendlyByteBuf> protocolinfobuilder = new ProtocolInfoBuilder<>(p_334123_, p_327838_);
        p_327993_.accept(protocolinfobuilder);
        return protocolinfobuilder.m_319209_(FriendlyByteBuf::new);
    }

    public static <T extends ServerboundPacketListener> ProtocolInfo<T> m_323394_(
        ConnectionProtocol p_331618_, Consumer<ProtocolInfoBuilder<T, FriendlyByteBuf>> p_330318_
    ) {
        return m_321517_(p_331618_, PacketFlow.SERVERBOUND, p_330318_);
    }

    public static <T extends ClientboundPacketListener> ProtocolInfo<T> m_322020_(
        ConnectionProtocol p_329688_, Consumer<ProtocolInfoBuilder<T, FriendlyByteBuf>> p_332900_
    ) {
        return m_321517_(p_329688_, PacketFlow.CLIENTBOUND, p_332900_);
    }

    private static <L extends PacketListener, B extends ByteBuf> ProtocolInfo.Unbound<L, B> m_319891_(
        ConnectionProtocol p_330235_, PacketFlow p_335045_, Consumer<ProtocolInfoBuilder<L, B>> p_329753_
    ) {
        ProtocolInfoBuilder<L, B> protocolinfobuilder = new ProtocolInfoBuilder<>(p_330235_, p_335045_);
        p_329753_.accept(protocolinfobuilder);
        return protocolinfobuilder.m_318838_();
    }

    public static <T extends ServerboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> m_321568_(
        ConnectionProtocol p_330435_, Consumer<ProtocolInfoBuilder<T, B>> p_333183_
    ) {
        return m_319891_(p_330435_, PacketFlow.SERVERBOUND, p_333183_);
    }

    public static <T extends ClientboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> m_323393_(
        ConnectionProtocol p_330385_, Consumer<ProtocolInfoBuilder<T, B>> p_330670_
    ) {
        return m_319891_(p_330385_, PacketFlow.CLIENTBOUND, p_330670_);
    }

    static record CodecEntry<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf>(
        PacketType<P> f_315645_, StreamCodec<? super B, P> f_315912_
    ) {
        public void m_319563_(ProtocolCodecBuilder<ByteBuf, T> p_328095_, Function<ByteBuf, B> p_333803_) {
            StreamCodec<ByteBuf, P> streamcodec = this.f_315912_.m_322313_(p_333803_);
            p_328095_.m_320599_(this.f_315645_, streamcodec);
        }
    }

    static record Implementation<L extends PacketListener>(
        ConnectionProtocol f_316542_, PacketFlow f_314614_, StreamCodec<ByteBuf, Packet<? super L>> f_314239_, @Nullable BundlerInfo f_313910_
    ) implements ProtocolInfo<L> {
        @Nullable
        @Override
        public BundlerInfo m_320896_() {
            return this.f_313910_;
        }

        @Override
        public ConnectionProtocol m_320326_() {
            return this.f_316542_;
        }

        @Override
        public PacketFlow m_319133_() {
            return this.f_314614_;
        }

        @Override
        public StreamCodec<ByteBuf, Packet<? super L>> m_319098_() {
            return this.f_314239_;
        }
    }
}