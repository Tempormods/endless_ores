package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.slf4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final int maxCount;
    private int count;
    private long startTime;
    private long nextTickTime = Long.MAX_VALUE;

    private LoggerChunkProgressListener(int pRadius) {
        this.maxCount = pRadius;
    }

    public static LoggerChunkProgressListener m_324668_(int p_331102_) {
        return p_331102_ > 0 ? m_324586_(p_331102_ + 1) : m_321559_();
    }

    public static LoggerChunkProgressListener m_324586_(int p_331415_) {
        int i = ChunkProgressListener.m_320498_(p_331415_);
        return new LoggerChunkProgressListener(i * i);
    }

    public static LoggerChunkProgressListener m_321559_() {
        return new LoggerChunkProgressListener(0);
    }

    @Override
    public void updateSpawnPos(ChunkPos pCenter) {
        this.nextTickTime = Util.getMillis();
        this.startTime = this.nextTickTime;
    }

    @Override
    public void onStatusChange(ChunkPos pChunkPosition, @Nullable ChunkStatus p_331064_) {
        if (p_331064_ == ChunkStatus.f_315432_) {
            this.count++;
        }

        int i = this.getProgress();
        if (Util.getMillis() > this.nextTickTime) {
            this.nextTickTime += 500L;
            LOGGER.info(Component.translatable("menu.preparingSpawn", Mth.clamp(i, 0, 100)).getString());
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
        this.nextTickTime = Long.MAX_VALUE;
    }

    public int getProgress() {
        return this.maxCount == 0 ? 100 : Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
    }
}