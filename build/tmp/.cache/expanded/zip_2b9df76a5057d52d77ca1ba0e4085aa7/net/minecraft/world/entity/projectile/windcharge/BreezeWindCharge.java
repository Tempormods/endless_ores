package net.minecraft.world.entity.projectile.windcharge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.level.Level;

public class BreezeWindCharge extends AbstractWindCharge {
    private static final float f_316531_ = 3.0F;

    public BreezeWindCharge(EntityType<? extends AbstractWindCharge> p_328102_, Level p_329873_) {
        super(p_328102_, p_329873_);
    }

    public BreezeWindCharge(Breeze p_330729_, Level p_329490_) {
        super(EntityType.f_315936_, p_329490_, p_330729_, p_330729_.getX(), p_330729_.m_307822_(), p_330729_.getZ());
    }

    @Override
    protected void m_320462_() {
        this.level()
            .m_255039_(
                this,
                null,
                f_316764_,
                this.getX(),
                this.getY(),
                this.getZ(),
                3.0F,
                false,
                Level.ExplosionInteraction.BLOW,
                ParticleTypes.f_315099_,
                ParticleTypes.f_316181_,
                SoundEvents.f_316455_
            );
    }
}