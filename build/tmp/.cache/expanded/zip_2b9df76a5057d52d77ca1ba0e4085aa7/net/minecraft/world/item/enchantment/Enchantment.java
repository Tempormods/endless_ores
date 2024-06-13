package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Enchantment implements FeatureElement, net.minecraftforge.common.extensions.IForgeEnchantment {
    private final Enchantment.EnchantmentDefinition f_316889_;
    @Nullable
    protected String descriptionId;
    private final Holder.Reference<Enchantment> builtInRegistryHolder = BuiltInRegistries.ENCHANTMENT.createIntrusiveHolder(this);

    public static Enchantment.Cost m_322287_(int p_334530_) {
        return new Enchantment.Cost(p_334530_, 0);
    }

    public static Enchantment.Cost m_318803_(int p_334326_, int p_335507_) {
        return new Enchantment.Cost(p_334326_, p_335507_);
    }

    public static Enchantment.EnchantmentDefinition m_322764_(
        TagKey<Item> p_329090_,
        TagKey<Item> p_332240_,
        int p_328611_,
        int p_336009_,
        Enchantment.Cost p_330605_,
        Enchantment.Cost p_333983_,
        int p_327771_,
        EquipmentSlot... p_329538_
    ) {
        return new Enchantment.EnchantmentDefinition(
            p_329090_, Optional.of(p_332240_), p_328611_, p_336009_, p_330605_, p_333983_, p_327771_, FeatureFlags.DEFAULT_FLAGS, p_329538_
        );
    }

    public static Enchantment.EnchantmentDefinition m_324539_(
        TagKey<Item> p_334656_, int p_335023_, int p_332990_, Enchantment.Cost p_328936_, Enchantment.Cost p_332239_, int p_332354_, EquipmentSlot... p_334822_
    ) {
        return new Enchantment.EnchantmentDefinition(
            p_334656_, Optional.empty(), p_335023_, p_332990_, p_328936_, p_332239_, p_332354_, FeatureFlags.DEFAULT_FLAGS, p_334822_
        );
    }

    public static Enchantment.EnchantmentDefinition m_319628_(
        TagKey<Item> p_335329_,
        int p_329635_,
        int p_331888_,
        Enchantment.Cost p_328182_,
        Enchantment.Cost p_328787_,
        int p_333931_,
        FeatureFlagSet p_330633_,
        EquipmentSlot... p_330676_
    ) {
        return new Enchantment.EnchantmentDefinition(p_335329_, Optional.empty(), p_329635_, p_331888_, p_328182_, p_328787_, p_333931_, p_330633_, p_330676_);
    }

    @Nullable
    public static Enchantment byId(int pId) {
        return BuiltInRegistries.ENCHANTMENT.byId(pId);
    }

    public Enchantment(Enchantment.EnchantmentDefinition p_327760_) {
        this.f_316889_ = p_327760_;
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity pEntity) {
        Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

        for (EquipmentSlot equipmentslot : this.f_316889_.f_315895_()) {
            ItemStack itemstack = pEntity.getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                map.put(equipmentslot, itemstack);
            }
        }

        return map;
    }

    public final TagKey<Item> m_318986_() {
        return this.f_316889_.f_316739_();
    }

    public final boolean m_320566_(ItemStack p_334183_) {
        return this.f_316889_.f_314876_.isEmpty() || p_334183_.is(this.f_316889_.f_314876_.get());
    }

    public final int m_322444_() {
        return this.f_316889_.f_315386_();
    }

    public final int m_320305_() {
        return this.f_316889_.f_317020_();
    }

    public final int getMinLevel() {
        return 1;
    }

    public final int getMaxLevel() {
        return this.f_316889_.f_315828_();
    }

    public final int getMinCost(int pLevel) {
        return this.f_316889_.f_316239_().m_321581_(pLevel);
    }

    public final int getMaxCost(int pLevel) {
        return this.f_316889_.f_316226_().m_321581_(pLevel);
    }

    public int getDamageProtection(int pLevel, DamageSource pSource) {
        return 0;
    }

    @Deprecated // Forge: Use ItemStack aware version in IForgeEnchantment
    public float getDamageBonus(int pLevel, @Nullable EntityType<?> p_331633_) {
        return 0.0F;
    }

    public final boolean isCompatibleWith(Enchantment pOther) {
        return this.checkCompatibility(pOther) && pOther.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment pOther) {
        return this != pOther;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int pLevel) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            mutablecomponent.withStyle(ChatFormatting.RED);
        } else {
            mutablecomponent.withStyle(ChatFormatting.GRAY);
        }

        if (pLevel != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + pLevel));
        }

        return mutablecomponent;
    }

    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem().builtInRegistryHolder().is(this.f_316889_.f_316739_());
    }

    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
    }

    public void doPostHurt(LivingEntity pTarget, Entity pAttacker, int pLevel) {
    }

    public void m_320095_(LivingEntity p_335453_, Entity p_329978_, int p_331186_) {
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    @Deprecated
    public Holder.Reference<Enchantment> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.f_316889_.f_314796_();
    }

    public static record Cost(int f_314969_, int f_316360_) {
        public int m_321581_(int p_333351_) {
            return this.f_314969_ + this.f_316360_ * (p_333351_ - 1);
        }
    }

    public static record EnchantmentDefinition(
        TagKey<Item> f_316739_,
        Optional<TagKey<Item>> f_314876_,
        int f_315386_,
        int f_315828_,
        Enchantment.Cost f_316239_,
        Enchantment.Cost f_316226_,
        int f_317020_,
        FeatureFlagSet f_314796_,
        EquipmentSlot[] f_315895_
    ) {
    }
}
