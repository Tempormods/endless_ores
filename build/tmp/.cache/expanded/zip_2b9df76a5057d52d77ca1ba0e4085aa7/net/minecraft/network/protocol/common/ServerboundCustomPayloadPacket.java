package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet<ServerCommonPacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 32767;
    public static final StreamCodec<FriendlyByteBuf, ServerboundCustomPayloadPacket> f_316323_ = CustomPacketPayload.<FriendlyByteBuf>m_323250_(
            p_335203_ -> net.minecraftforge.common.ForgeHooks.getCustomPayloadCodec(p_335203_, 32767),
            Util.make(Lists.newArrayList(new CustomPacketPayload.TypeAndCodec<>(BrandPayload.f_315254_, BrandPayload.f_315010_)), p_334419_ -> {
            })
        )
        .m_323038_(ServerboundCustomPayloadPacket::new, ServerboundCustomPayloadPacket::payload);

    @Override
    public PacketType<ServerboundCustomPayloadPacket> write() {
        return CommonPacketTypes.f_314805_;
    }

    public void handle(ServerCommonPacketListener pHandler) {
        pHandler.handleCustomPayload(this);
    }
}
