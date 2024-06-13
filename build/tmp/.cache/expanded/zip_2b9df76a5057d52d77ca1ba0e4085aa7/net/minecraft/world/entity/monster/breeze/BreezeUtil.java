package net.minecraft.world.entity.monster.breeze;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BreezeUtil {
    private static final double f_315268_ = 50.0;

    public static Vec3 m_318815_(LivingEntity p_333833_, RandomSource p_335052_) {
        int i = 90;
        float f = p_333833_.yHeadRot + 180.0F + (float)p_335052_.nextGaussian() * 90.0F / 2.0F;
        float f1 = Mth.lerp(p_335052_.nextFloat(), 4.0F, 8.0F);
        Vec3 vec3 = Vec3.directionFromRotation(0.0F, f).scale((double)f1);
        return p_333833_.position().add(vec3);
    }

    public static boolean m_320427_(Breeze p_334566_, Vec3 p_329106_) {
        Vec3 vec3 = new Vec3(p_334566_.getX(), p_334566_.getY(), p_334566_.getZ());
        return p_329106_.distanceTo(vec3) > 50.0
            ? false
            : p_334566_.level().clip(new ClipContext(vec3, p_329106_, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_334566_)).getType()
                == HitResult.Type.MISS;
    }
}