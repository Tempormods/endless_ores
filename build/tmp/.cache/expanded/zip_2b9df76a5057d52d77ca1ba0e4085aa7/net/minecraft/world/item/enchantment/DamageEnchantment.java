package net.minecraft.world.item.enchantment;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class DamageEnchantment extends Enchantment {
    private final Optional<TagKey<EntityType<?>>> f_314326_;

    public DamageEnchantment(Enchantment.EnchantmentDefinition p_330513_, Optional<TagKey<EntityType<?>>> p_332960_) {
        super(p_330513_);
        this.f_314326_ = p_332960_;
    }

    @Override
    public float getDamageBonus(int pLevel, @Nullable EntityType<?> p_331449_) {
        if (this.f_314326_.isEmpty()) {
            return 1.0F + (float)Math.max(0, pLevel - 1) * 0.5F;
        } else {
            return p_331449_ != null && p_331449_.is(this.f_314326_.get()) ? (float)pLevel * 2.5F : 0.0F;
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return !(pEnch instanceof DamageEnchantment);
    }

    @Override
    public void doPostAttack(LivingEntity pUser, Entity pTarget, int pLevel) {
        if (this.f_314326_.isPresent()
            && pTarget instanceof LivingEntity livingentity
            && this.f_314326_.get() == EntityTypeTags.f_314167_
            && pLevel > 0
            && livingentity.getType().is(this.f_314326_.get())) {
            int i = 20 + pUser.getRandom().nextInt(10 * pLevel);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
        }
    }
}