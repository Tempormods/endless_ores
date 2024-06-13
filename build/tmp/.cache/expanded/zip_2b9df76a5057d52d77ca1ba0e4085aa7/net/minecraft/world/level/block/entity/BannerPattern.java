package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public record BannerPattern(ResourceLocation f_316056_, String f_315615_) {
    public static final Codec<BannerPattern> f_316004_ = RecordCodecBuilder.create(
        p_327278_ -> p_327278_.group(
                    ResourceLocation.CODEC.fieldOf("asset_id").forGetter(BannerPattern::f_316056_),
                    Codec.STRING.fieldOf("translation_key").forGetter(BannerPattern::f_315615_)
                )
                .apply(p_327278_, BannerPattern::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BannerPattern> f_316459_ = StreamCodec.m_320349_(
        ResourceLocation.f_314488_, BannerPattern::f_316056_, ByteBufCodecs.f_315450_, BannerPattern::f_315615_, BannerPattern::new
    );
    public static final Codec<Holder<BannerPattern>> f_316089_ = RegistryFileCodec.create(Registries.BANNER_PATTERN, f_316004_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BannerPattern>> f_316922_ = ByteBufCodecs.m_321333_(Registries.BANNER_PATTERN, f_316459_);
}