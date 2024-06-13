package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

class InfestedMobEffect extends MobEffect {
    private final float f_314935_;
    private final ToIntFunction<RandomSource> f_315326_;

    protected InfestedMobEffect(MobEffectCategory p_330372_, int p_332310_, float p_334976_, ToIntFunction<RandomSource> p_328183_) {
        super(p_330372_, p_332310_, ParticleTypes.f_314307_);
        this.f_314935_ = p_334976_;
        this.f_315326_ = p_328183_;
    }

    @Override
    public void applyEffectTick(LivingEntity p_334146_, int p_328888_, DamageSource p_330722_, float p_331740_) {
        if (p_334146_.getRandom().nextFloat() <= this.f_314935_) {
            int i = this.f_315326_.applyAsInt(p_334146_.getRandom());

            for (int j = 0; j < i; j++) {
                this.m_319488_(
                    p_334146_.level(), p_334146_, p_334146_.getX(), p_334146_.getY() + (double)p_334146_.getBbHeight() / 2.0, p_334146_.getZ()
                );
            }
        }
    }

    private void m_319488_(Level p_332452_, LivingEntity p_327972_, double p_334243_, double p_334439_, double p_333358_) {
        Silverfish silverfish = EntityType.SILVERFISH.create(p_332452_);
        if (silverfish != null) {
            RandomSource randomsource = p_327972_.getRandom();
            float f = (float) (Math.PI / 2);
            float f1 = Mth.randomBetween(randomsource, (float) (-Math.PI / 2), (float) (Math.PI / 2));
            Vector3f vector3f = p_327972_.getLookAngle().toVector3f().mul(0.3F).mul(1.0F, 1.5F, 1.0F).rotateY(f1);
            silverfish.moveTo(p_334243_, p_334439_, p_333358_, p_332452_.getRandom().nextFloat() * 360.0F, 0.0F);
            silverfish.setDeltaMovement(new Vec3(vector3f));
            p_332452_.addFreshEntity(silverfish);
            silverfish.playSound(SoundEvents.SILVERFISH_HURT);
        }
    }
}