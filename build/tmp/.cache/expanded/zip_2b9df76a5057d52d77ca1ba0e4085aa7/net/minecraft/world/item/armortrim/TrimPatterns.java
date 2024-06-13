package net.minecraft.world.item.armortrim;

import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimPatterns {
    public static final ResourceKey<TrimPattern> SENTRY = registryKey("sentry");
    public static final ResourceKey<TrimPattern> DUNE = registryKey("dune");
    public static final ResourceKey<TrimPattern> COAST = registryKey("coast");
    public static final ResourceKey<TrimPattern> WILD = registryKey("wild");
    public static final ResourceKey<TrimPattern> WARD = registryKey("ward");
    public static final ResourceKey<TrimPattern> EYE = registryKey("eye");
    public static final ResourceKey<TrimPattern> VEX = registryKey("vex");
    public static final ResourceKey<TrimPattern> TIDE = registryKey("tide");
    public static final ResourceKey<TrimPattern> SNOUT = registryKey("snout");
    public static final ResourceKey<TrimPattern> RIB = registryKey("rib");
    public static final ResourceKey<TrimPattern> SPIRE = registryKey("spire");
    public static final ResourceKey<TrimPattern> WAYFINDER = registryKey("wayfinder");
    public static final ResourceKey<TrimPattern> SHAPER = registryKey("shaper");
    public static final ResourceKey<TrimPattern> SILENCE = registryKey("silence");
    public static final ResourceKey<TrimPattern> RAISER = registryKey("raiser");
    public static final ResourceKey<TrimPattern> HOST = registryKey("host");
    public static final ResourceKey<TrimPattern> f_316919_ = registryKey("flow");
    public static final ResourceKey<TrimPattern> f_317041_ = registryKey("bolt");

    public static void bootstrap(BootstrapContext<TrimPattern> p_331614_) {
        register(p_331614_, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY);
        register(p_331614_, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE);
        register(p_331614_, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST);
        register(p_331614_, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD);
        register(p_331614_, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD);
        register(p_331614_, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE);
        register(p_331614_, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX);
        register(p_331614_, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE);
        register(p_331614_, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT);
        register(p_331614_, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB);
        register(p_331614_, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE);
        register(p_331614_, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, WAYFINDER);
        register(p_331614_, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, SHAPER);
        register(p_331614_, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, SILENCE);
        register(p_331614_, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, RAISER);
        register(p_331614_, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, HOST);
    }

    public static Optional<Holder.Reference<TrimPattern>> getFromTemplate(HolderLookup.Provider p_333919_, ItemStack pTemplate) {
        return p_333919_.lookupOrThrow(Registries.TRIM_PATTERN).listElements().filter(p_266833_ -> pTemplate.is(p_266833_.value().templateItem())).findFirst();
    }

    public static void register(BootstrapContext<TrimPattern> p_333886_, Item pTemplateItem, ResourceKey<TrimPattern> pTrimPatternKey) {
        TrimPattern trimpattern = new TrimPattern(
            pTrimPatternKey.location(),
            BuiltInRegistries.ITEM.wrapAsHolder(pTemplateItem),
            Component.translatable(Util.makeDescriptionId("trim_pattern", pTrimPatternKey.location())),
            false
        );
        p_333886_.m_321889_(pTrimPatternKey, trimpattern);
    }

    private static ResourceKey<TrimPattern> registryKey(String pKey) {
        return ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation(pKey));
    }
}