package net.minecraft.world;

import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class TickRateManager {
    public static final float f_302470_ = 1.0F;
    protected float f_302740_ = 20.0F;
    protected long f_303856_ = TimeUtil.NANOSECONDS_PER_SECOND / 20L;
    protected int f_303482_ = 0;
    protected boolean f_302370_ = true;
    protected boolean f_303812_ = false;

    public void m_307254_(float p_312754_) {
        this.f_302740_ = Math.max(p_312754_, 1.0F);
        this.f_303856_ = (long)((double)TimeUtil.NANOSECONDS_PER_SECOND / (double)this.f_302740_);
    }

    public float m_306179_() {
        return this.f_302740_;
    }

    public float m_305111_() {
        return (float)this.f_303856_ / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND;
    }

    public long m_307289_() {
        return this.f_303856_;
    }

    public boolean m_305915_() {
        return this.f_302370_;
    }

    public boolean m_307006_() {
        return this.f_303482_ > 0;
    }

    public void m_307652_(int p_312047_) {
        this.f_303482_ = p_312047_;
    }

    public int m_306668_() {
        return this.f_303482_;
    }

    public void m_306419_(boolean p_312988_) {
        this.f_303812_ = p_312988_;
    }

    public boolean m_306363_() {
        return this.f_303812_;
    }

    public void m_306707_() {
        this.f_302370_ = !this.f_303812_ || this.f_303482_ > 0;
        if (this.f_303482_ > 0) {
            this.f_303482_--;
        }
    }

    public boolean m_305579_(Entity p_311574_) {
        return !this.m_305915_() && !(p_311574_ instanceof Player) && p_311574_.m_305867_() <= 0;
    }
}