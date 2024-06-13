package net.minecraft.world.item.armortrim;

import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimMaterials {
    public static final ResourceKey<TrimMaterial> QUARTZ = registryKey("quartz");
    public static final ResourceKey<TrimMaterial> IRON = registryKey("iron");
    public static final ResourceKey<TrimMaterial> NETHERITE = registryKey("netherite");
    public static final ResourceKey<TrimMaterial> REDSTONE = registryKey("redstone");
    public static final ResourceKey<TrimMaterial> COPPER = registryKey("copper");
    public static final ResourceKey<TrimMaterial> GOLD = registryKey("gold");
    public static final ResourceKey<TrimMaterial> EMERALD = registryKey("emerald");
    public static final ResourceKey<TrimMaterial> DIAMOND = registryKey("diamond");
    public static final ResourceKey<TrimMaterial> LAPIS = registryKey("lapis");
    public static final ResourceKey<TrimMaterial> AMETHYST = registryKey("amethyst");

    public static void bootstrap(BootstrapContext<TrimMaterial> p_329047_) {
        register(p_329047_, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1F);
        register(p_329047_, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(15527148), 0.2F, Map.of(ArmorMaterials.f_40455_, "iron_darker"));
        register(p_329047_, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3F, Map.of(ArmorMaterials.f_40459_, "netherite_darker"));
        register(p_329047_, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4F);
        register(p_329047_, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5F);
        register(p_329047_, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6F, Map.of(ArmorMaterials.f_40456_, "gold_darker"));
        register(p_329047_, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7F);
        register(p_329047_, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8F, Map.of(ArmorMaterials.f_40457_, "diamond_darker"));
        register(p_329047_, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9F);
        register(p_329047_, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0F);
    }

    public static Optional<Holder.Reference<TrimMaterial>> getFromIngredient(HolderLookup.Provider p_331522_, ItemStack pIngredient) {
        return p_331522_.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().filter(p_266876_ -> pIngredient.is(p_266876_.value().ingredient())).findFirst();
    }

    private static void register(
        BootstrapContext<TrimMaterial> p_335680_, ResourceKey<TrimMaterial> pMaterialKey, Item pIngredient, Style pStyle, float pItemModelIndex
    ) {
        register(p_335680_, pMaterialKey, pIngredient, pStyle, pItemModelIndex, Map.of());
    }

    private static void register(
        BootstrapContext<TrimMaterial> p_330018_,
        ResourceKey<TrimMaterial> pMaterialKey,
        Item pIngredient,
        Style pStyle,
        float pItemModelIndex,
        Map<Holder<ArmorMaterial>, String> pOverrideArmorMaterials
    ) {
        TrimMaterial trimmaterial = TrimMaterial.create(
            pMaterialKey.location().getPath(),
            pIngredient,
            pItemModelIndex,
            Component.translatable(Util.makeDescriptionId("trim_material", pMaterialKey.location())).withStyle(pStyle),
            pOverrideArmorMaterials
        );
        p_330018_.m_321889_(pMaterialKey, trimmaterial);
    }

    private static ResourceKey<TrimMaterial> registryKey(String pKey) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(pKey));
    }
}