package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundEntityTagQueryPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundEntityTagQueryPacket> f_315919_ = Packet.m_319422_(
        ServerboundEntityTagQueryPacket::m_322479_, ServerboundEntityTagQueryPacket::new
    );
    private final int f_317015_;
    private final int f_313931_;

    public ServerboundEntityTagQueryPacket(int p_332553_, int p_328823_) {
        this.f_317015_ = p_332553_;
        this.f_313931_ = p_328823_;
    }

    private ServerboundEntityTagQueryPacket(FriendlyByteBuf p_333986_) {
        this.f_317015_ = p_333986_.readVarInt();
        this.f_313931_ = p_333986_.readVarInt();
    }

    private void m_322479_(FriendlyByteBuf p_333064_) {
        p_333064_.writeVarInt(this.f_317015_);
        p_333064_.writeVarInt(this.f_313931_);
    }

    @Override
    public PacketType<ServerboundEntityTagQueryPacket> write() {
        return GamePacketTypes.f_314400_;
    }

    public void handle(ServerGamePacketListener p_330266_) {
        p_330266_.handleEntityTagQuery(this);
    }

    public int m_321979_() {
        return this.f_317015_;
    }

    public int m_319000_() {
        return this.f_313931_;
    }
}