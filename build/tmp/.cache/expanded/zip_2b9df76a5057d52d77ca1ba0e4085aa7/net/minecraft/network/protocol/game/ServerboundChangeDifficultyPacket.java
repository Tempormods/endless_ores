package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChangeDifficultyPacket> f_314182_ = Packet.m_319422_(
        ServerboundChangeDifficultyPacket::m_133825_, ServerboundChangeDifficultyPacket::new
    );
    private final Difficulty difficulty;

    public ServerboundChangeDifficultyPacket(Difficulty pDifficulty) {
        this.difficulty = pDifficulty;
    }

    private ServerboundChangeDifficultyPacket(FriendlyByteBuf pBuffer) {
        this.difficulty = Difficulty.byId(pBuffer.readUnsignedByte());
    }

    private void m_133825_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.difficulty.getId());
    }

    @Override
    public PacketType<ServerboundChangeDifficultyPacket> write() {
        return GamePacketTypes.f_314929_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChangeDifficulty(this);
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}