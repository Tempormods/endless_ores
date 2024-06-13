package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public record ServerboundSetCreativeModeSlotPacket(short slotNum, ItemStack itemStack) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetCreativeModeSlotPacket> f_316304_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_315014_,
        ServerboundSetCreativeModeSlotPacket::slotNum,
        ItemStack.m_319263_(ItemStack.f_314979_),
        ServerboundSetCreativeModeSlotPacket::itemStack,
        ServerboundSetCreativeModeSlotPacket::new
    );

    public ServerboundSetCreativeModeSlotPacket(int pSlotNum, ItemStack pItemStack) {
        this((short)pSlotNum, pItemStack);
    }

    @Override
    public PacketType<ServerboundSetCreativeModeSlotPacket> write() {
        return GamePacketTypes.f_316927_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSetCreativeModeSlot(this);
    }
}