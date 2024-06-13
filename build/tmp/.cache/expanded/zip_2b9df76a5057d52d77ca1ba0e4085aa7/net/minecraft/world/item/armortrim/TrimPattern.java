package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record TrimPattern(ResourceLocation assetId, Holder<Item> templateItem, Component description, boolean decal) {
    public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(
        p_309249_ -> p_309249_.group(
                    ResourceLocation.CODEC.fieldOf("asset_id").forGetter(TrimPattern::assetId),
                    RegistryFixedCodec.create(Registries.ITEM).fieldOf("template_item").forGetter(TrimPattern::templateItem),
                    ComponentSerialization.f_303288_.fieldOf("description").forGetter(TrimPattern::description),
                    Codec.BOOL.fieldOf("decal").orElse(false).forGetter(TrimPattern::decal)
                )
                .apply(p_309249_, TrimPattern::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TrimPattern> f_315843_ = StreamCodec.m_319980_(
        ResourceLocation.f_314488_,
        TrimPattern::assetId,
        ByteBufCodecs.m_322636_(Registries.ITEM),
        TrimPattern::templateItem,
        ComponentSerialization.f_315335_,
        TrimPattern::description,
        ByteBufCodecs.f_315514_,
        TrimPattern::decal,
        TrimPattern::new
    );
    public static final Codec<Holder<TrimPattern>> CODEC = RegistryFileCodec.create(Registries.TRIM_PATTERN, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimPattern>> f_313913_ = ByteBufCodecs.m_321333_(Registries.TRIM_PATTERN, f_315843_);

    public Component copyWithStyle(Holder<TrimMaterial> pTrimMaterial) {
        return this.description.copy().withStyle(pTrimMaterial.value().description().getStyle());
    }
}