package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem.BlockContainerSingleItem {
    private static final int SONG_END_PADDING = 20;
    private ItemStack f_303331_ = ItemStack.EMPTY;
    private int ticksSinceLastEvent;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;

    public JukeboxBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.JUKEBOX, pPos, pBlockState);
    }

    @Override
    protected void m_318667_(CompoundTag p_329712_, HolderLookup.Provider p_330255_) {
        super.m_318667_(p_329712_, p_330255_);
        if (p_329712_.contains("RecordItem", 10)) {
            this.f_303331_ = ItemStack.m_323951_(p_330255_, p_329712_.getCompound("RecordItem")).orElse(ItemStack.EMPTY);
        } else {
            this.f_303331_ = ItemStack.EMPTY;
        }

        this.isPlaying = p_329712_.getBoolean("IsPlaying");
        this.recordStartedTick = p_329712_.getLong("RecordStartTick");
        this.tickCount = p_329712_.getLong("TickCount");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_332390_) {
        super.saveAdditional(pTag, p_332390_);
        if (!this.m_306082_().isEmpty()) {
            pTag.put("RecordItem", this.m_306082_().save(p_332390_));
        }

        pTag.putBoolean("IsPlaying", this.isPlaying);
        pTag.putLong("RecordStartTick", this.recordStartedTick);
        pTag.putLong("TickCount", this.tickCount);
    }

    public boolean isRecordPlaying() {
        return !this.m_306082_().isEmpty() && this.isPlaying;
    }

    private void setHasRecordBlockState(@Nullable Entity pEntity, boolean pHasRecord) {
        if (this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, Boolean.valueOf(pHasRecord)), 2);
            this.level.m_322719_(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(pEntity, this.getBlockState()));
        }
    }

    @VisibleForTesting
    public void startPlaying() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(null, 1010, this.getBlockPos(), Item.getId(this.m_306082_().getItem()));
        this.setChanged();
    }

    private void stopPlaying() {
        this.isPlaying = false;
        this.level.m_322719_(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(1011, this.getBlockPos(), 0);
        this.setChanged();
    }

    private void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        this.ticksSinceLastEvent++;
        if (this.isRecordPlaying() && this.m_306082_().getItem() instanceof RecordItem recorditem) {
            if (this.shouldRecordStopPlaying(recorditem)) {
                this.stopPlaying();
            } else if (this.shouldSendJukeboxPlayingEvent()) {
                this.ticksSinceLastEvent = 0;
                pLevel.m_322719_(GameEvent.JUKEBOX_PLAY, pPos, GameEvent.Context.of(pState));
                this.spawnMusicParticles(pLevel, pPos);
            }
        }

        this.tickCount++;
    }

    private boolean shouldRecordStopPlaying(RecordItem pRecord) {
        return this.tickCount >= this.recordStartedTick + (long)pRecord.getLengthInTicks() + 20L;
    }

    private boolean shouldSendJukeboxPlayingEvent() {
        return this.ticksSinceLastEvent >= 20;
    }

    @Override
    public ItemStack m_306082_() {
        return this.f_303331_;
    }

    @Override
    public ItemStack m_305214_(int p_309876_) {
        ItemStack itemstack = this.f_303331_;
        this.f_303331_ = ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.setHasRecordBlockState(null, false);
            this.stopPlaying();
        }

        return itemstack;
    }

    @Override
    public void m_305072_(ItemStack p_309430_) {
        if (p_309430_.is(ItemTags.MUSIC_DISCS) && this.level != null) {
            this.f_303331_ = p_309430_;
            this.setHasRecordBlockState(null, true);
            this.startPlaying();
        } else if (p_309430_.isEmpty()) {
            this.m_305214_(1);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public BlockEntity m_304707_() {
        return this;
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return pStack.is(ItemTags.MUSIC_DISCS) && this.getItem(pIndex).isEmpty();
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
        return pTarget.hasAnyMatching(ItemStack::isEmpty);
    }

    private void spawnMusicParticles(Level pLevel, BlockPos pPos) {
        if (pLevel instanceof ServerLevel serverlevel) {
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos).add(0.0, 1.2F, 0.0);
            float f = (float)pLevel.getRandom().nextInt(4) / 24.0F;
            serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, (double)f, 0.0, 0.0, 1.0);
        }
    }

    public void popOutRecord() {
        if (this.level != null && !this.level.isClientSide) {
            BlockPos blockpos = this.getBlockPos();
            ItemStack itemstack = this.m_306082_();
            if (!itemstack.isEmpty()) {
                this.m_306595_();
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5, 1.01, 0.5).offsetRandom(this.level.random, 0.7F);
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemstack1);
                itementity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itementity);
            }
        }
    }

    public static void playRecordTick(Level pLevel, BlockPos pPos, BlockState pState, JukeboxBlockEntity pJukebox) {
        pJukebox.tick(pLevel, pPos, pState);
    }

    @VisibleForTesting
    public void setRecordWithoutPlaying(ItemStack pStack) {
        this.f_303331_ = pStack;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }
}