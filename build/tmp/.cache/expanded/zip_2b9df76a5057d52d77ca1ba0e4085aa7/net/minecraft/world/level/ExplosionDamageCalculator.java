package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class ExplosionDamageCalculator {
    public Optional<Float> getBlockExplosionResistance(Explosion pExplosion, BlockGetter pReader, BlockPos pPos, BlockState pState, FluidState pFluid) {
        return pState.isAir() && pFluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(pState.getExplosionResistance(pReader, pPos, pExplosion), pFluid.getExplosionResistance(pReader, pPos, pExplosion)));
    }

    public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter pReader, BlockPos pPos, BlockState pState, float pPower) {
        return true;
    }

    public boolean m_304921_(Explosion p_312772_, Entity p_311132_) {
        return true;
    }

    public float m_320739_(Entity p_330296_) {
        return 1.0F;
    }

    public float m_305869_(Explosion p_310428_, Entity p_310135_) {
        float f = p_310428_.m_305027_() * 2.0F;
        Vec3 vec3 = p_310428_.m_307721_();
        double d0 = Math.sqrt(p_310135_.distanceToSqr(vec3)) / (double)f;
        double d1 = (1.0 - d0) * (double)Explosion.getSeenPercent(vec3, p_310135_);
        return (float)((d1 * d1 + d1) / 2.0 * 7.0 * (double)f + 1.0);
    }
}
