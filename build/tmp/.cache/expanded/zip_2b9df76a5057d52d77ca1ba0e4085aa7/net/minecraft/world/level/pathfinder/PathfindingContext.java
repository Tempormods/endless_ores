package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PathfindingContext {
    private final CollisionGetter f_314135_;
    @Nullable
    private final PathTypeCache f_315633_;
    private final BlockPos f_314553_;
    private final BlockPos.MutableBlockPos f_315666_ = new BlockPos.MutableBlockPos();

    public PathfindingContext(CollisionGetter p_335722_, Mob p_329527_) {
        this.f_314135_ = p_335722_;
        if (p_329527_.level() instanceof ServerLevel serverlevel) {
            this.f_315633_ = serverlevel.m_321916_();
        } else {
            this.f_315633_ = null;
        }

        this.f_314553_ = p_329527_.blockPosition();
    }

    public PathType m_324267_(int p_332092_, int p_328372_, int p_333164_) {
        BlockPos blockpos = this.f_315666_.set(p_332092_, p_328372_, p_333164_);
        return this.f_315633_ == null ? WalkNodeEvaluator.m_324497_(this.f_314135_, blockpos) : this.f_315633_.m_321132_(this.f_314135_, blockpos);
    }

    public BlockState m_320852_(BlockPos p_333632_) {
        return this.f_314135_.getBlockState(p_333632_);
    }

    public CollisionGetter m_321732_() {
        return this.f_314135_;
    }

    public BlockPos m_323060_() {
        return this.f_314553_;
    }
}