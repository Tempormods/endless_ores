package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundLockDifficultyPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundLockDifficultyPacket> f_314910_ = Packet.m_319422_(
        ServerboundLockDifficultyPacket::m_134116_, ServerboundLockDifficultyPacket::new
    );
    private final boolean locked;

    public ServerboundLockDifficultyPacket(boolean pLocked) {
        this.locked = pLocked;
    }

    private ServerboundLockDifficultyPacket(FriendlyByteBuf pBuffer) {
        this.locked = pBuffer.readBoolean();
    }

    private void m_134116_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.locked);
    }

    @Override
    public PacketType<ServerboundLockDifficultyPacket> write() {
        return GamePacketTypes.f_315556_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleLockDifficulty(this);
    }

    public boolean isLocked() {
        return this.locked;
    }
}