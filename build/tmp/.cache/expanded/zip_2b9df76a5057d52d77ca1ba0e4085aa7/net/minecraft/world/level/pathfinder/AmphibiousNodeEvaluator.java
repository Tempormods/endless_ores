package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class AmphibiousNodeEvaluator extends WalkNodeEvaluator {
    private final boolean prefersShallowSwimming;
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    public AmphibiousNodeEvaluator(boolean pPrefersShallowSwimming) {
        this.prefersShallowSwimming = pPrefersShallowSwimming;
    }

    @Override
    public void prepare(PathNavigationRegion pLevel, Mob pMob) {
        super.prepare(pLevel, pMob);
        pMob.setPathfindingMalus(PathType.WATER, 0.0F);
        this.oldWalkableCost = pMob.getPathfindingMalus(PathType.WALKABLE);
        pMob.setPathfindingMalus(PathType.WALKABLE, 6.0F);
        this.oldWaterBorderCost = pMob.getPathfindingMalus(PathType.WATER_BORDER);
        pMob.setPathfindingMalus(PathType.WATER_BORDER, 4.0F);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathType.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(PathType.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public Node getStart() {
        return !this.mob.isInWater()
            ? super.getStart()
            : this.getStartNode(
                new BlockPos(
                    Mth.floor(this.mob.getBoundingBox().minX),
                    Mth.floor(this.mob.getBoundingBox().minY + 0.5),
                    Mth.floor(this.mob.getBoundingBox().minZ)
                )
            );
    }

    @Override
    public Target m_319819_(double p_330100_, double p_334194_, double p_330998_) {
        return this.m_322518_(p_330100_, p_334194_ + 0.5, p_330998_);
    }

    @Override
    public int getNeighbors(Node[] pOutputArray, Node pNode) {
        int i = super.getNeighbors(pOutputArray, pNode);
        PathType pathtype = this.m_321558_(pNode.x, pNode.y + 1, pNode.z);
        PathType pathtype1 = this.m_321558_(pNode.x, pNode.y, pNode.z);
        int j;
        if (this.mob.getPathfindingMalus(pathtype) >= 0.0F && pathtype1 != PathType.STICKY_HONEY) {
            j = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
        } else {
            j = 0;
        }

        double d0 = this.getFloorLevel(new BlockPos(pNode.x, pNode.y, pNode.z));
        Node node = this.findAcceptedNode(pNode.x, pNode.y + 1, pNode.z, Math.max(0, j - 1), d0, Direction.UP, pathtype1);
        Node node1 = this.findAcceptedNode(pNode.x, pNode.y - 1, pNode.z, j, d0, Direction.DOWN, pathtype1);
        if (this.isVerticalNeighborValid(node, pNode)) {
            pOutputArray[i++] = node;
        }

        if (this.isVerticalNeighborValid(node1, pNode) && pathtype1 != PathType.TRAPDOOR) {
            pOutputArray[i++] = node1;
        }

        for (int k = 0; k < i; k++) {
            Node node2 = pOutputArray[k];
            if (node2.type == PathType.WATER && this.prefersShallowSwimming && node2.y < this.mob.level().getSeaLevel() - 10) {
                node2.costMalus++;
            }
        }

        return i;
    }

    private boolean isVerticalNeighborValid(@Nullable Node pNeighbor, Node pNode) {
        return this.isNeighborValid(pNeighbor, pNode) && pNeighbor.type == PathType.WATER;
    }

    @Override
    protected boolean isAmphibious() {
        return true;
    }

    @Override
    public PathType m_320240_(PathfindingContext p_336213_, int p_329171_, int p_336028_, int p_327966_) {
        PathType pathtype = p_336213_.m_324267_(p_329171_, p_336028_, p_327966_);
        if (pathtype == PathType.WATER) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (Direction direction : Direction.values()) {
                blockpos$mutableblockpos.set(p_329171_, p_336028_, p_327966_).move(direction);
                PathType pathtype1 = p_336213_.m_324267_(
                    blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getY(), blockpos$mutableblockpos.getZ()
                );
                if (pathtype1 == PathType.BLOCKED) {
                    return PathType.WATER_BORDER;
                }
            }

            return PathType.WATER;
        } else {
            return super.m_320240_(p_336213_, p_329171_, p_336028_, p_327966_);
        }
    }
}