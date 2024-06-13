package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStepPacket(int f_302557_) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTickingStepPacket> f_315331_ = Packet.m_319422_(
        ClientboundTickingStepPacket::m_307105_, ClientboundTickingStepPacket::new
    );

    private ClientboundTickingStepPacket(FriendlyByteBuf p_311037_) {
        this(p_311037_.readVarInt());
    }

    public static ClientboundTickingStepPacket m_305989_(TickRateManager p_312211_) {
        return new ClientboundTickingStepPacket(p_312211_.m_306668_());
    }

    private void m_307105_(FriendlyByteBuf p_311017_) {
        p_311017_.writeVarInt(this.f_302557_);
    }

    @Override
    public PacketType<ClientboundTickingStepPacket> write() {
        return GamePacketTypes.f_315414_;
    }

    public void handle(ClientGamePacketListener p_309817_) {
        p_309817_.m_305194_(this);
    }
}