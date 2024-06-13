package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContainerSetContentPacket> f_315707_ = Packet.m_319422_(
        ClientboundContainerSetContentPacket::m_131955_, ClientboundContainerSetContentPacket::new
    );
    private final int containerId;
    private final int stateId;
    private final List<ItemStack> items;
    private final ItemStack carriedItem;

    public ClientboundContainerSetContentPacket(int pContainerId, int pStateId, NonNullList<ItemStack> pItems, ItemStack pCarriedItem) {
        this.containerId = pContainerId;
        this.stateId = pStateId;
        this.items = NonNullList.withSize(pItems.size(), ItemStack.EMPTY);

        for (int i = 0; i < pItems.size(); i++) {
            this.items.set(i, pItems.get(i).copy());
        }

        this.carriedItem = pCarriedItem.copy();
    }

    private ClientboundContainerSetContentPacket(RegistryFriendlyByteBuf p_332879_) {
        this.containerId = p_332879_.readUnsignedByte();
        this.stateId = p_332879_.readVarInt();
        this.items = ItemStack.f_315592_.m_318688_(p_332879_);
        this.carriedItem = ItemStack.f_314979_.m_318688_(p_332879_);
    }

    private void m_131955_(RegistryFriendlyByteBuf p_330970_) {
        p_330970_.writeByte(this.containerId);
        p_330970_.writeVarInt(this.stateId);
        ItemStack.f_315592_.m_318638_(p_330970_, this.items);
        ItemStack.f_314979_.m_318638_(p_330970_, this.carriedItem);
    }

    @Override
    public PacketType<ClientboundContainerSetContentPacket> write() {
        return GamePacketTypes.f_315600_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleContainerContent(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public int getStateId() {
        return this.stateId;
    }
}