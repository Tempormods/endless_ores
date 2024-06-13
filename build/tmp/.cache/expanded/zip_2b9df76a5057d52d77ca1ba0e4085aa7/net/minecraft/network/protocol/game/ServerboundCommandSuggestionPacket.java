package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundCommandSuggestionPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundCommandSuggestionPacket> f_316231_ = Packet.m_319422_(
        ServerboundCommandSuggestionPacket::m_133902_, ServerboundCommandSuggestionPacket::new
    );
    private final int id;
    private final String command;

    public ServerboundCommandSuggestionPacket(int pId, String pCommand) {
        this.id = pId;
        this.command = pCommand;
    }

    private ServerboundCommandSuggestionPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readVarInt();
        this.command = pBuffer.readUtf(32500);
    }

    private void m_133902_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.id);
        pBuffer.writeUtf(this.command, 32500);
    }

    @Override
    public PacketType<ServerboundCommandSuggestionPacket> write() {
        return GamePacketTypes.f_314601_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleCustomCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public String getCommand() {
        return this.command;
    }
}