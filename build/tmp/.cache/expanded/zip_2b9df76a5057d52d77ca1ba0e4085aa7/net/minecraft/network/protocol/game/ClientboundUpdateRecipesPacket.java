package net.minecraft.network.protocol.game;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateRecipesPacket> f_315247_ = StreamCodec.m_322204_(
        RecipeHolder.f_314856_.m_321801_(ByteBufCodecs.m_324765_()), p_326127_ -> p_326127_.recipes, ClientboundUpdateRecipesPacket::new
    );
    private final List<RecipeHolder<?>> recipes;

    public ClientboundUpdateRecipesPacket(Collection<RecipeHolder<?>> pRecipes) {
        this.recipes = List.copyOf(pRecipes);
    }

    @Override
    public PacketType<ClientboundUpdateRecipesPacket> write() {
        return GamePacketTypes.f_316403_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleUpdateRecipes(this);
    }

    public List<RecipeHolder<?>> getRecipes() {
        return this.recipes;
    }
}