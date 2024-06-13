package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public record TrimMaterial(String assetName, Holder<Item> ingredient, float itemModelIndex, Map<Holder<ArmorMaterial>, String> overrideArmorMaterials, Component description) {
    public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
        p_327191_ -> p_327191_.group(
                    ExtraCodecs.RESOURCE_PATH_CODEC.fieldOf("asset_name").forGetter(TrimMaterial::assetName),
                    RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(TrimMaterial::ingredient),
                    Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::itemModelIndex),
                    Codec.unboundedMap(ArmorMaterial.f_314133_, Codec.STRING)
                        .optionalFieldOf("override_armor_materials", Map.of())
                        .forGetter(TrimMaterial::overrideArmorMaterials),
                    ComponentSerialization.f_303288_.fieldOf("description").forGetter(TrimMaterial::description)
                )
                .apply(p_327191_, TrimMaterial::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> f_316229_ = StreamCodec.m_319894_(
        ByteBufCodecs.f_315450_,
        TrimMaterial::assetName,
        ByteBufCodecs.m_322636_(Registries.ITEM),
        TrimMaterial::ingredient,
        ByteBufCodecs.f_314734_,
        TrimMaterial::itemModelIndex,
        ByteBufCodecs.m_322136_(Object2ObjectOpenHashMap::new, ByteBufCodecs.m_322636_(Registries.f_315643_), ByteBufCodecs.f_315450_),
        TrimMaterial::overrideArmorMaterials,
        ComponentSerialization.f_315335_,
        TrimMaterial::description,
        TrimMaterial::new
    );
    public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimMaterial>> f_314439_ = ByteBufCodecs.m_321333_(Registries.TRIM_MATERIAL, f_316229_);

    public static TrimMaterial create(String pAssetName, Item pIngredient, float pItemModelIndex, Component pDescription, Map<Holder<ArmorMaterial>, String> pOverrideArmorMaterials) {
        return new TrimMaterial(pAssetName, BuiltInRegistries.ITEM.wrapAsHolder(pIngredient), pItemModelIndex, pOverrideArmorMaterials, pDescription);
    }
}