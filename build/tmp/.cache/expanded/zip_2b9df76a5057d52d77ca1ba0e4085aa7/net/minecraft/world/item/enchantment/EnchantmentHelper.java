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

    public static int getItemEnchantmentLevel(Enchantment pEnchantment, ItemStack pStack) {
        ItemEnchantments itemenchantments = pStack.m_322304_(DataComponents.f_314658_, ItemEnchantments.f_314789_);
        return itemenchantments.m_320299_(pEnchantment);
    }

    public static ItemEnchantments m_320959_(ItemStack p_333740_, Consumer<ItemEnchantments.Mutable> p_328467_) {
        DataComponentType<ItemEnchantments> datacomponenttype = m_322805_(p_333740_);
        ItemEnchantments itemenchantments = p_333740_.m_323252_(datacomponenttype);
        if (itemenchantments == null) {
            return ItemEnchantments.f_314789_;
        } else {
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(itemenchantments);
            p_328467_.accept(itemenchantments$mutable);
            ItemEnchantments itemenchantments1 = itemenchantments$mutable.m_321565_();
            p_333740_.m_322496_(datacomponenttype, itemenchantments1);
            return itemenchantments1;
        }
    }

    public static boolean m_320740_(ItemStack p_333572_) {
        return p_333572_.m_319951_(m_322805_(p_333572_));
    }

    public static void setEnchantments(ItemStack pStack, ItemEnchantments p_330134_) {
        pStack.m_322496_(m_322805_(pStack), p_330134_);
    }

    public static ItemEnchantments m_324152_(ItemStack p_335659_) {
        return p_335659_.m_322304_(m_322805_(p_335659_), ItemEnchantments.f_314789_);
    }

    private static DataComponentType<ItemEnchantments> m_322805_(ItemStack p_335414_) {
        return p_335414_.is(Items.ENCHANTED_BOOK) ? DataComponents.f_314515_ : DataComponents.f_314658_;
    }

    public static boolean m_322755_(ItemStack p_335287_) {
        return !p_335287_.m_322304_(DataComponents.f_314658_, ItemEnchantments.f_314789_).m_324000_()
            || !p_335287_.m_322304_(DataComponents.f_314515_, ItemEnchantments.f_314789_).m_324000_();
    }

    public static float m_323615_(int p_332009_) {
        return 1.0F - 1.0F / (float)(p_332009_ + 1);
    }

    private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor pVisitor, ItemStack pStack) {
        ItemEnchantments itemenchantments = pStack.m_322304_(DataComponents.f_314658_, ItemEnchantments.f_314789_);

        for (Entry<Holder<Enchantment>> entry : itemenchantments.m_320130_()) {
            pVisitor.accept(entry.getKey().value(), entry.getIntValue());
        }
    }

    private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor pVisitor, Iterable<ItemStack> pStacks) {
        for (ItemStack itemstack : pStacks) {
            runIterationOnItem(pVisitor, itemstack);
        }
    }

    public static int getDamageProtection(Iterable<ItemStack> pStacks, DamageSource pSource) {
        MutableInt mutableint = new MutableInt();
        runIterationOnInventory((p_44892_, p_44893_) -> mutableint.add(p_44892_.getDamageProtection(p_44893_, pSource)), pStacks);
        return mutableint.intValue();
    }

    public static float getDamageBonus(ItemStack pStack, @Nullable EntityType<?> p_331963_) {
        MutableFloat mutablefloat = new MutableFloat();
        runIterationOnItem((p_327223_, p_327224_) -> mutablefloat.add(p_327223_.getDamageBonus(p_327224_, p_331963_, pStack)), pStack);
        return mutablefloat.floatValue();
    }

    public static float getSweepingDamageRatio(LivingEntity pEntity) {
        int i = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, pEntity);
        return i > 0 ? m_323615_(i) : 0.0F;
    }

    public static float m_318919_(@Nullable Entity p_329003_, float p_334370_) {
        if (p_329003_ instanceof LivingEntity livingentity) {
            int i = getEnchantmentLevel(Enchantments.f_316771_, livingentity);
            if (i > 0) {
                return BreachEnchantment.m_318789_((float)i, p_334370_);
            }
        }

        return p_334370_;
    }

    public static void doPostHurtEffects(LivingEntity pTarget, Entity pAttacker) {
        EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44902_, p_44903_) -> p_44902_.doPostHurt(pTarget, pAttacker, p_44903_);
        if (pTarget != null) {
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, pTarget.m_323629_());
        }

        if(false) // Forge: Fix MC-248272
        if (pAttacker instanceof Player) {
            runIterationOnItem(enchantmenthelper$enchantmentvisitor, pTarget.getMainHandItem());
        }
    }

    public static void doPostDamageEffects(LivingEntity pAttacker, Entity pTarget) {
        EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (p_44829_, p_44830_) -> p_44829_.doPostAttack(pAttacker, pTarget, p_44830_);
        if (pAttacker != null) {
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, pAttacker.m_323629_());
        }

        if(false) // Forge: Fix MC-248272
        if (pAttacker instanceof Player) {
            runIterationOnItem(enchantmenthelper$enchantmentvisitor, pAttacker.getMainHandItem());
        }
    }

    public static void m_323486_(LivingEntity p_330971_, Entity p_330890_, ItemEnchantments p_336148_) {
        for (Entry<Holder<Enchantment>> entry : p_336148_.m_320130_()) {
            entry.getKey().value().m_320095_(p_330971_, p_330890_, entry.getIntValue());
        }
    }

    public static int getEnchantmentLevel(Enchantment pEnchantment, LivingEntity pEntity) {
        Iterable<ItemStack> iterable = pEnchantment.getSlotItems(pEntity).values();
        if (iterable == null) {
            return 0;
        } else {
            int i = 0;

            for (ItemStack itemstack : iterable) {
                int j = getItemEnchantmentLevel(pEnchantment, itemstack);
                if (j > i) {
                    i = j;
                }
            }

            return i;
        }
    }

    public static float getSneakingSpeedBonus(LivingEntity pEntity) {
        return (float)getEnchantmentLevel(Enchantments.SWIFT_SNEAK, pEntity) * 0.15F;
    }

    public static int getKnockbackBonus(LivingEntity pPlayer) {
        return getEnchantmentLevel(Enchantments.KNOCKBACK, pPlayer);
    }

    public static int getFireAspect(LivingEntity pPlayer) {
        return getEnchantmentLevel(Enchantments.FIRE_ASPECT, pPlayer);
    }

    public static int getRespiration(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.RESPIRATION, pEntity);
    }

    public static int getDepthStrider(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, pEntity);
    }

    public static int getBlockEfficiency(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.f_316758_, pEntity);
    }

    public static int getFishingLuckBonus(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.f_314659_, pStack);
    }

    public static int getFishingSpeedBonus(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.f_314874_, pStack);
    }

    public static int getMobLooting(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.f_316023_, pEntity);
    }

    public static boolean hasAquaAffinity(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, pEntity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity pPlayer) {
        return getEnchantmentLevel(Enchantments.FROST_WALKER, pPlayer) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity pEntity) {
        return getEnchantmentLevel(Enchantments.SOUL_SPEED, pEntity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, pStack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, pStack) > 0;
    }

    public static boolean hasSilkTouch(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.SILK_TOUCH, pStack) > 0;
    }

    public static int getLoyalty(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.LOYALTY, pStack);
    }

    public static int getRiptide(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.RIPTIDE, pStack);
    }

    public static boolean hasChanneling(ItemStack pStack) {
        return getItemEnchantmentLevel(Enchantments.CHANNELING, pStack) > 0;
    }

    @Nullable
    public static java.util.Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment pTargetEnchantment, LivingEntity pEntity) {
        return getRandomItemWith(pTargetEnchantment, pEntity, p_44941_ -> true);
    }

    @Nullable
    public static java.util.Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment pEnchantment, LivingEntity pLivingEntity, Predicate<ItemStack> pStackCondition) {
        Map<EquipmentSlot, ItemStack> map = pEnchantment.getSlotItems(pLivingEntity);
        if (map.isEmpty()) {
            return null;
        } else {
            List<java.util.Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

            for (java.util.Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
                ItemStack itemstack = entry.getValue();
                if (!itemstack.isEmpty() && getItemEnchantmentLevel(pEnchantment, itemstack) > 0 && pStackCondition.test(itemstack)) {
                    list.add(entry);
                }
            }

            return list.isEmpty() ? null : list.get(pLivingEntity.getRandom().nextInt(list.size()));
        }
    }

    public static int getEnchantmentCost(RandomSource pRandom, int pEnchantNum, int pPower, ItemStack pStack) {
        Item item = pStack.getItem();
        int i = pStack.getEnchantmentValue();
        if (i <= 0) {
            return 0;
        } else {
            if (pPower > 15) {
                pPower = 15;
            }

            int j = pRandom.nextInt(8) + 1 + (pPower >> 1) + pRandom.nextInt(pPower + 1);
            if (pEnchantNum == 0) {
                return Math.max(j / 3, 1);
            } else {
                return pEnchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, pPower * 2);
            }
        }
    }

    public static ItemStack enchantItem(FeatureFlagSet p_329514_, RandomSource pRandom, ItemStack pStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = selectEnchantment(p_329514_, pRandom, pStack, pLevel, pAllowTreasure);
        if (pStack.is(Items.BOOK)) {
            pStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        for (EnchantmentInstance enchantmentinstance : list) {
            pStack.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
        }

        return pStack;
    }

    public static List<EnchantmentInstance> selectEnchantment(FeatureFlagSet p_329284_, RandomSource pRandom, ItemStack pItemStack, int pLevel, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = pItemStack.getItem();
        int i = pItemStack.getEnchantmentValue();
        if (i <= 0) {
            return list;
        } else {
            pLevel += 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
            float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
            pLevel = Mth.clamp(Math.round((float)pLevel + (float)pLevel * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(p_329284_, pLevel, pItemStack, pAllowTreasure);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);

                while (pRandom.nextInt(50) <= pLevel) {
                    if (!list.isEmpty()) {
                        filterCompatibleEnchantments(list1, Util.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(pRandom, list1).ifPresent(list::add);
                    pLevel /= 2;
                }
            }

            return list;
        }
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> pDataList, EnchantmentInstance pData) {
        Iterator<EnchantmentInstance> iterator = pDataList.iterator();

        while (iterator.hasNext()) {
            if (!pData.enchantment.isCompatibleWith(iterator.next().enchantment)) {
                iterator.remove();
            }
        }
    }

    public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> pEnchantments, Enchantment pEnchantment) {
        for (Holder<Enchantment> holder : pEnchantments) {
            if (!holder.value().isCompatibleWith(pEnchantment)) {
                return false;
            }
        }

        return true;
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(FeatureFlagSet p_334102_, int pLevel, ItemStack pStack, boolean pAllowTreasure) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean flag = pStack.is(Items.BOOK);

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if (enchantment.isEnabled(p_334102_)
                && (!enchantment.isTreasureOnly() || pAllowTreasure)
                && enchantment.isDiscoverable()
                && ((flag && enchantment.isAllowedOnBooks()) || enchantment.canApplyAtEnchantingTable(pStack))) {
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
                    if (pLevel >= enchantment.getMinCost(i) && pLevel <= enchantment.getMaxCost(i)) {
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
        void accept(Enchantment pEnchantment, int pLevel);
    }
}
