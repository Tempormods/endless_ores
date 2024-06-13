package net.minecraft.world.item.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;

public class WindBurstEnchantment extends Enchantment {
    public WindBurstEnchantment() {
        super(
            Enchantment.m_319628_(
                ItemTags.f_314471_,
                2,
                3,
                Enchantment.m_318803_(15, 9),
                Enchantment.m_318803_(65, 9),
                4,
                FeatureFlagSet.of(FeatureFlags.f_302467_),
                EquipmentSlot.MAINHAND
            )
        );
    }

    @Override
    public void m_320095_(LivingEntity p_336187_, Entity p_332860_, int p_328211_) {
        float f = 0.25F + 0.25F * (float)p_328211_;
        p_336187_.level()
            .m_255039_(
                null,
                null,
                new WindBurstEnchantment.WindBurstEnchantmentDamageCalculator(f),
                p_336187_.getX(),
                p_336187_.getY(),
                p_336187_.getZ(),
                3.5F,
                false,
                Level.ExplosionInteraction.BLOW,
                ParticleTypes.f_315099_,
                ParticleTypes.f_316181_,
                SoundEvents.f_314772_
            );
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    static final class WindBurstEnchantmentDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
        private final float f_314563_;

        public WindBurstEnchantmentDamageCalculator(float p_331438_) {
            this.f_314563_ = p_331438_;
        }

        @Override
        public float m_320739_(Entity p_335974_) {
            boolean flag1;
            label17: {
                if (p_335974_ instanceof Player player && player.getAbilities().flying) {
                    flag1 = true;
                    break label17;
                }

                flag1 = false;
            }

            boolean flag = flag1;
            return !flag ? this.f_314563_ : 0.0F;
        }
    }
}