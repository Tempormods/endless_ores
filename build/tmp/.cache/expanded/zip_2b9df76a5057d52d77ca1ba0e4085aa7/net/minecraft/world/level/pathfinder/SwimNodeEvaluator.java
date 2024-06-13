package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
    private final boolean allowBreaching;
    private final Long2ObjectMap<PathType> pathTypesByPosCache = new Long2ObjectOpenHashMap<>();

    public SwimNodeEvaluator(boolean pAllowBreaching) {
        this.allowBreaching = pAllowBreaching;
    }

    @Override
    public void prepare(PathNavigationRegion pLevel, Mob pMob) {
        super.prepare(pLevel, pMob);
        this.pathTypesByPosCache.clear();
    }

    @Override
    public void done() {
        super.done();
        this.pathTypesByPosCache.clear();
    }

    @Override
    public Node getStart() {
        return this.getNode(
            Mth.floor(this.mob.getBoundingBox().minX),
            Mth.floor(this.mob.getBoundingBox().minY + 0.5),
            Mth.floor(this.mob.getBoundingBox().minZ)
        );
    }

    @Override
    public Target m_319819_(double p_331212_, double p_329065_, double p_336263_) {
        return this.m_322518_(p_331212_, p_329065_, p_336263_);
    }

    @Override
    public int getNeighbors(Node[] pOutputArray, Node pNode) {
        int i = 0;
        Map<Direction, Node> map = Maps.newEnumMap(Direction.class);

        for (Direction direction : Direction.values()) {
            Node node = this.findAcceptedNode(
                pNode.x + direction.getStepX(), pNode.y + direction.getStepY(), pNode.z + direction.getStepZ()
            );
            map.put(direction, node);
            if (this.isNodeValid(node)) {
                pOutputArray[i++] = node;
            }
        }

        for (Direction direction1 : Direction.Plane.HORIZONTAL) {
            Direction direction2 = direction1.getClockWise();
            if (m_319460_(map.get(direction1)) && m_319460_(map.get(direction2))) {
                Node node1 = this.findAcceptedNode(
                    pNode.x + direction1.getStepX() + direction2.getStepX(),
                    pNode.y,
                    pNode.z + direction1.getStepZ() + direction2.getStepZ()
                );
                if (this.isNodeValid(node1)) {
                    pOutputArray[i++] = node1;
                }
            }
        }

        return i;
    }

    protected boolean isNodeValid(@Nullable Node pNode) {
        return pNode != null && !pNode.closed;
    }

    private static boolean m_319460_(@Nullable Node p_328144_) {
        return p_328144_ != null && p_328144_.costMalus >= 0.0F;
    }

    @Nullable
    protected Node findAcceptedNode(int pX, int pY, int pZ) {
        Node node = null;
        PathType pathtype = this.getCachedBlockType(pX, pY, pZ);
        if (this.allowBreaching && pathtype == PathType.BREACH || pathtype == PathType.WATER) {
            float f = this.mob.getPathfindingMalus(pathtype);
            if (f >= 0.0F) {
                node = this.getNode(pX, pY, pZ);
                node.type = pathtype;
                node.costMalus = Math.max(node.costMalus, f);
                if (this.f_314620_.m_321732_().getFluidState(new BlockPos(pX, pY, pZ)).isEmpty()) {
                    node.costMalus += 8.0F;
                }
            }
        }

        return node;
    }

    protected PathType getCachedBlockType(int pX, int pY, int pZ) {
        return this.pathTypesByPosCache
            .computeIfAbsent(BlockPos.asLong(pX, pY, pZ), p_327515_ -> this.m_320240_(this.f_314620_, pX, pY, pZ));
    }

    @Override
    public PathType m_320240_(PathfindingContext p_333668_, int p_333001_, int p_328513_, int p_333109_) {
        return this.m_319854_(p_333668_, p_333001_, p_328513_, p_333109_, this.mob);
    }

    @Override
    public PathType m_319854_(PathfindingContext p_327815_, int p_334955_, int p_333227_, int p_331057_, Mob p_333533_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = p_334955_; i < p_334955_ + this.entityWidth; i++) {
            for (int j = p_333227_; j < p_333227_ + this.entityHeight; j++) {
                for (int k = p_331057_; k < p_331057_ + this.entityDepth; k++) {
                    BlockState blockstate = p_327815_.m_320852_(blockpos$mutableblockpos.set(i, j, k));
                    FluidState fluidstate = blockstate.getFluidState();
                    if (fluidstate.isEmpty() && blockstate.isPathfindable(PathComputationType.WATER) && blockstate.isAir()) {
                        return PathType.BREACH;
                    }

                    if (!fluidstate.is(FluidTags.WATER)) {
                        return PathType.BLOCKED;
                    }
                }
            }
        }

        BlockState blockstate1 = p_327815_.m_320852_(blockpos$mutableblockpos);
        return blockstate1.isPathfindable(PathComputationType.WATER) ? PathType.WATER : PathType.BLOCKED;
    }
}