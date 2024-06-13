package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralArrow extends AbstractArrow {
    private int duration = 200;

    public SpectralArrow(EntityType<? extends SpectralArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SpectralArrow(Level pLevel, LivingEntity p_311904_, ItemStack p_309799_) {
        super(EntityType.SPECTRAL_ARROW, p_311904_, pLevel, p_309799_);
    }

    public SpectralArrow(Level pLevel, double p_309976_, double p_310894_, double p_309922_, ItemStack p_311719_) {
        super(EntityType.SPECTRAL_ARROW, p_309976_, p_310894_, p_309922_, pLevel, p_311719_);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.inGround) {
            this.level().addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity pLiving) {
        super.doPostHurtEffects(pLiving);
        MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
        pLiving.addEffect(mobeffectinstance, this.getEffectSource());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Duration")) {
            this.duration = pCompound.getInt("Duration");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Duration", this.duration);
    }

    @Override
    protected ItemStack m_321416_() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }
}