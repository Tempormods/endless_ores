package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPlayerCombatEnterPacket implements Packet<ClientGamePacketListener> {
    public static final ClientboundPlayerCombatEnterPacket f_314108_ = new ClientboundPlayerCombatEnterPacket();
    public static final StreamCodec<ByteBuf, ClientboundPlayerCombatEnterPacket> f_314465_ = StreamCodec.m_323136_(f_314108_);

    private ClientboundPlayerCombatEnterPacket() {
    }

    @Override
    public PacketType<ClientboundPlayerCombatEnterPacket> write() {
        return GamePacketTypes.f_315032_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlayerCombatEnter(this);
    }
}