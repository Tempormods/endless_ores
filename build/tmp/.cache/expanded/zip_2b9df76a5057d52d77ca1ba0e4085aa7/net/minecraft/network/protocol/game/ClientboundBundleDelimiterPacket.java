package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundleDelimiterPacket;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundleDelimiterPacket extends BundleDelimiterPacket<ClientGamePacketListener> {
    @Override
    public PacketType<ClientboundBundleDelimiterPacket> write() {
        return GamePacketTypes.f_314524_;
    }
}