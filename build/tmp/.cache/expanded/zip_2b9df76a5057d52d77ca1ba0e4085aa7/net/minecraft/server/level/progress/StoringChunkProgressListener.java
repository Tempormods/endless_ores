package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class StoringChunkProgressListener implements ChunkProgressListener {
    private final LoggerChunkProgressListener delegate;
    private final Long2ObjectOpenHashMap<ChunkStatus> statuses = new Long2ObjectOpenHashMap<>();
    private ChunkPos spawnPos = new ChunkPos(0, 0);
    private final int fullDiameter;
    private final int radius;
    private final int diameter;
    private boolean started;

    private StoringChunkProgressListener(LoggerChunkProgressListener p_333746_, int pRadius, int p_328006_, int p_335828_) {
        this.delegate = p_333746_;
        this.fullDiameter = pRadius;
        this.radius = p_328006_;
        this.diameter = p_335828_;
    }

    public static StoringChunkProgressListener m_319107_(int p_329839_) {
        return p_329839_ > 0 ? m_324160_(p_329839_ + 1) : m_319204_();
    }

    public static StoringChunkProgressListener m_324160_(int p_335925_) {
        LoggerChunkProgressListener loggerchunkprogresslistener = LoggerChunkProgressListener.m_324586_(p_335925_);
        int i = ChunkProgressListener.m_320498_(p_335925_);
        int j = p_335925_ + ChunkStatus.m_324169_();
        int k = ChunkProgressListener.m_320498_(j);
        return new StoringChunkProgressListener(loggerchunkprogresslistener, i, j, k);
    }

    public static StoringChunkProgressListener m_319204_() {
        return new StoringChunkProgressListener(LoggerChunkProgressListener.m_321559_(), 0, 0, 0);
    }

    @Override
    public void updateSpawnPos(ChunkPos pCenter) {
        if (this.started) {
            this.delegate.updateSpawnPos(pCenter);
            this.spawnPos = pCenter;
        }
    }

    @Override
    public void onStatusChange(ChunkPos pChunkPosition, @Nullable ChunkStatus p_334580_) {
        if (this.started) {
            this.delegate.onStatusChange(pChunkPosition, p_334580_);
            if (p_334580_ == null) {
                this.statuses.remove(pChunkPosition.toLong());
            } else {
                this.statuses.put(pChunkPosition.toLong(), p_334580_);
            }
        }
    }

    @Override
    public void start() {
        this.started = true;
        this.statuses.clear();
        this.delegate.start();
    }

    @Override
    public void stop() {
        this.started = false;
        this.delegate.stop();
    }

    public int getFullDiameter() {
        return this.fullDiameter;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getProgress() {
        return this.delegate.getProgress();
    }

    @Nullable
    public ChunkStatus getStatus(int pX, int pZ) {
        return this.statuses.get(ChunkPos.asLong(pX + this.spawnPos.x - this.radius, pZ + this.spawnPos.z - this.radius));
    }
}