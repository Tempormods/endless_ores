package net.minecraft.client;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
    public float partialTick;
    public float tickDelta;
    private long lastMs;
    private final float msPerTick;
    private final FloatUnaryOperator f_302653_;

    public Timer(float pTicksPerSecond, long pLastMs, FloatUnaryOperator p_311663_) {
        this.msPerTick = 1000.0F / pTicksPerSecond;
        this.lastMs = pLastMs;
        this.f_302653_ = p_311663_;
    }

    public int advanceTime(long pGameTime) {
        this.tickDelta = (float)(pGameTime - this.lastMs) / this.f_302653_.apply(this.msPerTick);
        this.lastMs = pGameTime;
        this.partialTick = this.partialTick + this.tickDelta;
        int i = (int)this.partialTick;
        this.partialTick -= (float)i;
        return i;
    }
}