package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface ProjectileDeflection {
    ProjectileDeflection f_316951_ = (p_335766_, p_335741_, p_334113_) -> {
    };
    ProjectileDeflection f_314640_ = (p_328050_, p_331573_, p_336016_) -> {
        float f = 170.0F + p_336016_.nextFloat() * 20.0F;
        p_328050_.setDeltaMovement(p_328050_.getDeltaMovement().scale(-0.5));
        p_328050_.setYRot(p_328050_.getYRot() + f);
        p_328050_.yRotO += f;
        p_328050_.hurtMarked = true;
    };
    ProjectileDeflection f_314043_ = (p_330772_, p_332720_, p_329183_) -> {
        if (p_332720_ != null) {
            Vec3 vec3 = p_332720_.getLookAngle().normalize();
            p_330772_.setDeltaMovement(vec3);
            p_330772_.hurtMarked = true;
        }
    };
    ProjectileDeflection f_316502_ = (p_332290_, p_334407_, p_333225_) -> {
        if (p_334407_ != null) {
            Vec3 vec3 = p_334407_.getDeltaMovement().normalize();
            p_332290_.setDeltaMovement(vec3);
            p_332290_.hurtMarked = true;
        }
    };

    void m_322705_(Projectile p_332034_, @Nullable Entity p_330319_, RandomSource p_333938_);
}