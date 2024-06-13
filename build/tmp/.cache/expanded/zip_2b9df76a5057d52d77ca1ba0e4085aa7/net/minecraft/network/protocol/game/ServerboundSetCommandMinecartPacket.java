package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;

public class ServerboundSetCommandMinecartPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSetCommandMinecartPacket> f_313905_ = Packet.m_319422_(
        ServerboundSetCommandMinecartPacket::m_134546_, ServerboundSetCommandMinecartPacket::new
    );
    private final int entity;
    private final String command;
    private final boolean trackOutput;

    public ServerboundSetCommandMinecartPacket(int pEntity, String pCommand, boolean pTrackOutput) {
        this.entity = pEntity;
        this.command = pCommand;
        this.trackOutput = pTrackOutput;
    }

    private ServerboundSetCommandMinecartPacket(FriendlyByteBuf pBuffer) {
        this.entity = pBuffer.readVarInt();
        this.command = pBuffer.readUtf();
        this.trackOutput = pBuffer.readBoolean();
    }

    private void m_134546_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.entity);
        pBuffer.writeUtf(this.command);
        pBuffer.writeBoolean(this.trackOutput);
    }

    @Override
    public PacketType<ServerboundSetCommandMinecartPacket> write() {
        return GamePacketTypes.f_315305_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSetCommandMinecart(this);
    }

    @Nullable
    public BaseCommandBlock getCommandBlock(Level pLevel) {
        Entity entity = pLevel.getEntity(this.entity);
        return entity instanceof MinecartCommandBlock ? ((MinecartCommandBlock)entity).getCommandBlock() : null;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }
}