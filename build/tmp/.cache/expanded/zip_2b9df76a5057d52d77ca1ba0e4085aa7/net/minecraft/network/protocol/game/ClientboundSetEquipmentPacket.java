package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEquipmentPacket> f_314063_ = Packet.m_319422_(
        ClientboundSetEquipmentPacket::m_133211_, ClientboundSetEquipmentPacket::new
    );
    private static final byte CONTINUE_MASK = -128;
    private final int entity;
    private final List<Pair<EquipmentSlot, ItemStack>> slots;

    public ClientboundSetEquipmentPacket(int pEntity, List<Pair<EquipmentSlot, ItemStack>> pSlots) {
        this.entity = pEntity;
        this.slots = pSlots;
    }

    private ClientboundSetEquipmentPacket(RegistryFriendlyByteBuf p_329444_) {
        this.entity = p_329444_.readVarInt();
        EquipmentSlot[] aequipmentslot = EquipmentSlot.values();
        this.slots = Lists.newArrayList();

        int i;
        do {
            i = p_329444_.readByte();
            EquipmentSlot equipmentslot = aequipmentslot[i & 127];
            ItemStack itemstack = ItemStack.f_314979_.m_318688_(p_329444_);
            this.slots.add(Pair.of(equipmentslot, itemstack));
        } while ((i & -128) != 0);
    }

    private void m_133211_(RegistryFriendlyByteBuf p_328455_) {
        p_328455_.writeVarInt(this.entity);
        int i = this.slots.size();

        for (int j = 0; j < i; j++) {
            Pair<EquipmentSlot, ItemStack> pair = this.slots.get(j);
            EquipmentSlot equipmentslot = pair.getFirst();
            boolean flag = j != i - 1;
            int k = equipmentslot.ordinal();
            p_328455_.writeByte(flag ? k | -128 : k);
            ItemStack.f_314979_.m_318638_(p_328455_, pair.getSecond());
        }
    }

    @Override
    public PacketType<ClientboundSetEquipmentPacket> write() {
        return GamePacketTypes.f_314487_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetEquipment(this);
    }

    public int getEntity() {
        return this.entity;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
        return this.slots;
    }
}