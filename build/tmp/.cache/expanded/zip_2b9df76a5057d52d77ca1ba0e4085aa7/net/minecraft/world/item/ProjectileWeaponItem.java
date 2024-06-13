package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem extends Item {
    public static final Predicate<ItemStack> ARROW_ONLY = p_43017_ -> p_43017_.is(ItemTags.ARROWS);
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or(p_43015_ -> p_43015_.is(Items.FIREWORK_ROCKET));

    public ProjectileWeaponItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    public abstract Predicate<ItemStack> getAllSupportedProjectiles();

    public static ItemStack getHeldProjectile(LivingEntity pShooter, Predicate<ItemStack> pIsAmmo) {
        if (pIsAmmo.test(pShooter.getItemInHand(InteractionHand.OFF_HAND))) {
            return pShooter.getItemInHand(InteractionHand.OFF_HAND);
        } else {
            return pIsAmmo.test(pShooter.getItemInHand(InteractionHand.MAIN_HAND)) ? pShooter.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
        }
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    public abstract int getDefaultProjectileRange();

    protected void m_324710_(
        Level p_331349_,
        LivingEntity p_332682_,
        InteractionHand p_333462_,
        ItemStack p_333670_,
        List<ItemStack> p_328443_,
        float p_330956_,
        float p_333326_,
        boolean p_332457_,
        @Nullable LivingEntity p_328954_
    ) {
        float f = 10.0F;
        float f1 = p_328443_.size() == 1 ? 0.0F : 20.0F / (float)(p_328443_.size() - 1);
        float f2 = (float)((p_328443_.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for (int i = 0; i < p_328443_.size(); i++) {
            ItemStack itemstack = p_328443_.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
                f3 = -f3;
                p_333670_.hurtAndBreak(this.m_319432_(itemstack), p_332682_, LivingEntity.m_322775_(p_333462_));
                Projectile projectile = this.loadProjectile(p_331349_, p_332682_, p_333670_, itemstack, p_332457_);
                this.shootProjectile(p_332682_, projectile, i, p_330956_, p_333326_, f4, p_328954_);
                p_331349_.addFreshEntity(projectile);
            }
        }
    }

    protected int m_319432_(ItemStack p_330687_) {
        return 1;
    }

    protected abstract void shootProjectile(
        LivingEntity p_330864_, Projectile p_328720_, int p_328740_, float p_335337_, float p_332934_, float p_329948_, @Nullable LivingEntity p_329516_
    );

    protected Projectile loadProjectile(Level p_333069_, LivingEntity p_334736_, ItemStack p_333680_, ItemStack p_329118_, boolean p_336242_) {
        ArrowItem arrowitem = p_329118_.getItem() instanceof ArrowItem arrowitem1 ? arrowitem1 : (ArrowItem)Items.ARROW;
        AbstractArrow abstractarrow = arrowitem.createArrow(p_333069_, p_329118_, p_334736_);
        abstractarrow = customArrow(abstractarrow);
        if (p_336242_) {
            abstractarrow.setCritArrow(true);
        }

        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.f_314636_, p_333680_);
        if (k > 0) {
            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)k * 0.5 + 0.5);
        }

        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.f_316860_, p_333680_);
        if (i > 0) {
            abstractarrow.setKnockback(i);
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.f_316779_, p_333680_) > 0) {
            abstractarrow.m_322706_(100);
        }

        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, p_333680_);
        if (j > 0) {
            abstractarrow.setPierceLevel((byte)j);
        }

        return abstractarrow;
    }

    protected static boolean m_324621_(ItemStack p_336325_, ItemStack p_333325_, boolean p_333373_) {
        return p_333373_ || p_333325_.is(Items.ARROW) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.f_316098_, p_336325_) > 0;
    }

    protected static List<ItemStack> m_320555_(ItemStack p_329054_, ItemStack p_328618_, LivingEntity p_335616_) {
        if (p_328618_.isEmpty()) {
            return List.of();
        } else {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, p_329054_);
            int j = i == 0 ? 1 : 3;
            List<ItemStack> list = new ArrayList<>(j);
            ItemStack itemstack = p_328618_.copy();
            boolean infinite = p_328618_.getItem() instanceof ArrowItem arrow && arrow.isInfinite(p_328618_, p_329054_, p_335616_);

            for (int k = 0; k < j; k++) {
                list.add(m_324157_(p_329054_, k == 0 ? p_328618_ : itemstack, p_335616_, k > 0 || infinite));
            }

            return list;
        }
    }

    protected static ItemStack m_324157_(ItemStack p_335938_, ItemStack p_332014_, LivingEntity p_332327_, boolean p_327685_) {
        boolean flag = !p_327685_ && !m_324621_(p_335938_, p_332014_, p_332327_.m_322042_());
        if (!flag) {
            ItemStack itemstack1 = p_332014_.copyWithCount(1);
            itemstack1.m_322496_(DataComponents.f_314381_, Unit.INSTANCE);
            return itemstack1;
        } else {
            ItemStack itemstack = p_332014_.split(1);
            if (p_332014_.isEmpty() && p_332327_ instanceof Player player) {
                player.getInventory().removeItem(p_332014_);
            }

            return itemstack;
        }
    }

    public AbstractArrow customArrow(AbstractArrow arrow) {
        return arrow;
    }
}
