package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundBlockEntityTagQueryPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundBlockEntityTagQueryPacket> f_316461_ = Packet.m_319422_(
        ServerboundBlockEntityTagQueryPacket::m_321397_, ServerboundBlockEntityTagQueryPacket::new
    );
    private final int f_314835_;
    private final BlockPos f_315650_;

    public ServerboundBlockEntityTagQueryPacket(int p_336190_, BlockPos p_336300_) {
        this.f_314835_ = p_336190_;
        this.f_315650_ = p_336300_;
    }

    private ServerboundBlockEntityTagQueryPacket(FriendlyByteBuf p_328758_) {
        this.f_314835_ = p_328758_.readVarInt();
        this.f_315650_ = p_328758_.readBlockPos();
    }

    private void m_321397_(FriendlyByteBuf p_333511_) {
        p_333511_.writeVarInt(this.f_314835_);
        p_333511_.writeBlockPos(this.f_315650_);
    }

    @Override
    public PacketType<ServerboundBlockEntityTagQueryPacket> write() {
        return GamePacketTypes.f_316436_;
    }

    public void handle(ServerGamePacketListener p_335705_) {
        p_335705_.handleBlockEntityTagQuery(this);
    }

    public int m_320026_() {
        return this.f_314835_;
    }

    public BlockPos m_324477_() {
        return this.f_315650_;
    }
}