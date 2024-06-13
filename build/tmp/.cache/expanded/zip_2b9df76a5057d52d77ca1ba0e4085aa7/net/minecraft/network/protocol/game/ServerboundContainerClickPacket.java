package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundContainerClickPacket> f_316870_ = Packet.m_319422_(
        ServerboundContainerClickPacket::m_133960_, ServerboundContainerClickPacket::new
    );
    private static final int MAX_SLOT_COUNT = 128;
    private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<ItemStack>> f_315220_ = ByteBufCodecs.m_319874_(
        Int2ObjectOpenHashMap::new, ByteBufCodecs.f_315014_.m_323038_(Short::intValue, Integer::shortValue), ItemStack.f_314979_, 128
    );
    private final int containerId;
    private final int stateId;
    private final int slotNum;
    private final int buttonNum;
    private final ClickType clickType;
    private final ItemStack carriedItem;
    private final Int2ObjectMap<ItemStack> changedSlots;

    public ServerboundContainerClickPacket(
        int pContainerId, int pStateId, int pSlotNum, int pButtonNum, ClickType pClickType, ItemStack pCarriedItem, Int2ObjectMap<ItemStack> pChangedSlots
    ) {
        this.containerId = pContainerId;
        this.stateId = pStateId;
        this.slotNum = pSlotNum;
        this.buttonNum = pButtonNum;
        this.clickType = pClickType;
        this.carriedItem = pCarriedItem;
        this.changedSlots = Int2ObjectMaps.unmodifiable(pChangedSlots);
    }

    private ServerboundContainerClickPacket(RegistryFriendlyByteBuf p_329756_) {
        this.containerId = p_329756_.readByte();
        this.stateId = p_329756_.readVarInt();
        this.slotNum = p_329756_.readShort();
        this.buttonNum = p_329756_.readByte();
        this.clickType = p_329756_.readEnum(ClickType.class);
        this.changedSlots = Int2ObjectMaps.unmodifiable(f_315220_.m_318688_(p_329756_));
        this.carriedItem = ItemStack.f_314979_.m_318688_(p_329756_);
    }

    private void m_133960_(RegistryFriendlyByteBuf p_335657_) {
        p_335657_.writeByte(this.containerId);
        p_335657_.writeVarInt(this.stateId);
        p_335657_.writeShort(this.slotNum);
        p_335657_.writeByte(this.buttonNum);
        p_335657_.writeEnum(this.clickType);
        f_315220_.m_318638_(p_335657_, this.changedSlots);
        ItemStack.f_314979_.m_318638_(p_335657_, this.carriedItem);
    }

    @Override
    public PacketType<ServerboundContainerClickPacket> write() {
        return GamePacketTypes.f_317033_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleContainerClick(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public int getButtonNum() {
        return this.buttonNum;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return this.changedSlots;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public int getStateId() {
        return this.stateId;
    }
}