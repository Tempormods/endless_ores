package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public interface ChunkProgressListener {
    void updateSpawnPos(ChunkPos pCenter);

    void onStatusChange(ChunkPos pChunkPosition, @Nullable ChunkStatus p_328329_);

    void start();

    void stop();

    static int m_320498_(int p_329991_) {
        return 2 * p_329991_ + 1;
    }
}