package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ClientInformation;

public record ServerboundClientInformationPacket(ClientInformation information) implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundClientInformationPacket> f_315737_ = Packet.m_319422_(
        ServerboundClientInformationPacket::m_295726_, ServerboundClientInformationPacket::new
    );

    private ServerboundClientInformationPacket(FriendlyByteBuf pBuffer) {
        this(new ClientInformation(pBuffer));
    }

    private void m_295726_(FriendlyByteBuf pBuffer) {
        this.information.write(pBuffer);
    }

    @Override
    public PacketType<ServerboundClientInformationPacket> write() {
        return CommonPacketTypes.f_314731_;
    }

    public void handle(ServerCommonPacketListener pHandler) {
        pHandler.handleClientInformation(this);
    }
}