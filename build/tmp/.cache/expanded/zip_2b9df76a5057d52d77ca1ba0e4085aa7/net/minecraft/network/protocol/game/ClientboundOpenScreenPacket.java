package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenScreenPacket> f_314162_ = StreamCodec.m_321516_(
        ByteBufCodecs.f_316730_,
        ClientboundOpenScreenPacket::getContainerId,
        ByteBufCodecs.m_320159_(Registries.MENU),
        ClientboundOpenScreenPacket::getType,
        ComponentSerialization.f_316335_,
        ClientboundOpenScreenPacket::getTitle,
        ClientboundOpenScreenPacket::new
    );
    private final int containerId;
    private final MenuType<?> type;
    private final Component title;

    public ClientboundOpenScreenPacket(int pContainerId, MenuType<?> pMenuType, Component pTitle) {
        this.containerId = pContainerId;
        this.type = pMenuType;
        this.title = pTitle;
    }

    @Override
    public PacketType<ClientboundOpenScreenPacket> write() {
        return GamePacketTypes.f_316807_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleOpenScreen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MenuType<?> getType() {
        return this.type;
    }

    public Component getTitle() {
        return this.title;
    }
}