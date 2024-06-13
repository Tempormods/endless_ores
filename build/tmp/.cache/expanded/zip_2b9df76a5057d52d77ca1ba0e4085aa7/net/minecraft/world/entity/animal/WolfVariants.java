package net.minecraft.world.entity.animal;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class WolfVariants {
    public static final ResourceKey<WolfVariant> f_316890_ = m_323798_("pale");
    public static final ResourceKey<WolfVariant> f_315388_ = m_323798_("spotted");
    public static final ResourceKey<WolfVariant> f_316488_ = m_323798_("snowy");
    public static final ResourceKey<WolfVariant> f_314751_ = m_323798_("black");
    public static final ResourceKey<WolfVariant> f_315462_ = m_323798_("ashen");
    public static final ResourceKey<WolfVariant> f_315181_ = m_323798_("rusty");
    public static final ResourceKey<WolfVariant> f_316736_ = m_323798_("woods");
    public static final ResourceKey<WolfVariant> f_315883_ = m_323798_("chestnut");
    public static final ResourceKey<WolfVariant> f_314008_ = m_323798_("striped");

    private static ResourceKey<WolfVariant> m_323798_(String p_335110_) {
        return ResourceKey.create(Registries.f_317086_, new ResourceLocation(p_335110_));
    }

    static void m_324598_(BootstrapContext<WolfVariant> p_328632_, ResourceKey<WolfVariant> p_331459_, String p_329414_, ResourceKey<Biome> p_332564_) {
        m_319176_(p_328632_, p_331459_, p_329414_, HolderSet.direct(p_328632_.m_255434_(Registries.BIOME).getOrThrow(p_332564_)));
    }

    static void m_320593_(BootstrapContext<WolfVariant> p_334941_, ResourceKey<WolfVariant> p_335312_, String p_334468_, TagKey<Biome> p_335491_) {
        m_319176_(p_334941_, p_335312_, p_334468_, p_334941_.m_255434_(Registries.BIOME).getOrThrow(p_335491_));
    }

    static void m_319176_(BootstrapContext<WolfVariant> p_332159_, ResourceKey<WolfVariant> p_330575_, String p_333153_, HolderSet<Biome> p_334914_) {
        ResourceLocation resourcelocation = new ResourceLocation("entity/wolf/" + p_333153_);
        ResourceLocation resourcelocation1 = new ResourceLocation("entity/wolf/" + p_333153_ + "_tame");
        ResourceLocation resourcelocation2 = new ResourceLocation("entity/wolf/" + p_333153_ + "_angry");
        p_332159_.m_321889_(p_330575_, new WolfVariant(resourcelocation, resourcelocation1, resourcelocation2, p_334914_));
    }

    public static Holder<WolfVariant> m_320536_(RegistryAccess p_330241_, Holder<Biome> p_331959_) {
        Registry<WolfVariant> registry = p_330241_.registryOrThrow(Registries.f_317086_);
        return registry.holders()
            .filter(p_329793_ -> p_329793_.value().m_320548_().contains(p_331959_))
            .findFirst()
            .orElse(registry.getHolderOrThrow(f_316890_));
    }

    public static void m_322012_(BootstrapContext<WolfVariant> p_332045_) {
        m_324598_(p_332045_, f_316890_, "wolf", Biomes.TAIGA);
        m_320593_(p_332045_, f_315388_, "wolf_spotted", BiomeTags.IS_SAVANNA);
        m_324598_(p_332045_, f_316488_, "wolf_snowy", Biomes.GROVE);
        m_324598_(p_332045_, f_314751_, "wolf_black", Biomes.OLD_GROWTH_PINE_TAIGA);
        m_324598_(p_332045_, f_315462_, "wolf_ashen", Biomes.SNOWY_TAIGA);
        m_320593_(p_332045_, f_315181_, "wolf_rusty", BiomeTags.IS_JUNGLE);
        m_324598_(p_332045_, f_316736_, "wolf_woods", Biomes.FOREST);
        m_324598_(p_332045_, f_315883_, "wolf_chestnut", Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        m_320593_(p_332045_, f_314008_, "wolf_striped", BiomeTags.IS_BADLANDS);
    }
}