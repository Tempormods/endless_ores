package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
    CustomPacketPayload.Type<? extends CustomPacketPayload> m_293297_();

    static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> m_320054_(StreamMemberEncoder<B, T> p_336135_, StreamDecoder<B, T> p_335771_) {
        return StreamCodec.m_324771_(p_336135_, p_335771_);
    }

    static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> m_319865_(String p_331650_) {
        return new CustomPacketPayload.Type<>(new ResourceLocation(p_331650_));
    }

    static <B extends FriendlyByteBuf> StreamCodec<B, CustomPacketPayload> m_323250_(
        final CustomPacketPayload.FallbackProvider<B> p_329573_, List<CustomPacketPayload.TypeAndCodec<? super B, ?>> p_333081_
    ) {
        final Map<ResourceLocation, StreamCodec<? super B, ? extends CustomPacketPayload>> map = p_333081_.stream()
            .collect(Collectors.toUnmodifiableMap(p_332174_ -> p_332174_.f_317091_().f_314054_(), CustomPacketPayload.TypeAndCodec::f_314756_));
        return new StreamCodec<B, CustomPacketPayload>() {
            private StreamCodec<? super B, ? extends CustomPacketPayload> m_319999_(ResourceLocation p_335824_) {
                StreamCodec<? super B, ? extends CustomPacketPayload> streamcodec = map.get(p_335824_);
                return streamcodec != null ? streamcodec : p_329573_.m_319221_(p_335824_);
            }

            private <T extends CustomPacketPayload> void m_325081_(B p_332252_, CustomPacketPayload.Type<T> p_334465_, CustomPacketPayload p_334290_) {
                p_332252_.writeResourceLocation(p_334465_.f_314054_());
                StreamCodec<B, T> streamcodec = (StreamCodec)this.m_319999_(p_334465_.f_314054_);
                streamcodec.m_318638_(p_332252_, (T)p_334290_);
            }

            public void m_318638_(B p_334992_, CustomPacketPayload p_329854_) {
                this.m_325081_(p_334992_, p_329854_.m_293297_(), p_329854_);
            }

            public CustomPacketPayload m_318688_(B p_334320_) {
                ResourceLocation resourcelocation = p_334320_.readResourceLocation();
                return (CustomPacketPayload)this.m_319999_(resourcelocation).m_318688_(p_334320_);
            }
        };
    }

    public interface FallbackProvider<B extends FriendlyByteBuf> {
        StreamCodec<B, ? extends CustomPacketPayload> m_319221_(ResourceLocation p_336163_);
    }

    public static record Type<T extends CustomPacketPayload>(ResourceLocation f_314054_) {
    }

    public static record TypeAndCodec<B extends FriendlyByteBuf, T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> f_317091_, StreamCodec<B, T> f_314756_
    ) {
    }
}