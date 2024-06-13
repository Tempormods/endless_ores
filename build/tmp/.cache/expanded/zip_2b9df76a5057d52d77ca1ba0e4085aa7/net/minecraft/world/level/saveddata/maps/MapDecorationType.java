package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecorationType(ResourceLocation f_315068_, boolean f_315585_, int f_315023_, boolean f_314518_, boolean f_314463_) {
    public static final int f_316633_ = -1;
    public static final Codec<Holder<MapDecorationType>> f_316487_ = BuiltInRegistries.f_315353_.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> f_315859_ = ByteBufCodecs.m_322636_(Registries.f_313969_);

    public boolean m_324003_() {
        return this.f_315023_ != -1;
    }
}