package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;

public record ArmorMaterial(
    Map<ArmorItem.Type, Integer> f_316203_,
    int f_313926_,
    Holder<SoundEvent> f_313996_,
    Supplier<Ingredient> f_315867_,
    List<ArmorMaterial.Layer> f_315892_,
    float f_316002_,
    float f_317001_
) {
    public static final Codec<Holder<ArmorMaterial>> f_314133_ = BuiltInRegistries.f_315942_.holderByNameCodec();

    public int m_323068_(ArmorItem.Type p_328867_) {
        return this.f_316203_.getOrDefault(p_328867_, 0);
    }

    public static final class Layer {
        private final ResourceLocation f_317138_;
        private final String f_315638_;
        private final boolean f_315668_;
        private final ResourceLocation f_314936_;
        private final ResourceLocation f_315722_;

        public Layer(ResourceLocation p_328120_, String p_329928_, boolean p_329101_) {
            this.f_317138_ = p_328120_;
            this.f_315638_ = p_329928_;
            this.f_315668_ = p_329101_;
            this.f_314936_ = this.m_320920_(true);
            this.f_315722_ = this.m_320920_(false);
        }

        public Layer(ResourceLocation p_330953_) {
            this(p_330953_, "", false);
        }

        private ResourceLocation m_320920_(boolean p_329608_) {
            return this.f_317138_
                .withPath(p_330916_ -> "textures/models/armor/" + this.f_317138_.getPath() + "_layer_" + (p_329608_ ? 2 : 1) + this.f_315638_ + ".png");
        }

        public ResourceLocation m_318738_(boolean p_329648_) {
            return p_329648_ ? this.f_314936_ : this.f_315722_;
        }

        public boolean m_324910_() {
            return this.f_315668_;
        }

        public String getSuffix() {
            return this.f_315638_;
        }
    }
}
