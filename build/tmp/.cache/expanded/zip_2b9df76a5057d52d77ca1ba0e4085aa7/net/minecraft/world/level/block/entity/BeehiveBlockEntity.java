package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.slf4j.Logger;

public class BeehiveBlockEntity extends BlockEntity {
    private static final Logger f_316080_ = LogUtils.getLogger();
    private static final String TAG_FLOWER_POS = "flower_pos";
    private static final String BEES = "bees";
    static final List<String> IGNORED_BEE_TAGS = Arrays.asList(
        "Air",
        "ArmorDropChances",
        "ArmorItems",
        "Brain",
        "CanPickUpLoot",
        "DeathTime",
        "FallDistance",
        "FallFlying",
        "Fire",
        "HandDropChances",
        "HandItems",
        "HurtByTimestamp",
        "HurtTime",
        "LeftHanded",
        "Motion",
        "NoGravity",
        "OnGround",
        "PortalCooldown",
        "Pos",
        "Rotation",
        "SleepingX",
        "SleepingY",
        "SleepingZ",
        "CannotEnterHiveTicks",
        "TicksSincePollination",
        "CropsGrownSincePollination",
        "hive_pos",
        "Passengers",
        "leash",
        "UUID"
    );
    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<BeehiveBlockEntity.BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;

    public BeehiveBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.BEEHIVE, pPos, pBlockState);
    }

    @Override
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive(null, this.level.getBlockState(this.getBlockPos()), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }

        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        } else {
            for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
                if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    public void emptyAllLivingFromHive(@Nullable Player pPlayer, BlockState pState, BeehiveBlockEntity.BeeReleaseStatus pReleaseStatus) {
        List<Entity> list = this.releaseAllOccupants(pState, pReleaseStatus);
        if (pPlayer != null) {
            for (Entity entity : list) {
                if (entity instanceof Bee) {
                    Bee bee = (Bee)entity;
                    if (pPlayer.position().distanceToSqr(entity.position()) <= 16.0) {
                        if (!this.isSedated()) {
                            bee.setTarget(pPlayer);
                        } else {
                            bee.setStayOutOfHiveCountdown(400);
                        }
                    }
                }
            }
        }
    }

    private List<Entity> releaseAllOccupants(BlockState pState, BeehiveBlockEntity.BeeReleaseStatus pReleaseStatus) {
        List<Entity> list = Lists.newArrayList();
        this.stored.removeIf(p_327282_ -> releaseOccupant(this.level, this.worldPosition, pState, p_327282_.m_323874_(), list, pReleaseStatus, this.savedFlowerPos));
        if (!list.isEmpty()) {
            super.setChanged();
        }

        return list;
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState pState) {
        return pState.getValue(BeehiveBlock.HONEY_LEVEL);
    }

    @VisibleForDebug
    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public void addOccupant(Entity pOccupant) {
        if (this.stored.size() < 3) {
            pOccupant.stopRiding();
            pOccupant.ejectPassengers();
            this.storeBee(BeehiveBlockEntity.Occupant.m_323911_(pOccupant));
            if (this.level != null) {
                if (pOccupant instanceof Bee bee && bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                    this.savedFlowerPos = bee.getSavedFlowerPos();
                }

                BlockPos blockpos = this.getBlockPos();
                this.level
                    .playSound(
                        null,
                        (double)blockpos.getX(),
                        (double)blockpos.getY(),
                        (double)blockpos.getZ(),
                        SoundEvents.BEEHIVE_ENTER,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                    );
                this.level.m_322719_(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(pOccupant, this.getBlockState()));
            }

            pOccupant.discard();
            super.setChanged();
        }
    }

    public void storeBee(BeehiveBlockEntity.Occupant p_329282_) {
        this.stored.add(new BeehiveBlockEntity.BeeData(p_329282_));
    }

    private static boolean releaseOccupant(
        Level pLevel,
        BlockPos pPos,
        BlockState pState,
        BeehiveBlockEntity.Occupant p_335681_,
        @Nullable List<Entity> pStoredInHives,
        BeehiveBlockEntity.BeeReleaseStatus pReleaseStatus,
        @Nullable BlockPos pSavedFlowerPos
    ) {
        if ((pLevel.isNight() || pLevel.isRaining()) && pReleaseStatus != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
        } else {
            Direction direction = pState.getValue(BeehiveBlock.FACING);
            BlockPos blockpos = pPos.relative(direction);
            boolean flag = !pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos).isEmpty();
            if (flag && pReleaseStatus != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
                return false;
            } else {
                Entity entity = p_335681_.m_319900_(pLevel, pPos);
                if (entity != null) {
                    if (entity instanceof Bee bee) {
                        if (pSavedFlowerPos != null && !bee.hasSavedFlowerPos() && pLevel.random.nextFloat() < 0.9F) {
                            bee.setSavedFlowerPos(pSavedFlowerPos);
                        }

                        if (pReleaseStatus == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                            bee.dropOffNectar();
                            if (pState.is(BlockTags.BEEHIVES, p_202037_ -> p_202037_.hasProperty(BeehiveBlock.HONEY_LEVEL))) {
                                int i = getHoneyLevel(pState);
                                if (i < 5) {
                                    int j = pLevel.random.nextInt(100) == 0 ? 2 : 1;
                                    if (i + j > 5) {
                                        j--;
                                    }

                                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
                                }
                            }
                        }

                        if (pStoredInHives != null) {
                            pStoredInHives.add(bee);
                        }

                        float f = entity.getBbWidth();
                        double d3 = flag ? 0.0 : 0.55 + (double)(f / 2.0F);
                        double d0 = (double)pPos.getX() + 0.5 + d3 * (double)direction.getStepX();
                        double d1 = (double)pPos.getY() + 0.5 - (double)(entity.getBbHeight() / 2.0F);
                        double d2 = (double)pPos.getZ() + 0.5 + d3 * (double)direction.getStepZ();
                        entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                    }

                    pLevel.playSound(null, pPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.m_322719_(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(entity, pLevel.getBlockState(pPos)));
                    return pLevel.addFreshEntity(entity);
                } else {
                    return false;
                }
            }
        }
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private static void tickOccupants(
        Level pLevel, BlockPos pPos, BlockState pState, List<BeehiveBlockEntity.BeeData> pData, @Nullable BlockPos pSavedFlowerPos
    ) {
        boolean flag = false;
        Iterator<BeehiveBlockEntity.BeeData> iterator = pData.iterator();

        while (iterator.hasNext()) {
            BeehiveBlockEntity.BeeData beehiveblockentity$beedata = iterator.next();
            if (beehiveblockentity$beedata.m_320808_()) {
                BeehiveBlockEntity.BeeReleaseStatus beehiveblockentity$beereleasestatus = beehiveblockentity$beedata.m_322251_()
                    ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED
                    : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                if (releaseOccupant(pLevel, pPos, pState, beehiveblockentity$beedata.m_323874_(), null, beehiveblockentity$beereleasestatus, pSavedFlowerPos)) {
                    flag = true;
                    iterator.remove();
                }
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, BeehiveBlockEntity pBeehive) {
        tickOccupants(pLevel, pPos, pState, pBeehive.stored, pBeehive.savedFlowerPos);
        if (!pBeehive.stored.isEmpty() && pLevel.getRandom().nextDouble() < 0.005) {
            double d0 = (double)pPos.getX() + 0.5;
            double d1 = (double)pPos.getY();
            double d2 = (double)pPos.getZ() + 0.5;
            pLevel.playSound(null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        DebugPackets.sendHiveInfo(pLevel, pPos, pState, pBeehive);
    }

    @Override
    protected void m_318667_(CompoundTag p_333420_, HolderLookup.Provider p_335311_) {
        super.m_318667_(p_333420_, p_335311_);
        this.stored.clear();
        if (p_333420_.contains("bees")) {
            BeehiveBlockEntity.Occupant.f_314670_
                .parse(NbtOps.INSTANCE, p_333420_.get("bees"))
                .resultOrPartial(p_327283_ -> f_316080_.error("Failed to parse bees: '{}'", p_327283_))
                .ifPresent(p_327284_ -> p_327284_.forEach(this::storeBee));
        }

        this.savedFlowerPos = NbtUtils.readBlockPos(p_333420_, "flower_pos").orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_332762_) {
        super.saveAdditional(pTag, p_332762_);
        pTag.put("bees", BeehiveBlockEntity.Occupant.f_314670_.encodeStart(NbtOps.INSTANCE, this.m_322480_()).getOrThrow());
        if (this.hasSavedFlowerPos()) {
            pTag.put("flower_pos", NbtUtils.writeBlockPos(this.savedFlowerPos));
        }
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_333166_) {
        super.m_318741_(p_333166_);
        this.stored.clear();
        List<BeehiveBlockEntity.Occupant> list = p_333166_.m_319031_(DataComponents.f_314066_, List.of());
        list.forEach(this::storeBee);
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_328977_) {
        super.m_318837_(p_328977_);
        p_328977_.m_322739_(DataComponents.f_314066_, this.m_322480_());
    }

    @Override
    public void m_318942_(CompoundTag p_329874_) {
        super.m_318942_(p_329874_);
        p_329874_.remove("bees");
    }

    private List<BeehiveBlockEntity.Occupant> m_322480_() {
        return this.stored.stream().map(BeehiveBlockEntity.BeeData::m_323874_).toList();
    }

    static class BeeData {
        private final BeehiveBlockEntity.Occupant f_314157_;
        private int ticksInHive;

        BeeData(BeehiveBlockEntity.Occupant p_336059_) {
            this.f_314157_ = p_336059_;
            this.ticksInHive = p_336059_.f_314644_();
        }

        public boolean m_320808_() {
            return this.ticksInHive++ > this.f_314157_.f_314650_;
        }

        public BeehiveBlockEntity.Occupant m_323874_() {
            return new BeehiveBlockEntity.Occupant(this.f_314157_.f_316778_, this.ticksInHive, this.f_314157_.f_314650_);
        }

        public boolean m_322251_() {
            return this.f_314157_.f_316778_.m_323459_().getBoolean("HasNectar");
        }
    }

    public static enum BeeReleaseStatus {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;
    }

    public static record Occupant(CustomData f_316778_, int f_314644_, int f_314650_) {
        public static final Codec<BeehiveBlockEntity.Occupant> f_314790_ = RecordCodecBuilder.create(
            p_330401_ -> p_330401_.group(
                        CustomData.f_314012_.optionalFieldOf("entity_data", CustomData.f_317060_).forGetter(BeehiveBlockEntity.Occupant::f_316778_),
                        Codec.INT.fieldOf("ticks_in_hive").forGetter(BeehiveBlockEntity.Occupant::f_314644_),
                        Codec.INT.fieldOf("min_ticks_in_hive").forGetter(BeehiveBlockEntity.Occupant::f_314650_)
                    )
                    .apply(p_330401_, BeehiveBlockEntity.Occupant::new)
        );
        public static final Codec<List<BeehiveBlockEntity.Occupant>> f_314670_ = f_314790_.listOf();
        public static final StreamCodec<ByteBuf, BeehiveBlockEntity.Occupant> f_316641_ = StreamCodec.m_321516_(
            CustomData.f_316654_,
            BeehiveBlockEntity.Occupant::f_316778_,
            ByteBufCodecs.f_316730_,
            BeehiveBlockEntity.Occupant::f_314644_,
            ByteBufCodecs.f_316730_,
            BeehiveBlockEntity.Occupant::f_314650_,
            BeehiveBlockEntity.Occupant::new
        );

        public static BeehiveBlockEntity.Occupant m_323911_(Entity p_331052_) {
            CompoundTag compoundtag = new CompoundTag();
            p_331052_.save(compoundtag);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(compoundtag::remove);
            boolean flag = compoundtag.getBoolean("HasNectar");
            return new BeehiveBlockEntity.Occupant(CustomData.m_321102_(compoundtag), 0, flag ? 2400 : 600);
        }

        public static BeehiveBlockEntity.Occupant m_320100_(int p_330047_) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
            return new BeehiveBlockEntity.Occupant(CustomData.m_321102_(compoundtag), p_330047_, 600);
        }

        @Nullable
        public Entity m_319900_(Level p_328931_, BlockPos p_336164_) {
            CompoundTag compoundtag = this.f_316778_.m_323330_();
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(compoundtag::remove);
            Entity entity = EntityType.loadEntityRecursive(compoundtag, p_328931_, p_334152_ -> p_334152_);
            if (entity != null && entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                entity.setNoGravity(true);
                if (entity instanceof Bee bee) {
                    bee.m_320152_(p_336164_);
                    m_319670_(this.f_314644_, bee);
                }

                return entity;
            } else {
                return null;
            }
        }

        private static void m_319670_(int p_330253_, Bee p_331091_) {
            int i = p_331091_.getAge();
            if (i < 0) {
                p_331091_.setAge(Math.min(0, i + p_330253_));
            } else if (i > 0) {
                p_331091_.setAge(Math.max(0, i - p_330253_));
            }

            p_331091_.setInLoveTime(Math.max(0, p_331091_.getInLoveTime() - p_330253_));
        }
    }
}