package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSelectAdvancementsTabPacket> f_315979_ = Packet.m_319422_(
        ClientboundSelectAdvancementsTabPacket::m_133014_, ClientboundSelectAdvancementsTabPacket::new
    );
    @Nullable
    private final ResourceLocation tab;

    public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation pTab) {
        this.tab = pTab;
    }

    private ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf pBuffer) {
        this.tab = pBuffer.readNullable(FriendlyByteBuf::readResourceLocation);
    }

    private void m_133014_(FriendlyByteBuf pBuffer) {
        pBuffer.m_321806_(this.tab, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public PacketType<ClientboundSelectAdvancementsTabPacket> write() {
        return GamePacketTypes.f_315708_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSelectAdvancementsTab(this);
    }

    @Nullable
    public ResourceLocation getTab() {
        return this.tab;
    }
}