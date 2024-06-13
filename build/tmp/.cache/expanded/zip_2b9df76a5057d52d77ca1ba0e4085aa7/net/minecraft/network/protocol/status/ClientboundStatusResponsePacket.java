package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundStatusResponsePacket(ServerStatus status, @org.jetbrains.annotations.Nullable String cachedStatus) implements Packet<ClientStatusPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundStatusResponsePacket> f_316394_ = Packet.m_319422_(
        ClientboundStatusResponsePacket::m_134898_, ClientboundStatusResponsePacket::new
    );

    public ClientboundStatusResponsePacket(ServerStatus status) {
        this(status, null);
    }

    private ClientboundStatusResponsePacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readJsonWithCodec(ServerStatus.CODEC));
    }

    private void m_134898_(FriendlyByteBuf pBuffer) {
        if (cachedStatus != null) pBuffer.writeUtf(cachedStatus);
        else
        pBuffer.writeJsonWithCodec(ServerStatus.CODEC, this.status);
    }

    @Override
    public PacketType<ClientboundStatusResponsePacket> write() {
        return StatusPacketTypes.f_315020_;
    }

    public void handle(ClientStatusPacketListener pHandler) {
        pHandler.handleStatusResponse(this);
    }
}
