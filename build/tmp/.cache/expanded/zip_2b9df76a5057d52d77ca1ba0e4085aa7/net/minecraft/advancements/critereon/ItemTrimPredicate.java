package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;

public record ItemTrimPredicate(Optional<HolderSet<TrimMaterial>> f_316094_, Optional<HolderSet<TrimPattern>> f_314755_)
    implements SingleComponentItemPredicate<ArmorTrim> {
    public static final Codec<ItemTrimPredicate> f_315217_ = RecordCodecBuilder.create(
        p_334329_ -> p_334329_.group(
                    RegistryCodecs.homogeneousList(Registries.TRIM_MATERIAL).optionalFieldOf("material").forGetter(ItemTrimPredicate::f_316094_),
                    RegistryCodecs.homogeneousList(Registries.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(ItemTrimPredicate::f_314755_)
                )
                .apply(p_334329_, ItemTrimPredicate::new)
    );

    @Override
    public DataComponentType<ArmorTrim> m_318698_() {
        return DataComponents.f_315199_;
    }

    public boolean m_318913_(ItemStack p_336368_, ArmorTrim p_330276_) {
        return this.f_316094_.isPresent() && !this.f_316094_.get().contains(p_330276_.material())
            ? false
            : !this.f_314755_.isPresent() || this.f_314755_.get().contains(p_330276_.pattern());
    }
}