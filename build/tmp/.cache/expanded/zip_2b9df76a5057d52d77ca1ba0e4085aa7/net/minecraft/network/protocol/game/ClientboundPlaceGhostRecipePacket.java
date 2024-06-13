package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ClientboundPlaceGhostRecipePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlaceGhostRecipePacket> f_315813_ = Packet.m_319422_(
        ClientboundPlaceGhostRecipePacket::m_132656_, ClientboundPlaceGhostRecipePacket::new
    );
    private final int containerId;
    private final ResourceLocation recipe;

    public ClientboundPlaceGhostRecipePacket(int pContainerId, RecipeHolder<?> pRecipe) {
        this.containerId = pContainerId;
        this.recipe = pRecipe.id();
    }

    private ClientboundPlaceGhostRecipePacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readByte();
        this.recipe = pBuffer.readResourceLocation();
    }

    private void m_132656_(FriendlyByteBuf pByteBuf) {
        pByteBuf.writeByte(this.containerId);
        pByteBuf.writeResourceLocation(this.recipe);
    }

    @Override
    public PacketType<ClientboundPlaceGhostRecipePacket> write() {
        return GamePacketTypes.f_314098_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlaceRecipe(this);
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }

    public int getContainerId() {
        return this.containerId;
    }
}