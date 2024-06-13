package net.minecraft.world.entity;

import net.minecraft.world.item.ItemStack;

public class Crackiness {
    public static final Crackiness f_314390_ = new Crackiness(0.75F, 0.5F, 0.25F);
    public static final Crackiness f_315625_ = new Crackiness(0.95F, 0.69F, 0.32F);
    private final float f_316649_;
    private final float f_314052_;
    private final float f_314125_;

    private Crackiness(float p_332482_, float p_329781_, float p_335121_) {
        this.f_316649_ = p_332482_;
        this.f_314052_ = p_329781_;
        this.f_314125_ = p_335121_;
    }

    public Crackiness.Level m_320179_(float p_330247_) {
        if (p_330247_ < this.f_314125_) {
            return Crackiness.Level.HIGH;
        } else if (p_330247_ < this.f_314052_) {
            return Crackiness.Level.MEDIUM;
        } else {
            return p_330247_ < this.f_316649_ ? Crackiness.Level.LOW : Crackiness.Level.NONE;
        }
    }

    public Crackiness.Level m_318874_(ItemStack p_328846_) {
        return !p_328846_.isDamageableItem() ? Crackiness.Level.NONE : this.m_324753_(p_328846_.getDamageValue(), p_328846_.getMaxDamage());
    }

    public Crackiness.Level m_324753_(int p_329022_, int p_332255_) {
        return this.m_320179_((float)(p_332255_ - p_329022_) / (float)p_332255_);
    }

    public static enum Level {
        NONE,
        LOW,
        MEDIUM,
        HIGH;
    }
}