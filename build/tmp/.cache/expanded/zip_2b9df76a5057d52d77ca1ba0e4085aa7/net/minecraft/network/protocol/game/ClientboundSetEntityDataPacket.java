package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEntityDataPacket> f_316233_ = Packet.m_319422_(
        ClientboundSetEntityDataPacket::m_133157_, ClientboundSetEntityDataPacket::new
    );
    public static final int EOF_MARKER = 255;

    private ClientboundSetEntityDataPacket(RegistryFriendlyByteBuf p_335656_) {
        this(p_335656_.readVarInt(), unpack(p_335656_));
    }

    private static void pack(List<SynchedEntityData.DataValue<?>> pValues, RegistryFriendlyByteBuf p_331850_) {
        for (SynchedEntityData.DataValue<?> datavalue : pValues) {
            datavalue.write(p_331850_);
        }

        p_331850_.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(RegistryFriendlyByteBuf p_330932_) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

        int i;
        while ((i = p_330932_.readUnsignedByte()) != 255) {
            list.add(SynchedEntityData.DataValue.read(p_330932_, i));
        }

        return list;
    }

    private void m_133157_(RegistryFriendlyByteBuf p_333245_) {
        p_333245_.writeVarInt(this.id);
        pack(this.packedItems, p_333245_);
    }

    @Override
    public PacketType<ClientboundSetEntityDataPacket> write() {
        return GamePacketTypes.f_314449_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetEntityData(this);
    }
}