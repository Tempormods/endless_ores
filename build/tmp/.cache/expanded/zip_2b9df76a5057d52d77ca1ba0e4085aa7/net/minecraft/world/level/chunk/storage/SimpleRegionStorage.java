package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;

public class SimpleRegionStorage implements AutoCloseable {
    private final IOWorker f_316936_;
    private final DataFixer f_316431_;
    private final DataFixTypes f_315557_;

    public SimpleRegionStorage(RegionStorageInfo p_327836_, Path p_328804_, DataFixer p_332309_, boolean p_335456_, DataFixTypes p_331426_) {
        this.f_316431_ = p_332309_;
        this.f_315557_ = p_331426_;
        this.f_316936_ = new IOWorker(p_327836_, p_328804_, p_335456_);
    }

    public CompletableFuture<Optional<CompoundTag>> m_321984_(ChunkPos p_328805_) {
        return this.f_316936_.loadAsync(p_328805_);
    }

    public CompletableFuture<Void> m_321640_(ChunkPos p_328507_, @Nullable CompoundTag p_328699_) {
        return this.f_316936_.store(p_328507_, p_328699_);
    }

    public CompoundTag m_323126_(CompoundTag p_330988_, int p_328203_) {
        int i = NbtUtils.getDataVersion(p_330988_, p_328203_);
        return this.f_315557_.updateToCurrentVersion(this.f_316431_, p_330988_, i);
    }

    public Dynamic<Tag> m_323523_(Dynamic<Tag> p_329521_, int p_334930_) {
        return this.f_315557_.updateToCurrentVersion(this.f_316431_, p_329521_, p_334930_);
    }

    public CompletableFuture<Void> m_322284_(boolean p_334675_) {
        return this.f_316936_.synchronize(p_334675_);
    }

    @Override
    public void close() throws IOException {
        this.f_316936_.close();
    }
}