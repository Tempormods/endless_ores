package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundGameProfilePacket(GameProfile gameProfile, @Deprecated(forRemoval = true) boolean f_315909_) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<ByteBuf, ClientboundGameProfilePacket> f_315571_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_314168_,
        ClientboundGameProfilePacket::gameProfile,
        ByteBufCodecs.f_315514_,
        ClientboundGameProfilePacket::f_315909_,
        ClientboundGameProfilePacket::new
    );

    @Override
    public PacketType<ClientboundGameProfilePacket> write() {
        return LoginPacketTypes.f_314987_;
    }

    public void handle(ClientLoginPacketListener pHandler) {
        pHandler.handleGameProfile(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}