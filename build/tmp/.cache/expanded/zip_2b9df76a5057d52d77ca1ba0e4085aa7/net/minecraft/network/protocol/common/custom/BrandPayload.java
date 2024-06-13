package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BrandPayload(String brand) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BrandPayload> f_315010_ = CustomPacketPayload.m_320054_(BrandPayload::m_295270_, BrandPayload::new);
    public static final CustomPacketPayload.Type<BrandPayload> f_315254_ = CustomPacketPayload.m_319865_("brand");

    private BrandPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readUtf());
    }

    private void m_295270_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.brand);
    }

    @Override
    public CustomPacketPayload.Type<BrandPayload> m_293297_() {
        return f_315254_;
    }
}