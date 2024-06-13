package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
    public static final double SPACE_BETWEEN_WALL_POSTS = 0.5;
    private static final double DEFAULT_MOB_JUMP_HEIGHT = 1.125;
    private final Long2ObjectMap<PathType> f_315292_ = new Long2ObjectOpenHashMap<>();
    private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap<>();
    private final Node[] f_313986_ = new Node[Direction.Plane.HORIZONTAL.m_322453_()];

    @Override
    public void prepare(PathNavigationRegion pLevel, Mob pMob) {
        super.prepare(pLevel, pMob);
        pMob.onPathfindingStart();
    }

    @Override
    public void done() {
        this.mob.onPathfindingDone();
        this.f_315292_.clear();
        this.collisionCache.clear();
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = this.mob.getBlockY();
        BlockState blockstate = this.f_314620_.m_320852_(blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ()));
        if (!this.mob.canStandOnFluid(blockstate.getFluidState())) {
            if (this.canFloat() && this.mob.isInWater()) {
                while (true) {
                    if (!blockstate.is(Blocks.WATER) && blockstate.getFluidState() != Fluids.WATER.getSource(false)) {
                        i--;
                        break;
                    }

                    blockstate = this.f_314620_
                        .m_320852_(blockpos$mutableblockpos.set(this.mob.getX(), (double)(++i), this.mob.getZ()));
                }
            } else if (this.mob.onGround()) {
                i = Mth.floor(this.mob.getY() + 0.5);
            } else {
                blockpos$mutableblockpos.set(this.mob.getX(), this.mob.getY() + 1.0, this.mob.getZ());

                while (blockpos$mutableblockpos.getY() > this.f_314620_.m_321732_().getMinBuildHeight()) {
                    i = blockpos$mutableblockpos.getY();
                    blockpos$mutableblockpos.setY(blockpos$mutableblockpos.getY() - 1);
                    BlockState blockstate1 = this.f_314620_.m_320852_(blockpos$mutableblockpos);
                    if (!blockstate1.isAir() && !blockstate1.isPathfindable(PathComputationType.LAND)) {
                        break;
                    }
                }
            }
        } else {
            while (this.mob.canStandOnFluid(blockstate.getFluidState())) {
                blockstate = this.f_314620_.m_320852_(blockpos$mutableblockpos.set(this.mob.getX(), (double)(++i), this.mob.getZ()));
            }

            i--;
        }

        BlockPos blockpos = this.mob.blockPosition();
        if (!this.canStartAt(blockpos$mutableblockpos.set(blockpos.getX(), i, blockpos.getZ()))) {
            AABB aabb = this.mob.getBoundingBox();
            if (this.canStartAt(blockpos$mutableblockpos.set(aabb.minX, (double)i, aabb.minZ))
                || this.canStartAt(blockpos$mutableblockpos.set(aabb.minX, (double)i, aabb.maxZ))
                || this.canStartAt(blockpos$mutableblockpos.set(aabb.maxX, (double)i, aabb.minZ))
                || this.canStartAt(blockpos$mutableblockpos.set(aabb.maxX, (double)i, aabb.maxZ))) {
                return this.getStartNode(blockpos$mutableblockpos);
            }
        }

        return this.getStartNode(new BlockPos(blockpos.getX(), i, blockpos.getZ()));
    }

    protected Node getStartNode(BlockPos pPos) {
        Node node = this.getNode(pPos);
        node.type = this.m_321558_(node.x, node.y, node.z);
        node.costMalus = this.mob.getPathfindingMalus(node.type);
        return node;
    }

    protected boolean canStartAt(BlockPos pPos) {
        PathType pathtype = this.m_321558_(pPos.getX(), pPos.getY(), pPos.getZ());
        return pathtype != PathType.OPEN && this.mob.getPathfindingMalus(pathtype) >= 0.0F;
    }

    @Override
    public Target m_319819_(double p_334058_, double p_329070_, double p_328068_) {
        return this.m_322518_(p_334058_, p_329070_, p_328068_);
    }

    @Override
    public int getNeighbors(Node[] pOutputArray, Node pNode) {
        int i = 0;
        int j = 0;
        PathType pathtype = this.m_321558_(pNode.x, pNode.y + 1, pNode.z);
        PathType pathtype1 = this.m_321558_(pNode.x, pNode.y, pNode.z);
        if (this.mob.getPathfindingMalus(pathtype) >= 0.0F && pathtype1 != PathType.STICKY_HONEY) {
            j = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
        }

        double d0 = this.getFloorLevel(new BlockPos(pNode.x, pNode.y, pNode.z));

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Node node = this.findAcceptedNode(
                pNode.x + direction.getStepX(), pNode.y, pNode.z + direction.getStepZ(), j, d0, direction, pathtype1
            );
            this.f_313986_[direction.get2DDataValue()] = node;
            if (this.isNeighborValid(node, pNode)) {
                pOutputArray[i++] = node;
            }
        }

        for (Direction direction1 : Direction.Plane.HORIZONTAL) {
            Direction direction2 = direction1.getClockWise();
            if (this.isDiagonalValid(pNode, this.f_313986_[direction1.get2DDataValue()], this.f_313986_[direction2.get2DDataValue()])) {
                Node node1 = this.findAcceptedNode(
                    pNode.x + direction1.getStepX() + direction2.getStepX(),
                    pNode.y,
                    pNode.z + direction1.getStepZ() + direction2.getStepZ(),
                    j,
                    d0,
                    direction1,
                    pathtype1
                );
                if (this.m_321731_(node1)) {
                    pOutputArray[i++] = node1;
                }
            }
        }

        return i;
    }

    protected boolean isNeighborValid(@Nullable Node pNeighbor, Node pNode) {
        return pNeighbor != null && !pNeighbor.closed && (pNeighbor.costMalus >= 0.0F || pNode.costMalus < 0.0F);
    }

    protected boolean isDiagonalValid(Node pRoot, @Nullable Node pXNode, @Nullable Node pZNode) {
        if (pZNode == null || pXNode == null || pZNode.y > pRoot.y || pXNode.y > pRoot.y) {
            return false;
        } else if (pXNode.type != PathType.WALKABLE_DOOR && pZNode.type != PathType.WALKABLE_DOOR) {
            boolean flag = pZNode.type == PathType.FENCE && pXNode.type == PathType.FENCE && (double)this.mob.getBbWidth() < 0.5;
            return (pZNode.y < pRoot.y || pZNode.costMalus >= 0.0F || flag)
                && (pXNode.y < pRoot.y || pXNode.costMalus >= 0.0F || flag);
        } else {
            return false;
        }
    }

    protected boolean m_321731_(@Nullable Node p_332817_) {
        if (p_332817_ == null || p_332817_.closed) {
            return false;
        } else {
            return p_332817_.type == PathType.WALKABLE_DOOR ? false : p_332817_.costMalus >= 0.0F;
        }
    }

    private static boolean doesBlockHavePartialCollision(PathType p_332557_) {
        return p_332557_ == PathType.FENCE || p_332557_ == PathType.DOOR_WOOD_CLOSED || p_332557_ == PathType.DOOR_IRON_CLOSED;
    }

    private boolean canReachWithoutCollision(Node pNode) {
        AABB aabb = this.mob.getBoundingBox();
        Vec3 vec3 = new Vec3(
            (double)pNode.x - this.mob.getX() + aabb.getXsize() / 2.0,
            (double)pNode.y - this.mob.getY() + aabb.getYsize() / 2.0,
            (double)pNode.z - this.mob.getZ() + aabb.getZsize() / 2.0
        );
        int i = Mth.ceil(vec3.length() / aabb.getSize());
        vec3 = vec3.scale((double)(1.0F / (float)i));

        for (int j = 1; j <= i; j++) {
            aabb = aabb.move(vec3);
            if (this.hasCollisions(aabb)) {
                return false;
            }
        }

        return true;
    }

    protected double getFloorLevel(BlockPos pPos) {
        BlockGetter blockgetter = this.f_314620_.m_321732_();
        return (this.canFloat() || this.isAmphibious()) && blockgetter.getFluidState(pPos).is(FluidTags.WATER)
            ? (double)pPos.getY() + 0.5
            : getFloorLevel(blockgetter, pPos);
    }

    public static double getFloorLevel(BlockGetter pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        VoxelShape voxelshape = pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos);
        return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0 : voxelshape.max(Direction.Axis.Y));
    }

    protected boolean isAmphibious() {
        return false;
    }

    @Nullable
    protected Node findAcceptedNode(int pX, int pY, int pZ, int pVerticalDeltaLimit, double pNodeFloorLevel, Direction pDirection, PathType p_330077_) {
        Node node = null;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        double d0 = this.getFloorLevel(blockpos$mutableblockpos.set(pX, pY, pZ));
        if (d0 - pNodeFloorLevel > this.getMobJumpHeight()) {
            return null;
        } else {
            PathType pathtype = this.m_321558_(pX, pY, pZ);
            float f = this.mob.getPathfindingMalus(pathtype);
            if (f >= 0.0F) {
                node = this.getNodeAndUpdateCostToMax(pX, pY, pZ, pathtype, f);
            }

            if (doesBlockHavePartialCollision(p_330077_) && node != null && node.costMalus >= 0.0F && !this.canReachWithoutCollision(node)) {
                node = null;
            }

            if (pathtype != PathType.WALKABLE && (!this.isAmphibious() || pathtype != PathType.WATER)) {
                if ((node == null || node.costMalus < 0.0F)
                    && pVerticalDeltaLimit > 0
                    && (pathtype != PathType.FENCE || this.canWalkOverFences())
                    && pathtype != PathType.UNPASSABLE_RAIL
                    && pathtype != PathType.TRAPDOOR
                    && pathtype != PathType.POWDER_SNOW) {
                    node = this.m_324533_(pX, pY, pZ, pVerticalDeltaLimit, pNodeFloorLevel, pDirection, p_330077_, blockpos$mutableblockpos);
                } else if (!this.isAmphibious() && pathtype == PathType.WATER && !this.canFloat()) {
                    node = this.m_324617_(pX, pY, pZ, node);
                } else if (pathtype == PathType.OPEN) {
                    node = this.m_322432_(pX, pY, pZ);
                } else if (doesBlockHavePartialCollision(pathtype) && node == null) {
                    node = this.m_321803_(pX, pY, pZ, pathtype);
                }

                return node;
            } else {
                return node;
            }
        }
    }

    private double getMobJumpHeight() {
        return Math.max(1.125, (double)this.mob.maxUpStep());
    }

    private Node getNodeAndUpdateCostToMax(int pX, int pY, int pZ, PathType p_335762_, float pCostMalus) {
        Node node = this.getNode(pX, pY, pZ);
        node.type = p_335762_;
        node.costMalus = Math.max(node.costMalus, pCostMalus);
        return node;
    }

    private Node getBlockedNode(int pX, int pY, int pZ) {
        Node node = this.getNode(pX, pY, pZ);
        node.type = PathType.BLOCKED;
        node.costMalus = -1.0F;
        return node;
    }

    private Node m_321803_(int p_332713_, int p_333094_, int p_327804_, PathType p_334600_) {
        Node node = this.getNode(p_332713_, p_333094_, p_327804_);
        node.closed = true;
        node.type = p_334600_;
        node.costMalus = p_334600_.m_320214_();
        return node;
    }

    @Nullable
    private Node m_324533_(
        int p_335353_,
        int p_333388_,
        int p_331837_,
        int p_329120_,
        double p_335627_,
        Direction p_334618_,
        PathType p_330418_,
        BlockPos.MutableBlockPos p_329431_
    ) {
        Node node = this.findAcceptedNode(p_335353_, p_333388_ + 1, p_331837_, p_329120_ - 1, p_335627_, p_334618_, p_330418_);
        if (node == null) {
            return null;
        } else if (this.mob.getBbWidth() >= 1.0F) {
            return node;
        } else if (node.type != PathType.OPEN && node.type != PathType.WALKABLE) {
            return node;
        } else {
            double d0 = (double)(p_335353_ - p_334618_.getStepX()) + 0.5;
            double d1 = (double)(p_331837_ - p_334618_.getStepZ()) + 0.5;
            double d2 = (double)this.mob.getBbWidth() / 2.0;
            AABB aabb = new AABB(
                d0 - d2,
                this.getFloorLevel(p_329431_.set(d0, (double)(p_333388_ + 1), d1)) + 0.001,
                d1 - d2,
                d0 + d2,
                (double)this.mob.getBbHeight()
                    + this.getFloorLevel(p_329431_.set((double)node.x, (double)node.y, (double)node.z))
                    - 0.002,
                d1 + d2
            );
            return this.hasCollisions(aabb) ? null : node;
        }
    }

    @Nullable
    private Node m_324617_(int p_334565_, int p_335840_, int p_330496_, @Nullable Node p_327969_) {
        p_335840_--;

        while (p_335840_ > this.mob.level().getMinBuildHeight()) {
            PathType pathtype = this.m_321558_(p_334565_, p_335840_, p_330496_);
            if (pathtype != PathType.WATER) {
                return p_327969_;
            }

            p_327969_ = this.getNodeAndUpdateCostToMax(p_334565_, p_335840_, p_330496_, pathtype, this.mob.getPathfindingMalus(pathtype));
            p_335840_--;
        }

        return p_327969_;
    }

    private Node m_322432_(int p_335495_, int p_328639_, int p_335885_) {
        for (int i = p_328639_ - 1; i >= this.mob.level().getMinBuildHeight(); i--) {
            if (p_328639_ - i > this.mob.getMaxFallDistance()) {
                return this.getBlockedNode(p_335495_, i, p_335885_);
            }

            PathType pathtype = this.m_321558_(p_335495_, i, p_335885_);
            float f = this.mob.getPathfindingMalus(pathtype);
            if (pathtype != PathType.OPEN) {
                if (f >= 0.0F) {
                    return this.getNodeAndUpdateCostToMax(p_335495_, i, p_335885_, pathtype, f);
                }

                return this.getBlockedNode(p_335495_, i, p_335885_);
            }
        }

        return this.getBlockedNode(p_335495_, p_328639_, p_335885_);
    }

    private boolean hasCollisions(AABB pBoundingBox) {
        return this.collisionCache.computeIfAbsent(pBoundingBox, p_327517_ -> !this.f_314620_.m_321732_().noCollision(this.mob, pBoundingBox));
    }

    protected PathType m_321558_(int p_328411_, int p_334833_, int p_334446_) {
        return this.f_315292_
            .computeIfAbsent(
                BlockPos.asLong(p_328411_, p_334833_, p_334446_),
                p_327521_ -> this.m_319854_(this.f_314620_, p_328411_, p_334833_, p_334446_, this.mob)
            );
    }

    @Override
    public PathType m_319854_(PathfindingContext p_336212_, int p_330284_, int p_332224_, int p_335362_, Mob p_327680_) {
        Set<PathType> set = this.m_320358_(p_336212_, p_330284_, p_332224_, p_335362_);
        if (set.contains(PathType.FENCE)) {
            return PathType.FENCE;
        } else if (set.contains(PathType.UNPASSABLE_RAIL)) {
            return PathType.UNPASSABLE_RAIL;
        } else {
            PathType pathtype = PathType.BLOCKED;

            for (PathType pathtype1 : set) {
                if (p_327680_.getPathfindingMalus(pathtype1) < 0.0F) {
                    return pathtype1;
                }

                if (p_327680_.getPathfindingMalus(pathtype1) >= p_327680_.getPathfindingMalus(pathtype)) {
                    pathtype = pathtype1;
                }
            }

            return this.entityWidth <= 1
                    && pathtype != PathType.OPEN
                    && p_327680_.getPathfindingMalus(pathtype) == 0.0F
                    && this.m_320240_(p_336212_, p_330284_, p_332224_, p_335362_) == PathType.OPEN
                ? PathType.OPEN
                : pathtype;
        }
    }

    public Set<PathType> m_320358_(PathfindingContext p_334304_, int p_335980_, int p_330052_, int p_334476_) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);

        for (int i = 0; i < this.entityWidth; i++) {
            for (int j = 0; j < this.entityHeight; j++) {
                for (int k = 0; k < this.entityDepth; k++) {
                    int l = i + p_335980_;
                    int i1 = j + p_330052_;
                    int j1 = k + p_334476_;
                    PathType pathtype = this.m_320240_(p_334304_, l, i1, j1);
                    BlockPos blockpos = this.mob.blockPosition();
                    boolean flag = this.canPassDoors();
                    if (pathtype == PathType.DOOR_WOOD_CLOSED && this.canOpenDoors() && flag) {
                        pathtype = PathType.WALKABLE_DOOR;
                    }

                    if (pathtype == PathType.DOOR_OPEN && !flag) {
                        pathtype = PathType.BLOCKED;
                    }

                    if (pathtype == PathType.RAIL
                        && this.m_320240_(p_334304_, blockpos.getX(), blockpos.getY(), blockpos.getZ()) != PathType.RAIL
                        && this.m_320240_(p_334304_, blockpos.getX(), blockpos.getY() - 1, blockpos.getZ()) != PathType.RAIL) {
                        pathtype = PathType.UNPASSABLE_RAIL;
                    }

                    enumset.add(pathtype);
                }
            }
        }

        return enumset;
    }

    @Override
    public PathType m_320240_(PathfindingContext p_333098_, int p_327758_, int p_329863_, int p_328680_) {
        return m_324107_(p_333098_, new BlockPos.MutableBlockPos(p_327758_, p_329863_, p_328680_));
    }

    public static PathType m_324593_(Mob p_332988_, BlockPos p_332803_) {
        return m_324107_(new PathfindingContext(p_332988_.level(), p_332988_), p_332803_.mutable());
    }

    public static PathType m_324107_(PathfindingContext p_335315_, BlockPos.MutableBlockPos p_334167_) {
        int i = p_334167_.getX();
        int j = p_334167_.getY();
        int k = p_334167_.getZ();
        PathType pathtype = p_335315_.m_324267_(i, j, k);
        if (pathtype == PathType.OPEN && j >= p_335315_.m_321732_().getMinBuildHeight() + 1) {
            return switch (p_335315_.m_324267_(i, j - 1, k)) {
                case OPEN, WATER, LAVA, WALKABLE -> PathType.OPEN;
                case DAMAGE_FIRE -> PathType.DAMAGE_FIRE;
                case DAMAGE_OTHER -> PathType.DAMAGE_OTHER;
                case STICKY_HONEY -> PathType.STICKY_HONEY;
                case POWDER_SNOW -> PathType.DANGER_POWDER_SNOW;
                case DAMAGE_CAUTIOUS -> PathType.DAMAGE_CAUTIOUS;
                case TRAPDOOR -> PathType.DANGER_TRAPDOOR;
                default -> checkNeighbourBlocks(p_335315_, i, j, k, PathType.WALKABLE);
            };
        } else {
            return pathtype;
        }
    }

    public static PathType checkNeighbourBlocks(PathfindingContext p_334221_, int p_336062_, int p_335259_, int p_336315_, PathType p_333971_) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (i != 0 || k != 0) {
                        PathType pathtype = p_334221_.m_324267_(p_336062_ + i, p_335259_ + j, p_336315_ + k);

                        var pos = new BlockPos(p_336062_ + i, p_335259_ + j, p_336315_ + k);
                        var blockstate = p_334221_.m_321732_().getBlockState(pos);

                        var blockPathType = blockstate.getAdjacentBlockPathType(p_334221_.m_321732_(), pos, null, p_333971_);
                        if (blockPathType != null) return blockPathType;

                        var fluidPathType = blockstate.getFluidState().getAdjacentBlockPathType(p_334221_.m_321732_(), pos, null, p_333971_);
                        if (fluidPathType != null) return fluidPathType;

                        if (pathtype == PathType.DAMAGE_OTHER) {
                            return PathType.DANGER_OTHER;
                        }

                        if (pathtype == PathType.DAMAGE_FIRE || pathtype == PathType.LAVA) {
                            return PathType.DANGER_FIRE;
                        }

                        if (pathtype == PathType.WATER) {
                            return PathType.WATER_BORDER;
                        }

                        if (pathtype == PathType.DAMAGE_CAUTIOUS) {
                            return PathType.DAMAGE_CAUTIOUS;
                        }
                    }
                }
            }
        }

        return p_333971_;
    }

    protected static PathType m_324497_(BlockGetter p_335222_, BlockPos p_331935_) {
        BlockState blockstate = p_335222_.getBlockState(p_331935_);
        Block block = blockstate.getBlock();

        var type = blockstate.getBlockPathType(p_335222_, p_331935_, null);
        if (type != null) return type;

        if (blockstate.isAir()) {
            return PathType.OPEN;
        } else if (blockstate.is(BlockTags.TRAPDOORS) || blockstate.is(Blocks.LILY_PAD) || blockstate.is(Blocks.BIG_DRIPLEAF)) {
            return PathType.TRAPDOOR;
        } else if (blockstate.is(Blocks.POWDER_SNOW)) {
            return PathType.POWDER_SNOW;
        } else if (blockstate.is(Blocks.CACTUS) || blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
            return PathType.DAMAGE_OTHER;
        } else if (blockstate.is(Blocks.HONEY_BLOCK)) {
            return PathType.STICKY_HONEY;
        } else if (blockstate.is(Blocks.COCOA)) {
            return PathType.COCOA;
        } else if (!blockstate.is(Blocks.WITHER_ROSE) && !blockstate.is(Blocks.POINTED_DRIPSTONE)) {
            FluidState fluidstate = blockstate.getFluidState();
            var nonLoggableFluidPathType = fluidstate.getBlockPathType(p_335222_, p_331935_, null, false);
            if (nonLoggableFluidPathType != null) return nonLoggableFluidPathType;
            if (fluidstate.is(FluidTags.LAVA)) {
                return PathType.LAVA;
            } else if (m_321676_(blockstate)) {
                return PathType.DAMAGE_FIRE;
            } else if (block instanceof DoorBlock doorblock) {
                if (blockstate.getValue(DoorBlock.OPEN)) {
                    return PathType.DOOR_OPEN;
                } else {
                    return doorblock.type().canOpenByHand() ? PathType.DOOR_WOOD_CLOSED : PathType.DOOR_IRON_CLOSED;
                }
            } else if (block instanceof BaseRailBlock) {
                return PathType.RAIL;
            } else if (block instanceof LeavesBlock) {
                return PathType.LEAVES;
            } else if (!blockstate.is(BlockTags.FENCES)
                && !blockstate.is(BlockTags.WALLS)
                && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
                if (!blockstate.isPathfindable(PathComputationType.LAND)) {
                    return PathType.BLOCKED;
                } else {
                    var loggableFluidPathType = fluidstate.getBlockPathType(p_335222_, p_331935_, null, true);
                    if (loggableFluidPathType != null) return loggableFluidPathType;
                    return fluidstate.is(FluidTags.WATER) ? PathType.WATER : PathType.OPEN;
                }
            } else {
                return PathType.FENCE;
            }
        } else {
            return PathType.DAMAGE_CAUTIOUS;
        }
    }
}
