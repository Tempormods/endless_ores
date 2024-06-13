package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public abstract class ItemEnchantmentsPredicate implements SingleComponentItemPredicate<ItemEnchantments> {
    private final List<EnchantmentPredicate> f_314663_;

    protected ItemEnchantmentsPredicate(List<EnchantmentPredicate> p_327909_) {
        this.f_314663_ = p_327909_;
    }

    public static <T extends ItemEnchantmentsPredicate> Codec<T> m_325020_(Function<List<EnchantmentPredicate>, T> p_330847_) {
        return EnchantmentPredicate.CODEC.listOf().xmap(p_330847_, ItemEnchantmentsPredicate::m_321691_);
    }

    protected List<EnchantmentPredicate> m_321691_() {
        return this.f_314663_;
    }

    public boolean m_318913_(ItemStack p_332137_, ItemEnchantments p_331461_) {
        for (EnchantmentPredicate enchantmentpredicate : this.f_314663_) {
            if (!enchantmentpredicate.containedIn(p_331461_)) {
                return false;
            }
        }

        return true;
    }

    public static ItemEnchantmentsPredicate.Enchantments m_319224_(List<EnchantmentPredicate> p_334509_) {
        return new ItemEnchantmentsPredicate.Enchantments(p_334509_);
    }

    public static ItemEnchantmentsPredicate.StoredEnchantments m_322731_(List<EnchantmentPredicate> p_331491_) {
        return new ItemEnchantmentsPredicate.StoredEnchantments(p_331491_);
    }

    public static class Enchantments extends ItemEnchantmentsPredicate {
        public static final Codec<ItemEnchantmentsPredicate.Enchantments> f_316379_ = m_325020_(ItemEnchantmentsPredicate.Enchantments::new);

        protected Enchantments(List<EnchantmentPredicate> p_333770_) {
            super(p_333770_);
        }

        @Override
        public DataComponentType<ItemEnchantments> m_318698_() {
            return DataComponents.f_314658_;
        }
    }

    public static class StoredEnchantments extends ItemEnchantmentsPredicate {
        public static final Codec<ItemEnchantmentsPredicate.StoredEnchantments> f_316422_ = m_325020_(ItemEnchantmentsPredicate.StoredEnchantments::new);

        protected StoredEnchantments(List<EnchantmentPredicate> p_330178_) {
            super(p_330178_);
        }

        @Override
        public DataComponentType<ItemEnchantments> m_318698_() {
            return DataComponents.f_314515_;
        }
    }
}