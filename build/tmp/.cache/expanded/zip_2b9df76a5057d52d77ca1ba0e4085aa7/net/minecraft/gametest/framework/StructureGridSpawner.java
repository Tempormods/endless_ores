package net.minecraft.gametest.framework;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class StructureGridSpawner implements GameTestRunner.StructureSpawner {
    private static final int f_317034_ = 5;
    private static final int f_314807_ = 6;
    private final int f_314071_;
    private int f_315917_;
    private AABB f_316053_;
    private final BlockPos.MutableBlockPos f_317117_;
    private final BlockPos f_315928_;

    public StructureGridSpawner(BlockPos p_329915_, int p_328380_) {
        this.f_314071_ = p_328380_;
        this.f_317117_ = p_329915_.mutable();
        this.f_316053_ = new AABB(this.f_317117_);
        this.f_315928_ = p_329915_;
    }

    @Override
    public Optional<GameTestInfo> m_321592_(GameTestInfo p_335013_) {
        BlockPos blockpos = new BlockPos(this.f_317117_);
        p_335013_.m_322570_(blockpos);
        p_335013_.m_306517_();
        AABB aabb = StructureUtils.getStructureBounds(p_335013_.getStructureBlockEntity());
        this.f_316053_ = this.f_316053_.minmax(aabb);
        this.f_317117_.move((int)aabb.getXsize() + 5, 0, 0);
        if (++this.f_315917_ >= this.f_314071_) {
            this.f_315917_ = 0;
            this.f_317117_.move(0, 0, (int)this.f_316053_.getZsize() + 6);
            this.f_317117_.setX(this.f_315928_.getX());
            this.f_316053_ = new AABB(this.f_317117_);
        }

        return Optional.of(p_335013_);
    }
}