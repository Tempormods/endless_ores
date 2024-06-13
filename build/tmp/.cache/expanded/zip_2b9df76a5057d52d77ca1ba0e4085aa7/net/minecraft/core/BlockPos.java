package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
    public static final Codec<BlockPos> CODEC = Codec.INT_STREAM
        .<BlockPos>comapFlatMap(
            p_325638_ -> Util.fixedSize(p_325638_, 3).map(p_175270_ -> new BlockPos(p_175270_[0], p_175270_[1], p_175270_[2])),
            p_121924_ -> IntStream.of(p_121924_.getX(), p_121924_.getY(), p_121924_.getZ())
        )
        .stable();
    public static final StreamCodec<ByteBuf, BlockPos> f_316462_ = new StreamCodec<ByteBuf, BlockPos>() {
        public BlockPos m_318688_(ByteBuf p_335731_) {
            return FriendlyByteBuf.m_319748_(p_335731_);
        }

        public void m_318638_(ByteBuf p_329093_, BlockPos p_330029_) {
            FriendlyByteBuf.m_323314_(p_329093_, p_330029_);
        }
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);
    private static final int PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
    private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
    public static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
    private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
    private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
    private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET = PACKED_Y_LENGTH;
    private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

    public BlockPos(int pX, int pY, int pZ) {
        super(pX, pY, pZ);
    }

    public BlockPos(Vec3i pVector) {
        this(pVector.getX(), pVector.getY(), pVector.getZ());
    }

    public static long offset(long pPos, Direction pDirection) {
        return offset(pPos, pDirection.getStepX(), pDirection.getStepY(), pDirection.getStepZ());
    }

    public static long offset(long pPos, int pDx, int pDy, int pDz) {
        return asLong(getX(pPos) + pDx, getY(pPos) + pDy, getZ(pPos) + pDz);
    }

    public static int getX(long pPackedPos) {
        return (int)(pPackedPos << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    public static int getY(long pPackedPos) {
        return (int)(pPackedPos << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    public static int getZ(long pPackedPos) {
        return (int)(pPackedPos << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public static BlockPos of(long pPackedPos) {
        return new BlockPos(getX(pPackedPos), getY(pPackedPos), getZ(pPackedPos));
    }

    public static BlockPos containing(double pX, double pY, double pZ) {
        return new BlockPos(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
    }

    public static BlockPos containing(Position pPosition) {
        return containing(pPosition.x(), pPosition.y(), pPosition.z());
    }

    public static BlockPos m_319889_(BlockPos p_328564_, BlockPos p_328313_) {
        return new BlockPos(
            Math.min(p_328564_.getX(), p_328313_.getX()),
            Math.min(p_328564_.getY(), p_328313_.getY()),
            Math.min(p_328564_.getZ(), p_328313_.getZ())
        );
    }

    public static BlockPos m_323725_(BlockPos p_330903_, BlockPos p_332595_) {
        return new BlockPos(
            Math.max(p_330903_.getX(), p_332595_.getX()),
            Math.max(p_330903_.getY(), p_332595_.getY()),
            Math.max(p_330903_.getZ(), p_332595_.getZ())
        );
    }

    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int pX, int pY, int pZ) {
        long i = 0L;
        i |= ((long)pX & PACKED_X_MASK) << X_OFFSET;
        i |= ((long)pY & PACKED_Y_MASK) << 0;
        return i | ((long)pZ & PACKED_Z_MASK) << Z_OFFSET;
    }

    public static long getFlatIndex(long pPackedPos) {
        return pPackedPos & -16L;
    }

    public BlockPos offset(int pDx, int pDy, int pDz) {
        return pDx == 0 && pDy == 0 && pDz == 0
            ? this
            : new BlockPos(this.getX() + pDx, this.getY() + pDy, this.getZ() + pDz);
    }

    public Vec3 getCenter() {
        return Vec3.atCenterOf(this);
    }

    public BlockPos offset(Vec3i pVector) {
        return this.offset(pVector.getX(), pVector.getY(), pVector.getZ());
    }

    public BlockPos subtract(Vec3i pVector) {
        return this.offset(-pVector.getX(), -pVector.getY(), -pVector.getZ());
    }

    public BlockPos multiply(int pScalar) {
        if (pScalar == 1) {
            return this;
        } else {
            return pScalar == 0 ? ZERO : new BlockPos(this.getX() * pScalar, this.getY() * pScalar, this.getZ() * pScalar);
        }
    }

    public BlockPos above() {
        return this.relative(Direction.UP);
    }

    public BlockPos above(int pDistance) {
        return this.relative(Direction.UP, pDistance);
    }

    public BlockPos below() {
        return this.relative(Direction.DOWN);
    }

    public BlockPos below(int pDistance) {
        return this.relative(Direction.DOWN, pDistance);
    }

    public BlockPos north() {
        return this.relative(Direction.NORTH);
    }

    public BlockPos north(int pDistance) {
        return this.relative(Direction.NORTH, pDistance);
    }

    public BlockPos south() {
        return this.relative(Direction.SOUTH);
    }

    public BlockPos south(int pDistance) {
        return this.relative(Direction.SOUTH, pDistance);
    }

    public BlockPos west() {
        return this.relative(Direction.WEST);
    }

    public BlockPos west(int pDistance) {
        return this.relative(Direction.WEST, pDistance);
    }

    public BlockPos east() {
        return this.relative(Direction.EAST);
    }

    public BlockPos east(int pDistance) {
        return this.relative(Direction.EAST, pDistance);
    }

    public BlockPos relative(Direction pDirection) {
        return new BlockPos(this.getX() + pDirection.getStepX(), this.getY() + pDirection.getStepY(), this.getZ() + pDirection.getStepZ());
    }

    public BlockPos relative(Direction pDirection, int pDistance) {
        return pDistance == 0
            ? this
            : new BlockPos(
                this.getX() + pDirection.getStepX() * pDistance,
                this.getY() + pDirection.getStepY() * pDistance,
                this.getZ() + pDirection.getStepZ() * pDistance
            );
    }

    public BlockPos relative(Direction.Axis pAxis, int pAmount) {
        if (pAmount == 0) {
            return this;
        } else {
            int i = pAxis == Direction.Axis.X ? pAmount : 0;
            int j = pAxis == Direction.Axis.Y ? pAmount : 0;
            int k = pAxis == Direction.Axis.Z ? pAmount : 0;
            return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
        }
    }

    public BlockPos rotate(Rotation pRotation) {
        switch (pRotation) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180:
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPos(this.getZ(), this.getY(), -this.getX());
        }
    }

    public BlockPos cross(Vec3i pVector) {
        return new BlockPos(
            this.getY() * pVector.getZ() - this.getZ() * pVector.getY(),
            this.getZ() * pVector.getX() - this.getX() * pVector.getZ(),
            this.getX() * pVector.getY() - this.getY() * pVector.getX()
        );
    }

    public BlockPos atY(int pY) {
        return new BlockPos(this.getX(), pY, this.getZ());
    }

    public BlockPos immutable() {
        return this;
    }

    public BlockPos.MutableBlockPos mutable() {
        return new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPos> randomInCube(RandomSource pRandom, int pAmount, BlockPos pCenter, int pRadius) {
        return randomBetweenClosed(
            pRandom,
            pAmount,
            pCenter.getX() - pRadius,
            pCenter.getY() - pRadius,
            pCenter.getZ() - pRadius,
            pCenter.getX() + pRadius,
            pCenter.getY() + pRadius,
            pCenter.getZ() + pRadius
        );
    }

    @Deprecated
    public static Stream<BlockPos> squareOutSouthEast(BlockPos pPos) {
        return Stream.of(pPos, pPos.south(), pPos.east(), pPos.south().east());
    }

    public static Iterable<BlockPos> randomBetweenClosed(
        RandomSource pRandom, int pAmount, int pMinX, int pMinY, int pMinZ, int pMaxX, int pMaxY, int pMaxZ
    ) {
        int i = pMaxX - pMinX + 1;
        int j = pMaxY - pMinY + 1;
        int k = pMaxZ - pMinZ + 1;
        return () -> new AbstractIterator<BlockPos>() {
                final BlockPos.MutableBlockPos f_315241_ = new BlockPos.MutableBlockPos();
                int f_314282_ = pAmount;

                protected BlockPos computeNext() {
                    if (this.f_314282_ <= 0) {
                        return this.endOfData();
                    } else {
                        BlockPos blockpos = this.f_315241_
                            .set(pMinX + pRandom.nextInt(i), pMinY + pRandom.nextInt(j), pMinZ + pRandom.nextInt(k));
                        this.f_314282_--;
                        return blockpos;
                    }
                }
            };
    }

    public static Iterable<BlockPos> withinManhattan(BlockPos pPos, int pXSize, int pYSize, int pZSize) {
        int i = pXSize + pYSize + pZSize;
        int j = pPos.getX();
        int k = pPos.getY();
        int l = pPos.getZ();
        return () -> new AbstractIterator<BlockPos>() {
                private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
                private int f_316549_;
                private int f_315209_;
                private int f_314062_;
                private int f_316284_;
                private int f_314412_;
                private boolean f_314090_;

                protected BlockPos computeNext() {
                    if (this.f_314090_) {
                        this.f_314090_ = false;
                        this.cursor.setZ(l - (this.cursor.getZ() - l));
                        return this.cursor;
                    } else {
                        BlockPos blockpos;
                        for (blockpos = null; blockpos == null; this.f_314412_++) {
                            if (this.f_314412_ > this.f_314062_) {
                                this.f_316284_++;
                                if (this.f_316284_ > this.f_315209_) {
                                    this.f_316549_++;
                                    if (this.f_316549_ > i) {
                                        return this.endOfData();
                                    }

                                    this.f_315209_ = Math.min(pXSize, this.f_316549_);
                                    this.f_316284_ = -this.f_315209_;
                                }

                                this.f_314062_ = Math.min(pYSize, this.f_316549_ - Math.abs(this.f_316284_));
                                this.f_314412_ = -this.f_314062_;
                            }

                            int i1 = this.f_316284_;
                            int j1 = this.f_314412_;
                            int k1 = this.f_316549_ - Math.abs(i1) - Math.abs(j1);
                            if (k1 <= pZSize) {
                                this.f_314090_ = k1 != 0;
                                blockpos = this.cursor.set(j + i1, k + j1, l + k1);
                            }
                        }

                        return blockpos;
                    }
                }
            };
    }

    public static Optional<BlockPos> findClosestMatch(BlockPos pPos, int pWidth, int pHeight, Predicate<BlockPos> pPosFilter) {
        for (BlockPos blockpos : withinManhattan(pPos, pWidth, pHeight, pWidth)) {
            if (pPosFilter.test(blockpos)) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    public static Stream<BlockPos> withinManhattanStream(BlockPos pPos, int pXSize, int pYSize, int pZSize) {
        return StreamSupport.stream(withinManhattan(pPos, pXSize, pYSize, pZSize).spliterator(), false);
    }

    public static Iterable<BlockPos> betweenClosed(BlockPos pFirstPos, BlockPos pSecondPos) {
        return betweenClosed(
            Math.min(pFirstPos.getX(), pSecondPos.getX()),
            Math.min(pFirstPos.getY(), pSecondPos.getY()),
            Math.min(pFirstPos.getZ(), pSecondPos.getZ()),
            Math.max(pFirstPos.getX(), pSecondPos.getX()),
            Math.max(pFirstPos.getY(), pSecondPos.getY()),
            Math.max(pFirstPos.getZ(), pSecondPos.getZ())
        );
    }

    public static Stream<BlockPos> betweenClosedStream(BlockPos pFirstPos, BlockPos pSecondPos) {
        return StreamSupport.stream(betweenClosed(pFirstPos, pSecondPos).spliterator(), false);
    }

    public static Stream<BlockPos> betweenClosedStream(BoundingBox pBox) {
        return betweenClosedStream(
            Math.min(pBox.minX(), pBox.maxX()),
            Math.min(pBox.minY(), pBox.maxY()),
            Math.min(pBox.minZ(), pBox.maxZ()),
            Math.max(pBox.minX(), pBox.maxX()),
            Math.max(pBox.minY(), pBox.maxY()),
            Math.max(pBox.minZ(), pBox.maxZ())
        );
    }

    public static Stream<BlockPos> betweenClosedStream(AABB pAabb) {
        return betweenClosedStream(
            Mth.floor(pAabb.minX),
            Mth.floor(pAabb.minY),
            Mth.floor(pAabb.minZ),
            Mth.floor(pAabb.maxX),
            Mth.floor(pAabb.maxY),
            Mth.floor(pAabb.maxZ)
        );
    }

    public static Stream<BlockPos> betweenClosedStream(int pMinX, int pMinY, int pMinZ, int pMaxX, int pMaxY, int pMaxZ) {
        return StreamSupport.stream(betweenClosed(pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> betweenClosed(int pX1, int pY1, int pZ1, int pX2, int pY2, int pZ2) {
        int i = pX2 - pX1 + 1;
        int j = pY2 - pY1 + 1;
        int k = pZ2 - pZ1 + 1;
        int l = i * j * k;
        return () -> new AbstractIterator<BlockPos>() {
                private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
                private int f_315569_;

                protected BlockPos computeNext() {
                    if (this.f_315569_ == l) {
                        return this.endOfData();
                    } else {
                        int i1 = this.f_315569_ % i;
                        int j1 = this.f_315569_ / i;
                        int k1 = j1 % j;
                        int l1 = j1 / j;
                        this.f_315569_++;
                        return this.cursor.set(pX1 + i1, pY1 + k1, pZ1 + l1);
                    }
                }
            };
    }

    public static Iterable<BlockPos.MutableBlockPos> spiralAround(BlockPos pCenter, int pSize, Direction pRotationDirection, Direction pExpansionDirection) {
        Validate.validState(pRotationDirection.getAxis() != pExpansionDirection.getAxis(), "The two directions cannot be on the same axis");
        return () -> new AbstractIterator<BlockPos.MutableBlockPos>() {
                private final Direction[] f_315103_ = new Direction[]{pRotationDirection, pExpansionDirection, pRotationDirection.getOpposite(), pExpansionDirection.getOpposite()};
                private final BlockPos.MutableBlockPos f_315358_ = pCenter.mutable().move(pExpansionDirection);
                private final int f_315166_ = 4 * pSize;
                private int f_315178_ = -1;
                private int f_315100_;
                private int f_314192_;
                private int f_315864_ = this.f_315358_.getX();
                private int f_316054_ = this.f_315358_.getY();
                private int f_314569_ = this.f_315358_.getZ();

                protected BlockPos.MutableBlockPos computeNext() {
                    this.f_315358_.set(this.f_315864_, this.f_316054_, this.f_314569_).move(this.f_315103_[(this.f_315178_ + 4) % 4]);
                    this.f_315864_ = this.f_315358_.getX();
                    this.f_316054_ = this.f_315358_.getY();
                    this.f_314569_ = this.f_315358_.getZ();
                    if (this.f_314192_ >= this.f_315100_) {
                        if (this.f_315178_ >= this.f_315166_) {
                            return this.endOfData();
                        }

                        this.f_315178_++;
                        this.f_314192_ = 0;
                        this.f_315100_ = this.f_315178_ / 2 + 1;
                    }

                    this.f_314192_++;
                    return this.f_315358_;
                }
            };
    }

    public static int breadthFirstTraversal(
        BlockPos pStartPos, int pDepth, int pVisitLimit, BiConsumer<BlockPos, Consumer<BlockPos>> pAction, Predicate<BlockPos> pPredicate
    ) {
        Queue<Pair<BlockPos, Integer>> queue = new ArrayDeque<>();
        LongSet longset = new LongOpenHashSet();
        queue.add(Pair.of(pStartPos, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockpos = pair.getLeft();
            int j = pair.getRight();
            long k = blockpos.asLong();
            if (longset.add(k) && pPredicate.test(blockpos)) {
                if (++i >= pVisitLimit) {
                    return i;
                }

                if (j < pDepth) {
                    pAction.accept(blockpos, p_277234_ -> queue.add(Pair.of(p_277234_, j + 1)));
                }
            }
        }

        return i;
    }

    public static class MutableBlockPos extends BlockPos {
        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(int pX, int pY, int pZ) {
            super(pX, pY, pZ);
        }

        public MutableBlockPos(double pX, double pY, double pZ) {
            this(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
        }

        @Override
        public BlockPos offset(int pDx, int pDy, int pDz) {
            return super.offset(pDx, pDy, pDz).immutable();
        }

        @Override
        public BlockPos multiply(int pScalar) {
            return super.multiply(pScalar).immutable();
        }

        @Override
        public BlockPos relative(Direction pDirection, int pDistance) {
            return super.relative(pDirection, pDistance).immutable();
        }

        @Override
        public BlockPos relative(Direction.Axis pAxis, int pAmount) {
            return super.relative(pAxis, pAmount).immutable();
        }

        @Override
        public BlockPos rotate(Rotation pRotation) {
            return super.rotate(pRotation).immutable();
        }

        public BlockPos.MutableBlockPos set(int pX, int pY, int pZ) {
            this.setX(pX);
            this.setY(pY);
            this.setZ(pZ);
            return this;
        }

        public BlockPos.MutableBlockPos set(double pX, double pY, double pZ) {
            return this.set(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
        }

        public BlockPos.MutableBlockPos set(Vec3i pVector) {
            return this.set(pVector.getX(), pVector.getY(), pVector.getZ());
        }

        public BlockPos.MutableBlockPos set(long pPackedPos) {
            return this.set(getX(pPackedPos), getY(pPackedPos), getZ(pPackedPos));
        }

        public BlockPos.MutableBlockPos set(AxisCycle pCycle, int pX, int pY, int pZ) {
            return this.set(
                pCycle.cycle(pX, pY, pZ, Direction.Axis.X),
                pCycle.cycle(pX, pY, pZ, Direction.Axis.Y),
                pCycle.cycle(pX, pY, pZ, Direction.Axis.Z)
            );
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i pPos, Direction pDirection) {
            return this.set(
                pPos.getX() + pDirection.getStepX(), pPos.getY() + pDirection.getStepY(), pPos.getZ() + pDirection.getStepZ()
            );
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i pVector, int pOffsetX, int pOffsetY, int pOffsetZ) {
            return this.set(pVector.getX() + pOffsetX, pVector.getY() + pOffsetY, pVector.getZ() + pOffsetZ);
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i pPos, Vec3i pOffset) {
            return this.set(
                pPos.getX() + pOffset.getX(), pPos.getY() + pOffset.getY(), pPos.getZ() + pOffset.getZ()
            );
        }

        public BlockPos.MutableBlockPos move(Direction pDirection) {
            return this.move(pDirection, 1);
        }

        public BlockPos.MutableBlockPos move(Direction pDirection, int pN) {
            return this.set(
                this.getX() + pDirection.getStepX() * pN,
                this.getY() + pDirection.getStepY() * pN,
                this.getZ() + pDirection.getStepZ() * pN
            );
        }

        public BlockPos.MutableBlockPos move(int pX, int pY, int pZ) {
            return this.set(this.getX() + pX, this.getY() + pY, this.getZ() + pZ);
        }

        public BlockPos.MutableBlockPos move(Vec3i pOffset) {
            return this.set(this.getX() + pOffset.getX(), this.getY() + pOffset.getY(), this.getZ() + pOffset.getZ());
        }

        public BlockPos.MutableBlockPos clamp(Direction.Axis pAxis, int pMin, int pMax) {
            switch (pAxis) {
                case X:
                    return this.set(Mth.clamp(this.getX(), pMin, pMax), this.getY(), this.getZ());
                case Y:
                    return this.set(this.getX(), Mth.clamp(this.getY(), pMin, pMax), this.getZ());
                case Z:
                    return this.set(this.getX(), this.getY(), Mth.clamp(this.getZ(), pMin, pMax));
                default:
                    throw new IllegalStateException("Unable to clamp axis " + pAxis);
            }
        }

        public BlockPos.MutableBlockPos setX(int pX) {
            super.setX(pX);
            return this;
        }

        public BlockPos.MutableBlockPos setY(int pY) {
            super.setY(pY);
            return this;
        }

        public BlockPos.MutableBlockPos setZ(int pZ) {
            super.setZ(pZ);
            return this;
        }

        @Override
        public BlockPos immutable() {
            return new BlockPos(this);
        }
    }
}