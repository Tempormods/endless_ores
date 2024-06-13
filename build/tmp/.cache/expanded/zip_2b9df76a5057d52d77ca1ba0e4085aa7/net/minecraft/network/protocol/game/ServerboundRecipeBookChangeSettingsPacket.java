package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.RecipeBookType;

public class ServerboundRecipeBookChangeSettingsPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookChangeSettingsPacket> f_316483_ = Packet.m_319422_(
        ServerboundRecipeBookChangeSettingsPacket::m_134376_, ServerboundRecipeBookChangeSettingsPacket::new
    );
    private final RecipeBookType bookType;
    private final boolean isOpen;
    private final boolean isFiltering;

    public ServerboundRecipeBookChangeSettingsPacket(RecipeBookType pBookType, boolean pIsOpen, boolean pIsFiltering) {
        this.bookType = pBookType;
        this.isOpen = pIsOpen;
        this.isFiltering = pIsFiltering;
    }

    private ServerboundRecipeBookChangeSettingsPacket(FriendlyByteBuf pBuffer) {
        this.bookType = pBuffer.readEnum(RecipeBookType.class);
        this.isOpen = pBuffer.readBoolean();
        this.isFiltering = pBuffer.readBoolean();
    }

    private void m_134376_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.bookType);
        pBuffer.writeBoolean(this.isOpen);
        pBuffer.writeBoolean(this.isFiltering);
    }

    @Override
    public PacketType<ServerboundRecipeBookChangeSettingsPacket> write() {
        return GamePacketTypes.f_317047_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleRecipeBookChangeSettingsPacket(this);
    }

    public RecipeBookType getBookType() {
        return this.bookType;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean isFiltering() {
        return this.isFiltering;
    }
}