package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class WolfVariant {
    public static final Codec<WolfVariant> f_314617_ = RecordCodecBuilder.create(
        p_334166_ -> p_334166_.group(
                    ResourceLocation.CODEC.fieldOf("wild_texture").forGetter(p_328425_ -> p_328425_.f_315652_),
                    ResourceLocation.CODEC.fieldOf("tame_texture").forGetter(p_332357_ -> p_332357_.f_314029_),
                    ResourceLocation.CODEC.fieldOf("angry_texture").forGetter(p_331507_ -> p_331507_.f_316040_),
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(WolfVariant::m_320548_)
                )
                .apply(p_334166_, WolfVariant::new)
    );
    public static final Codec<Holder<WolfVariant>> f_316262_ = RegistryFileCodec.create(Registries.f_317086_, f_314617_);
    private final ResourceLocation f_315652_;
    private final ResourceLocation f_314029_;
    private final ResourceLocation f_316040_;
    private final ResourceLocation f_314959_;
    private final ResourceLocation f_314931_;
    private final ResourceLocation f_314025_;
    private final HolderSet<Biome> f_314141_;

    public WolfVariant(ResourceLocation p_329809_, ResourceLocation p_332773_, ResourceLocation p_332065_, HolderSet<Biome> p_330560_) {
        this.f_315652_ = p_329809_;
        this.f_314959_ = m_319020_(p_329809_);
        this.f_314029_ = p_332773_;
        this.f_314931_ = m_319020_(p_332773_);
        this.f_316040_ = p_332065_;
        this.f_314025_ = m_319020_(p_332065_);
        this.f_314141_ = p_330560_;
    }

    private static ResourceLocation m_319020_(ResourceLocation p_335830_) {
        return p_335830_.withPath(p_331806_ -> "textures/" + p_331806_ + ".png");
    }

    public ResourceLocation m_321466_() {
        return this.f_314959_;
    }

    public ResourceLocation m_323730_() {
        return this.f_314931_;
    }

    public ResourceLocation m_323442_() {
        return this.f_314025_;
    }

    public HolderSet<Biome> m_320548_() {
        return this.f_314141_;
    }

    @Override
    public boolean equals(Object p_329082_) {
        if (p_329082_ == this) {
            return true;
        } else {
            return !(p_329082_ instanceof WolfVariant wolfvariant)
                ? false
                : Objects.equals(this.f_315652_, wolfvariant.f_315652_)
                    && Objects.equals(this.f_314029_, wolfvariant.f_314029_)
                    && Objects.equals(this.f_316040_, wolfvariant.f_316040_)
                    && Objects.equals(this.f_314141_, wolfvariant.f_314141_);
        }
    }

    @Override
    public int hashCode() {
        int i = 1;
        i = 31 * i + this.f_315652_.hashCode();
        i = 31 * i + this.f_314029_.hashCode();
        i = 31 * i + this.f_316040_.hashCode();
        return 31 * i + this.f_314141_.hashCode();
    }
}