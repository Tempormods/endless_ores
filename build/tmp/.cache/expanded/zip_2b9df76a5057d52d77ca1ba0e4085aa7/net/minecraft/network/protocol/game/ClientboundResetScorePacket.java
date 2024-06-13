package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundResetScorePacket(String f_303137_, @Nullable String f_302815_) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundResetScorePacket> f_314577_ = Packet.m_319422_(
        ClientboundResetScorePacket::m_304876_, ClientboundResetScorePacket::new
    );

    private ClientboundResetScorePacket(FriendlyByteBuf p_312061_) {
        this(p_312061_.readUtf(), p_312061_.readNullable(FriendlyByteBuf::readUtf));
    }

    private void m_304876_(FriendlyByteBuf p_310951_) {
        p_310951_.writeUtf(this.f_303137_);
        p_310951_.m_321806_(this.f_302815_, FriendlyByteBuf::writeUtf);
    }

    @Override
    public PacketType<ClientboundResetScorePacket> write() {
        return GamePacketTypes.f_315844_;
    }

    public void handle(ClientGamePacketListener p_310650_) {
        p_310650_.m_305143_(this);
    }
}