package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private VibrationSystem.Data vibrationData;
    private final VibrationSystem.Listener vibrationListener;
    private final VibrationSystem.User vibrationUser = this.createVibrationUser();
    private int lastVibrationFrequency;

    protected SculkSensorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.vibrationData = new VibrationSystem.Data();
        this.vibrationListener = new VibrationSystem.Listener(this);
    }

    public SculkSensorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(BlockEntityType.SCULK_SENSOR, pPos, pBlockState);
    }

    public VibrationSystem.User createVibrationUser() {
        return new SculkSensorBlockEntity.VibrationUser(this.getBlockPos());
    }

    @Override
    protected void m_318667_(CompoundTag p_334658_, HolderLookup.Provider p_335301_) {
        super.m_318667_(p_334658_, p_335301_);
        this.lastVibrationFrequency = p_334658_.getInt("last_vibration_frequency");
        if (p_334658_.contains("listener", 10)) {
            VibrationSystem.Data.CODEC
                .parse(new Dynamic<>(NbtOps.INSTANCE, p_334658_.getCompound("listener")))
                .resultOrPartial(LOGGER::error)
                .ifPresent(p_281146_ -> this.vibrationData = p_281146_);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_327837_) {
        super.saveAdditional(pTag, p_327837_);
        pTag.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        VibrationSystem.Data.CODEC
            .encodeStart(NbtOps.INSTANCE, this.vibrationData)
            .resultOrPartial(LOGGER::error)
            .ifPresent(p_222820_ -> pTag.put("listener", p_222820_));
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int pLastVibrationFrequency) {
        this.lastVibrationFrequency = pLastVibrationFrequency;
    }

    public VibrationSystem.Listener m_280221_() {
        return this.vibrationListener;
    }

    protected class VibrationUser implements VibrationSystem.User {
        public static final int LISTENER_RANGE = 8;
        protected final BlockPos blockPos;
        private final PositionSource positionSource;

        public VibrationUser(final BlockPos pPos) {
            this.blockPos = pPos;
            this.positionSource = new BlockPositionSource(pPos);
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean canTriggerAvoidVibration() {
            return true;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel pLevel, BlockPos pPos, Holder<GameEvent> p_329159_, @Nullable GameEvent.Context pContext) {
            return !pPos.equals(this.blockPos) || !p_329159_.m_318604_(GameEvent.BLOCK_DESTROY) && !p_329159_.m_318604_(GameEvent.BLOCK_PLACE)
                ? SculkSensorBlock.canActivate(SculkSensorBlockEntity.this.getBlockState())
                : false;
        }

        @Override
        public void onReceiveVibration(
            ServerLevel pLevel, BlockPos pPos, Holder<GameEvent> p_331761_, @Nullable Entity pEntity, @Nullable Entity pPlayerEntity, float pDistance
        ) {
            BlockState blockstate = SculkSensorBlockEntity.this.getBlockState();
            if (SculkSensorBlock.canActivate(blockstate)) {
                SculkSensorBlockEntity.this.setLastVibrationFrequency(VibrationSystem.m_320356_(p_331761_));
                int i = VibrationSystem.getRedstoneStrengthForDistance(pDistance, this.getListenerRadius());
                if (blockstate.getBlock() instanceof SculkSensorBlock sculksensorblock) {
                    sculksensorblock.activate(pEntity, pLevel, this.blockPos, blockstate, i, SculkSensorBlockEntity.this.getLastVibrationFrequency());
                }
            }
        }

        @Override
        public void onDataChanged() {
            SculkSensorBlockEntity.this.setChanged();
        }

        @Override
        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}