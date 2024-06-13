package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookSeenRecipePacket> f_317003_ = Packet.m_319422_(
        ServerboundRecipeBookSeenRecipePacket::m_134391_, ServerboundRecipeBookSeenRecipePacket::new
    );
    private final ResourceLocation recipe;

    public ServerboundRecipeBookSeenRecipePacket(RecipeHolder<?> p_298515_) {
        this.recipe = p_298515_.id();
    }

    private ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf pBuffer) {
        this.recipe = pBuffer.readResourceLocation();
    }

    private void m_134391_(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(this.recipe);
    }

    @Override
    public PacketType<ServerboundRecipeBookSeenRecipePacket> write() {
        return GamePacketTypes.f_315800_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleRecipeBookSeenRecipePacket(this);
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }
}