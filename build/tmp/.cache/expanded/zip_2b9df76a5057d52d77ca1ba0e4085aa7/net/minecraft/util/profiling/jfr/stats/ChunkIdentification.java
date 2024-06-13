package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkIdentification(String f_314010_, String f_313971_, int f_316438_, int f_316635_) {
    public static ChunkIdentification m_321919_(RecordedEvent p_327718_) {
        return new ChunkIdentification(
            p_327718_.getString("level"), p_327718_.getString("dimension"), p_327718_.getInt("chunkPosX"), p_327718_.getInt("chunkPosZ")
        );
    }
}