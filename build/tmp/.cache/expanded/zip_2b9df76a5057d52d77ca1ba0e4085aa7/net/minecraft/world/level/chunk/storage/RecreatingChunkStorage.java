package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.io.FileUtils;

public class RecreatingChunkStorage extends ChunkStorage {
    private final IOWorker f_315897_;
    private final Path f_314769_;

    public RecreatingChunkStorage(
        RegionStorageInfo p_335619_, Path p_328786_, RegionStorageInfo p_335564_, Path p_329291_, DataFixer p_334361_, boolean p_329095_
    ) {
        super(p_335619_, p_328786_, p_334361_, p_329095_);
        this.f_314769_ = p_329291_;
        this.f_315897_ = new IOWorker(p_335564_, p_329291_, p_329095_);
    }

    @Override
    public CompletableFuture<Void> write(ChunkPos p_330240_, CompoundTag p_327782_) {
        this.m_320556_(p_330240_);
        return this.f_315897_.store(p_330240_, p_327782_);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.f_315897_.close();
        if (this.f_314769_.toFile().exists()) {
            FileUtils.deleteDirectory(this.f_314769_.toFile());
        }
    }
}