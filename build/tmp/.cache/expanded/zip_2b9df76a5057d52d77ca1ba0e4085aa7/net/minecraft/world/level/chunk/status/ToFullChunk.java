package net.minecraft.world.level.chunk.status;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.chunk.ChunkAccess;

@FunctionalInterface
public interface ToFullChunk {
    CompletableFuture<ChunkAccess> m_319739_(ChunkAccess p_333781_);
}