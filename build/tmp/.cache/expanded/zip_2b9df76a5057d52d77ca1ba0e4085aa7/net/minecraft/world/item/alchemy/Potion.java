package net.minecraft.world.item.alchemy;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

/**
 * Defines a type of potion in the game. These are used to associate one or more effects with items such as the bottled
 * potion or the tipped arrows.
 */
public class Potion implements FeatureElement {
    @Nullable
    private final String name;
    private final List<MobEffectInstance> effects;
    private FeatureFlagSet f_315259_ = FeatureFlags.VANILLA_SET;

    public Potion(MobEffectInstance... pEffects) {
        this(null, pEffects);
    }

    public Potion(@Nullable String pName, MobEffectInstance... pEffects) {
        this.name = pName;
        this.effects = List.of(pEffects);
    }

    public Potion m_319158_(FeatureFlag... p_331264_) {
        this.f_315259_ = FeatureFlags.REGISTRY.subset(p_331264_);
        return this;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.f_315259_;
    }

    public static String getName(Optional<Holder<Potion>> p_332314_, String pPrefix) {
        if (p_332314_.isPresent()) {
            String s = p_332314_.get().value().name;
            if (s != null) {
                return pPrefix + s;
            }
        }

        String s1 = p_332314_.flatMap(Holder::unwrapKey).map(p_329074_ -> p_329074_.location().getPath()).orElse("empty");
        return pPrefix + s1;
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffects() {
        if (!this.effects.isEmpty()) {
            for (MobEffectInstance mobeffectinstance : this.effects) {
                if (mobeffectinstance.getEffect().value().isInstantenous()) {
                    return true;
                }
            }
        }

        return false;
    }
}