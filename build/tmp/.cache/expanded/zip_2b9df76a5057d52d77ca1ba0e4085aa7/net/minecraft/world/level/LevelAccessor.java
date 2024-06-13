package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface LevelAccessor extends CommonLevelAccessor, LevelTimeAccess {
    @Override
    default long dayTime() {
        return this.getLevelData().getDayTime();
    }

    long nextSubTickCount();

    LevelTickAccess<Block> getBlockTicks();

    private <T> ScheduledTick<T> createTick(BlockPos pPos, T pType, int pDelay, TickPriority pPriority) {
        return new ScheduledTick<>(pType, pPos, this.getLevelData().getGameTime() + (long)pDelay, pPriority, this.nextSubTickCount());
    }

    private <T> ScheduledTick<T> createTick(BlockPos pPos, T pType, int pDelay) {
        return new ScheduledTick<>(pType, pPos, this.getLevelData().getGameTime() + (long)pDelay, this.nextSubTickCount());
    }

    default void scheduleTick(BlockPos pPos, Block pBlock, int pDelay, TickPriority pPriority) {
        this.getBlockTicks().schedule(this.createTick(pPos, pBlock, pDelay, pPriority));
    }

    default void scheduleTick(BlockPos pPos, Block pBlock, int pDelay) {
        this.getBlockTicks().schedule(this.createTick(pPos, pBlock, pDelay));
    }

    LevelTickAccess<Fluid> getFluidTicks();

    default void scheduleTick(BlockPos pPos, Fluid pFluid, int pDelay, TickPriority pPriority) {
        this.getFluidTicks().schedule(this.createTick(pPos, pFluid, pDelay, pPriority));
    }

    default void scheduleTick(BlockPos pPos, Fluid pFluid, int pDelay) {
        this.getFluidTicks().schedule(this.createTick(pPos, pFluid, pDelay));
    }

    LevelData getLevelData();

    DifficultyInstance getCurrentDifficultyAt(BlockPos pPos);

    @Nullable
    MinecraftServer getServer();

    default Difficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }

    ChunkSource getChunkSource();

    @Override
    default boolean hasChunk(int pChunkX, int pChunkZ) {
        return this.getChunkSource().hasChunk(pChunkX, pChunkZ);
    }

    RandomSource getRandom();

    default void blockUpdated(BlockPos pPos, Block pBlock) {
    }

    default void neighborShapeChanged(Direction pDirection, BlockState pQueried, BlockPos pPos, BlockPos pOffsetPos, int pFlags, int pRecursionLevel) {
        NeighborUpdater.executeShapeUpdate(this, pDirection, pQueried, pPos, pOffsetPos, pFlags, pRecursionLevel - 1);
    }

    default void playSound(@Nullable Player pPlayer, BlockPos pPos, SoundEvent pSound, SoundSource pSource) {
        this.playSound(pPlayer, pPos, pSound, pSource, 1.0F, 1.0F);
    }

    void playSound(@Nullable Player pPlayer, BlockPos pPos, SoundEvent pSound, SoundSource pSource, float pVolume, float pPitch);

    void addParticle(ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed);

    void levelEvent(@Nullable Player pPlayer, int pType, BlockPos pPos, int pData);

    default void levelEvent(int pType, BlockPos pPos, int pData) {
        this.levelEvent(null, pType, pPos, pData);
    }

    void gameEvent(Holder<GameEvent> p_330236_, Vec3 pPosition, GameEvent.Context pContext);

    default void gameEvent(@Nullable Entity pEntity, Holder<GameEvent> p_334728_, Vec3 pPosition) {
        this.gameEvent(p_334728_, pPosition, new GameEvent.Context(pEntity, null));
    }

    default void gameEvent(@Nullable Entity pEntity, Holder<GameEvent> p_331725_, BlockPos pPos) {
        this.m_322719_(p_331725_, pPos, new GameEvent.Context(pEntity, null));
    }

    default void m_322719_(Holder<GameEvent> p_333522_, BlockPos p_334961_, GameEvent.Context p_335744_) {
        this.gameEvent(p_333522_, Vec3.atCenterOf(p_334961_), p_335744_);
    }

    default void gameEvent(ResourceKey<GameEvent> p_332741_, BlockPos pPos, GameEvent.Context pContext) {
        this.m_322719_(this.registryAccess().registryOrThrow(Registries.GAME_EVENT).getHolderOrThrow(p_332741_), pPos, pContext);
    }
}