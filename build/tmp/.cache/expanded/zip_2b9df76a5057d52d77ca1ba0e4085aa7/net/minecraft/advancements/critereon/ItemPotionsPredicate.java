package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public record ItemPotionsPredicate(HolderSet<Potion> f_314853_) implements SingleComponentItemPredicate<PotionContents> {
    public static final Codec<ItemPotionsPredicate> f_316881_ = RegistryCodecs.homogeneousList(Registries.POTION)
        .xmap(ItemPotionsPredicate::new, ItemPotionsPredicate::f_314853_);

    @Override
    public DataComponentType<PotionContents> m_318698_() {
        return DataComponents.f_314188_;
    }

    public boolean m_318913_(ItemStack p_331848_, PotionContents p_330228_) {
        Optional<Holder<Potion>> optional = p_330228_.f_317059_();
        return !optional.isEmpty() && this.f_314853_.contains(optional.get());
    }

    public static ItemSubPredicate m_321367_(HolderSet<Potion> p_335554_) {
        return new ItemPotionsPredicate(p_335554_);
    }
}