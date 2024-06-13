package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStatePacket(float f_303125_, boolean f_302605_) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTickingStatePacket> f_316389_ = Packet.m_319422_(
        ClientboundTickingStatePacket::m_307964_, ClientboundTickingStatePacket::new
    );

    private ClientboundTickingStatePacket(FriendlyByteBuf p_312542_) {
        this(p_312542_.readFloat(), p_312542_.readBoolean());
    }

    public static ClientboundTickingStatePacket m_307319_(TickRateManager p_312239_) {
        return new ClientboundTickingStatePacket(p_312239_.m_306179_(), p_312239_.m_306363_());
    }

    private void m_307964_(FriendlyByteBuf p_312400_) {
        p_312400_.writeFloat(this.f_303125_);
        p_312400_.writeBoolean(this.f_302605_);
    }

    @Override
    public PacketType<ClientboundTickingStatePacket> write() {
        return GamePacketTypes.f_313968_;
    }

    public void handle(ClientGamePacketListener p_311641_) {
        p_311641_.m_307789_(this);
    }
}