package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.util.DebugBuffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
    public static final ChunkResult<ChunkAccess> UNLOADED_CHUNK = ChunkResult.m_322259_("Unloaded chunk");
    public static final CompletableFuture<ChunkResult<ChunkAccess>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final ChunkResult<LevelChunk> UNLOADED_LEVEL_CHUNK = ChunkResult.m_322259_("Unloaded level chunk");
    public static final ChunkResult<ChunkAccess> NOT_DONE_YET = ChunkResult.m_322259_("Not done yet");
    private static final CompletableFuture<ChunkResult<LevelChunk>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.m_323667_();
    private final AtomicReferenceArray<CompletableFuture<ChunkResult<ChunkAccess>>> futures = new AtomicReferenceArray<>(CHUNK_STATUSES.size());
    private final LevelHeightAccessor levelHeightAccessor;
    private volatile CompletableFuture<ChunkResult<LevelChunk>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<ChunkResult<LevelChunk>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<ChunkResult<LevelChunk>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private CompletableFuture<ChunkAccess> chunkToSave = CompletableFuture.completedFuture(null);
    @Nullable
    private final DebugBuffer<ChunkHolder.ChunkSaveDebug> chunkToSaveHistory = null;
    private int oldTicketLevel;
    private int ticketLevel;
    private int queueLevel;
    private final ChunkPos pos;
    private boolean hasChangedSections;
    private final ShortSet[] changedBlocksPerSection;
    private final BitSet blockChangedLightSectionFilter = new BitSet();
    private final BitSet skyChangedLightSectionFilter = new BitSet();
    private final LevelLightEngine lightEngine;
    private final ChunkHolder.LevelChangeListener onLevelChange;
    private final ChunkHolder.PlayerProvider playerProvider;
    private boolean wasAccessibleSinceLastSave;
    private CompletableFuture<Void> pendingFullStateConfirmation = CompletableFuture.completedFuture(null);
    private CompletableFuture<?> sendSync = CompletableFuture.completedFuture(null);
    LevelChunk currentlyLoading; // Forge: Used to bypass future chain when loading chunks.

    public ChunkHolder(
        ChunkPos pPos,
        int pTicketLevel,
        LevelHeightAccessor pLevelHeightAccessor,
        LevelLightEngine pLightEngine,
        ChunkHolder.LevelChangeListener pOnLevelChange,
        ChunkHolder.PlayerProvider pPlayerProvider
    ) {
        this.pos = pPos;
        this.levelHeightAccessor = pLevelHeightAccessor;
        this.lightEngine = pLightEngine;
        this.onLevelChange = pOnLevelChange;
        this.playerProvider = pPlayerProvider;
        this.oldTicketLevel = ChunkLevel.MAX_LEVEL + 1;
        this.ticketLevel = this.oldTicketLevel;
        this.queueLevel = this.oldTicketLevel;
        this.setTicketLevel(pTicketLevel);
        this.changedBlocksPerSection = new ShortSet[pLevelHeightAccessor.getSectionsCount()];
    }

    public CompletableFuture<ChunkResult<ChunkAccess>> getFutureIfPresentUnchecked(ChunkStatus p_331856_) {
        CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.futures.get(p_331856_.m_323297_());
        return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
    }

    public CompletableFuture<ChunkResult<ChunkAccess>> getFutureIfPresent(ChunkStatus p_330273_) {
        return ChunkLevel.generationStatus(this.ticketLevel).m_319325_(p_330273_) ? this.getFutureIfPresentUnchecked(p_330273_) : UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<ChunkResult<LevelChunk>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }

    public CompletableFuture<ChunkResult<LevelChunk>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }

    public CompletableFuture<ChunkResult<LevelChunk>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }

    @Nullable
    public LevelChunk getTickingChunk() {
        return this.getTickingChunkFuture().getNow(UNLOADED_LEVEL_CHUNK).m_318814_(null);
    }

    public CompletableFuture<?> getChunkSendSyncFuture() {
        return this.sendSync;
    }

    @Nullable
    public LevelChunk getChunkToSend() {
        return !this.sendSync.isDone() ? null : this.getTickingChunk();
    }

    @Nullable
    public ChunkStatus getLastAvailableStatus() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
            ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
            CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
            if (completablefuture.getNow(UNLOADED_CHUNK).m_321137_()) {
                return chunkstatus;
            }
        }

        return null;
    }

    @Nullable
    public ChunkAccess getLastAvailable() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
            ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
            CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
            if (!completablefuture.isCompletedExceptionally()) {
                ChunkAccess chunkaccess = completablefuture.getNow(UNLOADED_CHUNK).m_318814_(null);
                if (chunkaccess != null) {
                    return chunkaccess;
                }
            }
        }

        return null;
    }

    public CompletableFuture<ChunkAccess> getChunkToSave() {
        return this.chunkToSave;
    }

    public void blockChanged(BlockPos pPos) {
        LevelChunk levelchunk = this.getTickingChunk();
        if (levelchunk != null) {
            int i = this.levelHeightAccessor.getSectionIndex(pPos.getY());
            if (this.changedBlocksPerSection[i] == null) {
                this.hasChangedSections = true;
                this.changedBlocksPerSection[i] = new ShortOpenHashSet();
            }

            this.changedBlocksPerSection[i].add(SectionPos.sectionRelativePos(pPos));
        }
    }

    public void sectionLightChanged(LightLayer pType, int pSectionY) {
        ChunkAccess chunkaccess = this.getFutureIfPresent(ChunkStatus.f_315473_).getNow(UNLOADED_CHUNK).m_318814_(null);
        if (chunkaccess != null) {
            chunkaccess.setUnsaved(true);
            LevelChunk levelchunk = this.getTickingChunk();
            if (levelchunk != null) {
                int i = this.lightEngine.getMinLightSection();
                int j = this.lightEngine.getMaxLightSection();
                if (pSectionY >= i && pSectionY <= j) {
                    int k = pSectionY - i;
                    if (pType == LightLayer.SKY) {
                        this.skyChangedLightSectionFilter.set(k);
                    } else {
                        this.blockChangedLightSectionFilter.set(k);
                    }
                }
            }
        }
    }

    public void broadcastChanges(LevelChunk pChunk) {
        if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            Level level = pChunk.getLevel();
            if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
                List<ServerPlayer> list = this.playerProvider.getPlayers(this.pos, true);
                if (!list.isEmpty()) {
                    ClientboundLightUpdatePacket clientboundlightupdatepacket = new ClientboundLightUpdatePacket(
                        pChunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter
                    );
                    this.broadcast(list, clientboundlightupdatepacket);
                }

                this.skyChangedLightSectionFilter.clear();
                this.blockChangedLightSectionFilter.clear();
            }

            if (this.hasChangedSections) {
                List<ServerPlayer> list1 = this.playerProvider.getPlayers(this.pos, false);

                for (int j = 0; j < this.changedBlocksPerSection.length; j++) {
                    ShortSet shortset = this.changedBlocksPerSection[j];
                    if (shortset != null) {
                        this.changedBlocksPerSection[j] = null;
                        if (!list1.isEmpty()) {
                            int i = this.levelHeightAccessor.getSectionYFromSectionIndex(j);
                            SectionPos sectionpos = SectionPos.of(pChunk.getPos(), i);
                            if (shortset.size() == 1) {
                                BlockPos blockpos = sectionpos.relativeToBlockPos(shortset.iterator().nextShort());
                                BlockState blockstate = level.getBlockState(blockpos);
                                this.broadcast(list1, new ClientboundBlockUpdatePacket(blockpos, blockstate));
                                this.broadcastBlockEntityIfNeeded(list1, level, blockpos, blockstate);
                            } else {
                                LevelChunkSection levelchunksection = pChunk.getSection(j);
                                ClientboundSectionBlocksUpdatePacket clientboundsectionblocksupdatepacket = new ClientboundSectionBlocksUpdatePacket(
                                    sectionpos, shortset, levelchunksection
                                );
                                this.broadcast(list1, clientboundsectionblocksupdatepacket);
                                clientboundsectionblocksupdatepacket.runUpdates((p_288761_, p_288762_) -> this.broadcastBlockEntityIfNeeded(list1, level, p_288761_, p_288762_));
                            }
                        }
                    }
                }

                this.hasChangedSections = false;
            }
        }
    }

    private void broadcastBlockEntityIfNeeded(List<ServerPlayer> pPlayers, Level pLevel, BlockPos pPos, BlockState pState) {
        if (pState.hasBlockEntity()) {
            this.broadcastBlockEntity(pPlayers, pLevel, pPos);
        }
    }

    private void broadcastBlockEntity(List<ServerPlayer> pPlayers, Level pLevel, BlockPos pPox) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPox);
        if (blockentity != null) {
            Packet<?> packet = blockentity.getUpdatePacket();
            if (packet != null) {
                this.broadcast(pPlayers, packet);
            }
        }
    }

    private void broadcast(List<ServerPlayer> pPlayers, Packet<?> pPacket) {
        pPlayers.forEach(p_296560_ -> p_296560_.connection.send(pPacket));
    }

    public CompletableFuture<ChunkResult<ChunkAccess>> getOrScheduleFuture(ChunkStatus p_330909_, ChunkMap pMap) {
        int i = p_330909_.m_323297_();
        CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.futures.get(i);
        if (completablefuture != null) {
            ChunkResult<ChunkAccess> chunkresult = completablefuture.getNow(NOT_DONE_YET);
            if (chunkresult == null) {
                String s = "value in future for status: " + p_330909_ + " was incorrectly set to null at chunk: " + this.pos;
                throw pMap.debugFuturesAndCreateReportedException(new IllegalStateException("null value previously set for chunk status"), s);
            }

            if (chunkresult == NOT_DONE_YET || chunkresult.m_321137_()) {
                return completablefuture;
            }
        }

        if (ChunkLevel.generationStatus(this.ticketLevel).m_319325_(p_330909_)) {
            CompletableFuture<ChunkResult<ChunkAccess>> completablefuture1 = pMap.schedule(this, p_330909_);
            this.updateChunkToSave(completablefuture1, "schedule " + p_330909_);
            this.futures.set(i, completablefuture1);
            return completablefuture1;
        } else {
            return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
        }
    }

    protected void addSaveDependency(String pSource, CompletableFuture<?> pFuture) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new ChunkHolder.ChunkSaveDebug(Thread.currentThread(), pFuture, pSource));
        }

        this.chunkToSave = this.chunkToSave.thenCombine((CompletionStage<? extends Object>)pFuture, (p_200414_, p_200415_) -> (ChunkAccess)p_200414_);
    }

    private void updateChunkToSave(CompletableFuture<? extends ChunkResult<? extends ChunkAccess>> pFeature, String pSource) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new ChunkHolder.ChunkSaveDebug(Thread.currentThread(), pFeature, pSource));
        }

        this.chunkToSave = this.chunkToSave
            .thenCombine(pFeature, (p_326373_, p_326374_) -> ChunkResult.m_319813_((ChunkResult<? extends ChunkAccess>)p_326374_, (ChunkAccess)p_326373_));
    }

    public void addSendDependency(CompletableFuture<?> pDependency) {
        if (this.sendSync.isDone()) {
            this.sendSync = pDependency;
        } else {
            this.sendSync = this.sendSync.thenCombine((CompletionStage<? extends Object>)pDependency, (p_296561_, p_296562_) -> null);
        }
    }

    public FullChunkStatus getFullStatus() {
        return ChunkLevel.fullStatus(this.ticketLevel);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    public int getQueueLevel() {
        return this.queueLevel;
    }

    private void setQueueLevel(int p_140087_) {
        this.queueLevel = p_140087_;
    }

    public void setTicketLevel(int pLevel) {
        this.ticketLevel = pLevel;
    }

    private void scheduleFullChunkPromotion(ChunkMap pChunkMap, CompletableFuture<ChunkResult<LevelChunk>> pFuture, Executor pExecutor, FullChunkStatus pFullChunkStatus) {
        this.pendingFullStateConfirmation.cancel(false);
        CompletableFuture<Void> completablefuture = new CompletableFuture<>();
        completablefuture.thenRunAsync(() -> pChunkMap.onFullChunkStatusChange(this.pos, pFullChunkStatus), pExecutor);
        this.pendingFullStateConfirmation = completablefuture;
        pFuture.thenAccept(p_326372_ -> p_326372_.m_320477_(p_200424_ -> completablefuture.complete(null)));
    }

    private void demoteFullChunk(ChunkMap pChunkMap, FullChunkStatus pFullChunkStatus) {
        this.pendingFullStateConfirmation.cancel(false);
        pChunkMap.onFullChunkStatusChange(this.pos, pFullChunkStatus);
    }

    protected void updateFutures(ChunkMap pChunkMap, Executor pExecutor) {
        ChunkStatus chunkstatus = ChunkLevel.generationStatus(this.oldTicketLevel);
        ChunkStatus chunkstatus1 = ChunkLevel.generationStatus(this.ticketLevel);
        boolean flag = ChunkLevel.isLoaded(this.oldTicketLevel);
        boolean flag1 = ChunkLevel.isLoaded(this.ticketLevel);
        FullChunkStatus fullchunkstatus = ChunkLevel.fullStatus(this.oldTicketLevel);
        FullChunkStatus fullchunkstatus1 = ChunkLevel.fullStatus(this.ticketLevel);
        if (flag) {
            ChunkResult<ChunkAccess> chunkresult = ChunkResult.m_324523_(() -> "Unloaded ticket level " + this.pos);

            for (int i = flag1 ? chunkstatus1.m_323297_() + 1 : 0; i <= chunkstatus.m_323297_(); i++) {
                CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.futures.get(i);
                if (completablefuture == null) {
                    this.futures.set(i, CompletableFuture.completedFuture(chunkresult));
                }
            }
        }

        boolean flag5 = fullchunkstatus.isOrAfter(FullChunkStatus.FULL);
        boolean flag6 = fullchunkstatus1.isOrAfter(FullChunkStatus.FULL);
        this.wasAccessibleSinceLastSave |= flag6;
        if (!flag5 && flag6) {
            this.fullChunkFuture = pChunkMap.prepareAccessibleChunk(this);
            this.scheduleFullChunkPromotion(pChunkMap, this.fullChunkFuture, pExecutor, FullChunkStatus.FULL);
            this.updateChunkToSave(this.fullChunkFuture, "full");
        }

        if (flag5 && !flag6) {
            this.fullChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        boolean flag7 = fullchunkstatus.isOrAfter(FullChunkStatus.BLOCK_TICKING);
        boolean flag2 = fullchunkstatus1.isOrAfter(FullChunkStatus.BLOCK_TICKING);
        if (!flag7 && flag2) {
            this.tickingChunkFuture = pChunkMap.prepareTickingChunk(this);
            this.scheduleFullChunkPromotion(pChunkMap, this.tickingChunkFuture, pExecutor, FullChunkStatus.BLOCK_TICKING);
            this.updateChunkToSave(this.tickingChunkFuture, "ticking");
        }

        if (flag7 && !flag2) {
            this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        boolean flag3 = fullchunkstatus.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        boolean flag4 = fullchunkstatus1.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        if (!flag3 && flag4) {
            if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            }

            this.entityTickingChunkFuture = pChunkMap.prepareEntityTickingChunk(this);
            this.scheduleFullChunkPromotion(pChunkMap, this.entityTickingChunkFuture, pExecutor, FullChunkStatus.ENTITY_TICKING);
            this.updateChunkToSave(this.entityTickingChunkFuture, "entity ticking");
        }

        if (flag3 && !flag4) {
            this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        if (!fullchunkstatus1.isOrAfter(fullchunkstatus)) {
            this.demoteFullChunk(pChunkMap, fullchunkstatus1);
        }

        this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
        this.oldTicketLevel = this.ticketLevel;
    }

    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }

    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = ChunkLevel.fullStatus(this.ticketLevel).isOrAfter(FullChunkStatus.FULL);
    }

    public void replaceProtoChunk(ImposterProtoChunk pImposter) {
        for (int i = 0; i < this.futures.length(); i++) {
            CompletableFuture<ChunkResult<ChunkAccess>> completablefuture = this.futures.get(i);
            if (completablefuture != null) {
                ChunkAccess chunkaccess = completablefuture.getNow(UNLOADED_CHUNK).m_318814_(null);
                if (chunkaccess instanceof ProtoChunk) {
                    this.futures.set(i, CompletableFuture.completedFuture(ChunkResult.m_323605_(pImposter)));
                }
            }
        }

        this.updateChunkToSave(CompletableFuture.completedFuture(ChunkResult.m_323605_(pImposter.getWrapped())), "replaceProto");
    }

    public List<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>> getAllFutures() {
        List<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>> list = new ArrayList<>();

        for (int i = 0; i < CHUNK_STATUSES.size(); i++) {
            list.add(Pair.of(CHUNK_STATUSES.get(i), this.futures.get(i)));
        }

        return list;
    }

    static record ChunkSaveDebug(Thread thread, CompletableFuture<?> future, String source) {
    }

    @FunctionalInterface
    public interface LevelChangeListener {
        void onLevelChange(ChunkPos pChunkPos, IntSupplier p_140120_, int p_140121_, IntConsumer p_140122_);
    }

    public interface PlayerProvider {
        List<ServerPlayer> getPlayers(ChunkPos pPos, boolean pBoundaryOnly);
    }
}
