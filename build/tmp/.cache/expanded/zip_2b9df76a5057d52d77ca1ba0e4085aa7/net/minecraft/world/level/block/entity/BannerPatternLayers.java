package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import org.slf4j.Logger;

public record BannerPatternLayers(List<BannerPatternLayers.Layer> f_315710_) {
    static final Logger f_314019_ = LogUtils.getLogger();
    public static final BannerPatternLayers f_316086_ = new BannerPatternLayers(List.of());
    public static final Codec<BannerPatternLayers> f_315309_ = BannerPatternLayers.Layer.f_315366_
        .listOf()
        .xmap(BannerPatternLayers::new, BannerPatternLayers::f_315710_);
    public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers> f_316168_ = BannerPatternLayers.Layer.f_316132_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(BannerPatternLayers::new, BannerPatternLayers::f_315710_);

    public BannerPatternLayers m_320009_() {
        return new BannerPatternLayers(List.copyOf(this.f_315710_.subList(0, this.f_315710_.size() - 1)));
    }

    public static class Builder {
        private final ImmutableList.Builder<BannerPatternLayers.Layer> f_316935_ = ImmutableList.builder();

        @Deprecated
        public BannerPatternLayers.Builder m_318679_(HolderGetter<BannerPattern> p_335943_, ResourceKey<BannerPattern> p_334059_, DyeColor p_331295_) {
            Optional<Holder.Reference<BannerPattern>> optional = p_335943_.m_254926_(p_334059_);
            if (optional.isEmpty()) {
                BannerPatternLayers.f_314019_.warn("Unable to find banner pattern with id: '{}'", p_334059_.location());
                return this;
            } else {
                return this.m_318746_(optional.get(), p_331295_);
            }
        }

        public BannerPatternLayers.Builder m_318746_(Holder<BannerPattern> p_333687_, DyeColor p_331527_) {
            return this.m_319119_(new BannerPatternLayers.Layer(p_333687_, p_331527_));
        }

        public BannerPatternLayers.Builder m_319119_(BannerPatternLayers.Layer p_329666_) {
            this.f_316935_.add(p_329666_);
            return this;
        }

        public BannerPatternLayers.Builder m_322919_(BannerPatternLayers p_335609_) {
            this.f_316935_.addAll(p_335609_.f_315710_);
            return this;
        }

        public BannerPatternLayers m_319179_() {
            return new BannerPatternLayers(this.f_316935_.build());
        }
    }

    public static record Layer(Holder<BannerPattern> f_316420_, DyeColor f_316009_) {
        public static final Codec<BannerPatternLayers.Layer> f_315366_ = RecordCodecBuilder.create(
            p_332626_ -> p_332626_.group(
                        BannerPattern.f_316089_.fieldOf("pattern").forGetter(BannerPatternLayers.Layer::f_316420_),
                        DyeColor.CODEC.fieldOf("color").forGetter(BannerPatternLayers.Layer::f_316009_)
                    )
                    .apply(p_332626_, BannerPatternLayers.Layer::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers.Layer> f_316132_ = StreamCodec.m_320349_(
            BannerPattern.f_316922_,
            BannerPatternLayers.Layer::f_316420_,
            DyeColor.f_313960_,
            BannerPatternLayers.Layer::f_316009_,
            BannerPatternLayers.Layer::new
        );

        public MutableComponent m_323334_() {
            String s = this.f_316420_.value().f_315615_();
            return Component.translatable(s + "." + this.f_316009_.getName());
        }
    }
}