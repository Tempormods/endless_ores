package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPlaceRecipePacket> f_316340_ = Packet.m_319422_(
        ServerboundPlaceRecipePacket::m_134250_, ServerboundPlaceRecipePacket::new
    );
    private final int containerId;
    private final ResourceLocation recipe;
    private final boolean shiftDown;

    public ServerboundPlaceRecipePacket(int pContainerId, RecipeHolder<?> pRecipe, boolean pShiftDown) {
        this.containerId = pContainerId;
        this.recipe = pRecipe.id();
        this.shiftDown = pShiftDown;
    }

    private ServerboundPlaceRecipePacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readByte();
        this.recipe = pBuffer.readResourceLocation();
        this.shiftDown = pBuffer.readBoolean();
    }

    private void m_134250_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.containerId);
        pBuffer.writeResourceLocation(this.recipe);
        pBuffer.writeBoolean(this.shiftDown);
    }

    @Override
    public PacketType<ServerboundPlaceRecipePacket> write() {
        return GamePacketTypes.f_315108_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handlePlaceRecipe(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }

    public boolean isShiftDown() {
        return this.shiftDown;
    }
}