package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerInfoRemovePacket> f_314830_ = Packet.m_319422_(
        ClientboundPlayerInfoRemovePacket::m_245208_, ClientboundPlayerInfoRemovePacket::new
    );

    private ClientboundPlayerInfoRemovePacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readList(UUIDUtil.f_315346_));
    }

    private void m_245208_(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(this.profileIds, UUIDUtil.f_315346_);
    }

    @Override
    public PacketType<ClientboundPlayerInfoRemovePacket> write() {
        return GamePacketTypes.f_316010_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlayerInfoRemove(this);
    }
}