package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemSubPredicates {
    public static final ItemSubPredicate.Type<ItemDamagePredicate> f_315296_ = m_324921_("damage", ItemDamagePredicate.f_314810_);
    public static final ItemSubPredicate.Type<ItemEnchantmentsPredicate.Enchantments> f_315030_ = m_324921_(
        "enchantments", ItemEnchantmentsPredicate.Enchantments.f_316379_
    );
    public static final ItemSubPredicate.Type<ItemEnchantmentsPredicate.StoredEnchantments> f_315495_ = m_324921_(
        "stored_enchantments", ItemEnchantmentsPredicate.StoredEnchantments.f_316422_
    );
    public static final ItemSubPredicate.Type<ItemPotionsPredicate> f_316396_ = m_324921_("potion_contents", ItemPotionsPredicate.f_316881_);
    public static final ItemSubPredicate.Type<ItemCustomDataPredicate> f_315323_ = m_324921_("custom_data", ItemCustomDataPredicate.f_316481_);
    public static final ItemSubPredicate.Type<ItemContainerPredicate> f_315373_ = m_324921_("container", ItemContainerPredicate.f_315175_);
    public static final ItemSubPredicate.Type<ItemBundlePredicate> f_316770_ = m_324921_("bundle_contents", ItemBundlePredicate.f_314903_);
    public static final ItemSubPredicate.Type<ItemFireworkExplosionPredicate> f_314976_ = m_324921_(
        "firework_explosion", ItemFireworkExplosionPredicate.f_314276_
    );
    public static final ItemSubPredicate.Type<ItemFireworksPredicate> f_314145_ = m_324921_("fireworks", ItemFireworksPredicate.f_315729_);
    public static final ItemSubPredicate.Type<ItemWritableBookPredicate> f_316895_ = m_324921_("writable_book_content", ItemWritableBookPredicate.f_316382_);
    public static final ItemSubPredicate.Type<ItemWrittenBookPredicate> f_315381_ = m_324921_("written_book_content", ItemWrittenBookPredicate.f_314892_);
    public static final ItemSubPredicate.Type<ItemAttributeModifiersPredicate> f_314189_ = m_324921_(
        "attribute_modifiers", ItemAttributeModifiersPredicate.f_315051_
    );
    public static final ItemSubPredicate.Type<ItemTrimPredicate> f_317130_ = m_324921_("trim", ItemTrimPredicate.f_315217_);

    private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> m_324921_(String p_334515_, Codec<T> p_329751_) {
        return Registry.register(BuiltInRegistries.f_315468_, p_334515_, new ItemSubPredicate.Type<>(p_329751_));
    }

    public static ItemSubPredicate.Type<?> m_318816_(Registry<ItemSubPredicate.Type<?>> p_334950_) {
        return f_315296_;
    }
}