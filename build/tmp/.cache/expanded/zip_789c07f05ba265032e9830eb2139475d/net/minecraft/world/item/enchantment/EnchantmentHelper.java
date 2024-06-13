package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
    private static final float SWIFT_SNEAK_EXTRA_FACTOR = 0.15F;

    public static int getItemEnchantmentLevel(Enchantment p_44844_, ItemStack p_44845_) {
        ItemEnchantments itemenchantments = p_44845_.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return itemenchantments.getLevel(p_44844_);
    }

    public static ItemEnchantments updateEnchantments(ItemStack p_333740_, Consumer<ItemEnchantments.Mutable> p_328467_) {
        DataComponentType<ItemEnchantments> datacomponenttype = getComponentType(p_333740_);
        ItemEnchantments itemenchantments = p_333740_.get(datacomponenttype);
        if (itemenchantments == null) {
            return ItemEnchantments.EMPTY;
        } else {
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(itemenchantments);
            p_328467_.accept(itemenchantments$mutable);
            ItemEnchantments itemenchantments1 = itemenchantments$mutable.toImmutable();
            p_333740_.set(datacomponenttype, itemenchantments1);
            return itemenchantments1;
        }
    }

    public static boolean canStoreEnchantments(ItemStack p_333572_) {
        return p_333572_.has(getComponentType(p_333572_));
    }

    public static void setEnchantments(ItemStack p_44867_, ItemEnchantments p_330134_) {
        p_44867_.set(getComponentType(p_44867_), p_330134_);
    }

    public static ItemEnchantments getEnchantmentsForCrafting(ItemStack p_335659_) {
        return p_335659_.getOrDefault(getComponentType(p_335659_), ItemEnchantments.EMPTY);
    }

    private static DataComponentType<ItemEnchantments> getComponentType(ItemStack p_335414_) {
        return p_335414_.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
    }

    public static boolean hasAnyEnchantments(ItemStack p_335287_) {
        return !p_335287_.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()
            || !p_335287_.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
    }

    public static float getSweepingDamageRatio(int p_332009_) {
        return 1.0F - 1.0F / (float)(p_332009_ + 1);
    }

    private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor p_44851_, ItemStack p_44852_) {
        ItemEnchantments itemenchantments = p_44852_.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
            p_44851_.accept(entry.getKey().value(), entry.getIntValue());
        }
    }

    private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor p_44854_, Iterable<ItemStack> p_44855_) {
        for (ItemStack itemstack : p_44855_) {
            runIterationOnItem(p_44854_, itemstack);
        }
    }

    public static int getDamageProtection(Iterable<ItemStack> p_44857_, DamageSource p_44858_) {
        MutableInt mutableint = new MutableInt();
        runIterationOnInventory((p_44892_, p_44893_) -> mutableint.add(p_44892_.getDamageProtection(p_44893_, p_44858_)), p_44857_);
        return mutableint.intValue();
    }

    public static float getDamageBonus(ItemStack p_44834_, @Nullable EntityType<?> p_331963_) {
        MutableFloat mutablefloat = new MutableFloat();
        runIterationOnItem((p_327223_, p_327224_) -> mutablefloat.add(p_327223_.getDamageBonus(p_327224_, p_331963_, p_44834_)), p_44834_);
        return mutablefloat.floatValue();
    }

    public static float getSweepingDamageRatio(LivingEntity p_44822_) {
        int i = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, p_44822_);
        return i > 0 ? getSweepingDamageRatio(i) : 0.0F;
    }

    public static float calculateArmorBreach(@Nullable Entity p_329003_, float p_334370_) {
        if (p_329003_ instanceof LivingEntity livingentity) {
            int i = getEnchantmentLevel(Enchantments.BREACH, livingentity);
            if (i > 0) {
                return BreachEnchantment.calculateArmorBreach((float)i, p_334370_);
            }
        }

        return p_334370_;
    }

    public static void doPostHurtEffects(LivingEntity p_44824_, Entity p_44825_) {
        EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44902_, p_44903_) -> p_44902_.doPostHurt(p_44824_, p_44825_, p_44903_);
        if (p_44824_ != null) {
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, p_44824_.getAllSlots());
        }

        if(false) // Forge: Fix MC-248272
        if (p_44825_ instanceof Player) {
            runIterationOnItem(enchantmenthelper$enchantmentvisitor, p_44824_.getMainHandItem());
        }
    }

    public static void doPostDamageEffects(LivingEntity p_44897_, Entity p_44898_) {
        EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44829_, p_44830_) -> p_44829_.doPostAttack(p_44897_, p_44898_, p_44830_);
        if (p_44897_ != null) {
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, p_44897_.getAllSlots());
        }

        if(false) // Forge: Fix MC-248272
        if (p_44897_ instanceof Player) {
            runIterationOnItem(enchantmenthelper$enchantmentvisitor, p_44897_.getMainHandItem());
        }
    }

    public static void doPostItemStackHurtEffects(LivingEntity p_330971_, Entity p_330890_, ItemEnchantments p_336148_) {
        for (Entry<Holder<Enchantment>> entry : p_336148_.entrySet()) {
            entry.getKey().value().doPostItemStackHurt(p_330971_, p_330890_, entry.getIntValue());
        }
    }

    public static int getEnchantmentLevel(Enchantment p_44837_, LivingEntity p_44838_) {
        Iterable<ItemStack> iterable = p_44837_.getSlotItems(p_44838_).values();
        if (iterable == null) {
            return 0;
        } else {
            int i = 0;

            for (ItemStack itemstack : iterable) {
                int j = getItemEnchantmentLevel(p_44837_, itemstack);
                if (j > i) {
                    i = j;
                }
            }

            return i;
        }
    }

    public static float getSneakingSpeedBonus(LivingEntity p_220303_) {
        return (float)getEnchantmentLevel(Enchantments.SWIFT_SNEAK, p_220303_) * 0.15F;
    }

    public static int getKnockbackBonus(LivingEntity p_44895_) {
        return getEnchantmentLevel(Enchantments.KNOCKBACK, p_44895_);
    }

    public static int getFireAspect(LivingEntity p_44915_) {
        return getEnchantmentLevel(Enchantments.FIRE_ASPECT, p_44915_);
    }

    public static int getRespiration(LivingEntity p_44919_) {
        return getEnchantmentLevel(Enchantments.RESPIRATION, p_44919_);
    }

    public static int getDepthStrider(LivingEntity p_44923_) {
        return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, p_44923_);
    }

    public static int getBlockEfficiency(LivingEntity p_44927_) {
        return getEnchantmentLevel(Enchantments.EFFICIENCY, p_44927_);
    }

    public static int getFishingLuckBonus(ItemStack p_44905_) {
        return getItemEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, p_44905_);
    }

    public static int getFishingSpeedBonus(ItemStack p_44917_) {
        return getItemEnchantmentLevel(Enchantments.LURE, p_44917_);
    }

    public static int getMobLooting(LivingEntity p_44931_) {
        return getEnchantmentLevel(Enchantments.LOOTING, p_44931_);
    }

    public static boolean hasAquaAffinity(LivingEntity p_44935_) {
        return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, p_44935_) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity p_44939_) {
        return getEnchantmentLevel(Enchantments.FROST_WALKER, p_44939_) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity p_44943_) {
        return getEnchantmentLevel(Enchantments.SOUL_SPEED, p_44943_) > 0;
    }

    public static boolean hasBindingCurse(ItemStack p_44921_) {
        return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, p_44921_) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack p_44925_) {
        return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, p_44925_) > 0;
    }

    public static boolean hasSilkTouch(ItemStack p_273444_) {
        return getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_273444_) > 0;
    }

    public static int getLoyalty(ItemStack p_44929_) {
        return getItemEnchantmentLevel(Enchantments.LOYALTY, p_44929_);
    }

    public static int getRiptide(ItemStack p_44933_) {
        return getItemEnchantmentLevel(Enchantments.RIPTIDE, p_44933_);
    }

    public static boolean hasChanneling(ItemStack p_44937_) {
        return getItemEnchantmentLevel(Enchantments.CHANNELING, p_44937_) > 0;
    }

    @Nullable
    public static java.util.Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment p_44907_, LivingEntity p_44908_) {
        return getRandomItemWith(p_44907_, p_44908_, p_44941_ -> true);
    }

    @Nullable
    public static java.util.Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment p_44840_, LivingEntity p_44841_, Predicate<ItemStack> p_44842_) {
        Map<EquipmentSlot, ItemStack> map = p_44840_.getSlotItems(p_44841_);
        if (map.isEmpty()) {
            return null;
        } else {
            List<java.util.Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

            for (java.util.Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
                ItemStack itemstack = entry.getValue();
                if (!itemstack.isEmpty() && getItemEnchantmentLevel(p_44840_, itemstack) > 0 && p_44842_.test(itemstack)) {
                    list.add(entry);
                }
            }

            return list.isEmpty() ? null : list.get(p_44841_.getRandom().nextInt(list.size()));
        }
    }

    public static int getEnchantmentCost(RandomSource p_220288_, int p_220289_, int p_220290_, ItemStack p_220291_) {
        Item item = p_220291_.getItem();
        int i = p_220291_.getEnchantmentValue();
        if (i <= 0) {
            return 0;
        } else {
            if (p_220290_ > 15) {
                p_220290_ = 15;
            }

            int j = p_220288_.nextInt(8) + 1 + (p_220290_ >> 1) + p_220288_.nextInt(p_220290_ + 1);
            if (p_220289_ == 0) {
                return Math.max(j / 3, 1);
            } else {
                return p_220289_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_220290_ * 2);
            }
        }
    }

    public static ItemStack enchantItem(FeatureFlagSet p_329514_, RandomSource p_220293_, ItemStack p_220294_, int p_220295_, boolean p_220296_) {
        List<EnchantmentInstance> list = selectEnchantment(p_329514_, p_220293_, p_220294_, p_220295_, p_220296_);
        if (p_220294_.is(Items.BOOK)) {
            p_220294_ = new ItemStack(Items.ENCHANTED_BOOK);
        }

        for (EnchantmentInstance enchantmentinstance : list) {
            p_220294_.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
        }

        return p_220294_;
    }

    public static List<EnchantmentInstance> selectEnchantment(FeatureFlagSet p_329284_, RandomSource p_220298_, ItemStack p_220299_, int p_220300_, boolean p_220301_) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = p_220299_.getItem();
        int i = p_220299_.getEnchantmentValue();
        if (i <= 0) {
            return list;
        } else {
            p_220300_ += 1 + p_220298_.nextInt(i / 4 + 1) + p_220298_.nextInt(i / 4 + 1);
            float f = (p_220298_.nextFloat() + p_220298_.nextFloat() - 1.0F) * 0.15F;
            p_220300_ = Mth.clamp(Math.round((float)p_220300_ + (float)p_220300_ * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(p_329284_, p_220300_, p_220299_, p_220301_);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(p_220298_, list1).ifPresent(list::add);

                while (p_220298_.nextInt(50) <= p_220300_) {
                    if (!list.isEmpty()) {
                        filterCompatibleEnchantments(list1, Util.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(p_220298_, list1).ifPresent(list::add);
                    p_220300_ /= 2;
                }
            }

            return list;
        }
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> p_44863_, EnchantmentInstance p_44864_) {
        Iterator<EnchantmentInstance> iterator = p_44863_.iterator();

        while (iterator.hasNext()) {
            if (!p_44864_.enchantment.isCompatibleWith(iterator.next().enchantment)) {
                iterator.remove();
            }
        }
    }

    public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> p_44860_, Enchantment p_44861_) {
        for (Holder<Enchantment> holder : p_44860_) {
            if (!holder.value().isCompatibleWith(p_44861_)) {
                return false;
            }
        }

        return true;
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(FeatureFlagSet p_334102_, int p_44818_, ItemStack p_44819_, boolean p_44820_) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean flag = p_44819_.is(Items.BOOK);

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if (enchantment.isEnabled(p_334102_)
                && (!enchantment.isTreasureOnly() || p_44820_)
                && enchantment.isDiscoverable()
                && ((flag && enchantment.isAllowedOnBooks()) || enchantment.canApplyAtEnchantingTable(p_44819_))) {
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
                    if (p_44818_ >= enchantment.getMinCost(i) && p_44818_ <= enchantment.getMaxCost(i)) {
                        list.add(new EnchantmentInstance(enchantment, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

    @FunctionalInterface
    interface EnchantmentVisitor {
        void accept(Enchantment p_44945_, int p_44946_);
    }
}
