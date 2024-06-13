package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetScorePacket(String owner, String objectiveName, int score, Optional<Component> f_302966_, Optional<NumberFormat> f_302463_)
    implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetScorePacket> f_316191_ = StreamCodec.m_319894_(
        ByteBufCodecs.f_315450_,
        ClientboundSetScorePacket::owner,
        ByteBufCodecs.f_315450_,
        ClientboundSetScorePacket::objectiveName,
        ByteBufCodecs.f_316730_,
        ClientboundSetScorePacket::score,
        ComponentSerialization.f_316844_,
        ClientboundSetScorePacket::f_302966_,
        NumberFormatTypes.f_316603_,
        ClientboundSetScorePacket::f_302463_,
        ClientboundSetScorePacket::new
    );

    @Override
    public PacketType<ClientboundSetScorePacket> write() {
        return GamePacketTypes.f_315384_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetScore(this);
    }
}