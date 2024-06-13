package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.io.FileUtils;

public class RecreatingSimpleRegionStorage extends SimpleRegionStorage {
    private final IOWorker f_314431_;
    private final Path f_315300_;

    public RecreatingSimpleRegionStorage(
        RegionStorageInfo p_330416_,
        Path p_334038_,
        RegionStorageInfo p_332972_,
        Path p_334447_,
        DataFixer p_330614_,
        boolean p_331908_,
        DataFixTypes p_333003_
    ) {
        super(p_330416_, p_334038_, p_330614_, p_331908_, p_333003_);
        this.f_315300_ = p_334447_;
        this.f_314431_ = new IOWorker(p_332972_, p_334447_, p_331908_);
    }

    @Override
    public CompletableFuture<Void> m_321640_(ChunkPos p_333713_, @Nullable CompoundTag p_332709_) {
        return this.f_314431_.store(p_333713_, p_332709_);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.f_314431_.close();
        if (this.f_315300_.toFile().exists()) {
            FileUtils.deleteDirectory(this.f_315300_.toFile());
        }
    }
}