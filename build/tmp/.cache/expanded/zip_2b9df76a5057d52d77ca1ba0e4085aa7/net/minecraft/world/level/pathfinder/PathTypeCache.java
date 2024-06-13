package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

public class PathTypeCache {
    private static final int f_316885_ = 4096;
    private static final int f_314200_ = 4095;
    private final long[] f_316090_ = new long[4096];
    private final PathType[] f_314466_ = new PathType[4096];

    public PathType m_321132_(BlockGetter p_328738_, BlockPos p_328240_) {
        long i = p_328240_.asLong();
        int j = m_320515_(i);
        PathType pathtype = this.m_321310_(j, i);
        return pathtype != null ? pathtype : this.m_318597_(p_328738_, p_328240_, j, i);
    }

    @Nullable
    private PathType m_321310_(int p_331898_, long p_334711_) {
        return this.f_316090_[p_331898_] == p_334711_ ? this.f_314466_[p_331898_] : null;
    }

    private PathType m_318597_(BlockGetter p_333989_, BlockPos p_334142_, int p_329562_, long p_332989_) {
        PathType pathtype = WalkNodeEvaluator.m_324497_(p_333989_, p_334142_);
        this.f_316090_[p_329562_] = p_332989_;
        this.f_314466_[p_329562_] = pathtype;
        return pathtype;
    }

    public void m_324305_(BlockPos p_332226_) {
        long i = p_332226_.asLong();
        int j = m_320515_(i);
        if (this.f_316090_[j] == i) {
            this.f_314466_[j] = null;
        }
    }

    private static int m_320515_(long p_328788_) {
        return (int)HashCommon.mix(p_328788_) & 4095;
    }
}