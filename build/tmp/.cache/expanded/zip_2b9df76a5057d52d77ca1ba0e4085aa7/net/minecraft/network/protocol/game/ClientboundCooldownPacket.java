package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.Item;

public record ClientboundCooldownPacket(Item item, int duration) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCooldownPacket> f_315282_ = StreamCodec.m_320349_(
        ByteBufCodecs.m_320159_(Registries.ITEM),
        ClientboundCooldownPacket::item,
        ByteBufCodecs.f_316730_,
        ClientboundCooldownPacket::duration,
        ClientboundCooldownPacket::new
    );

    @Override
    public PacketType<ClientboundCooldownPacket> write() {
        return GamePacketTypes.f_316416_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleItemCooldown(this);
    }
}