package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import org.slf4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ENTITIES_TAG = "Entities";
    private static final String POSITION_TAG = "Position";
    private final ServerLevel level;
    private final SimpleRegionStorage f_314245_;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final ProcessorMailbox<Runnable> entityDeserializerQueue;

    public EntityStorage(SimpleRegionStorage p_329511_, ServerLevel pLevel, Executor pMainThreadExecutor) {
        this.f_314245_ = p_329511_;
        this.level = pLevel;
        this.entityDeserializerQueue = ProcessorMailbox.create(pMainThreadExecutor, "entity-deserializer");
    }

    @Override
    public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos pPos) {
        return this.emptyChunks.contains(pPos.toLong())
            ? CompletableFuture.completedFuture(emptyChunk(pPos))
            : this.f_314245_.m_321984_(pPos).thenApplyAsync(p_327420_ -> {
                if (p_327420_.isEmpty()) {
                    this.emptyChunks.add(pPos.toLong());
                    return emptyChunk(pPos);
                } else {
                    try {
                        ChunkPos chunkpos = readChunkPos(p_327420_.get());
                        if (!Objects.equals(pPos, chunkpos)) {
                            LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", pPos, pPos, chunkpos);
                        }
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to parse chunk {} position info", pPos, exception);
                    }

                    CompoundTag compoundtag = this.f_314245_.m_323126_(p_327420_.get(), -1);
                    ListTag listtag = compoundtag.getList("Entities", 10);
                    List<Entity> list = EntityType.loadEntitiesRecursive(listtag, this.level).collect(ImmutableList.toImmutableList());
                    return new ChunkEntities<>(pPos, list);
                }
            }, this.entityDeserializerQueue::tell);
    }

    private static ChunkPos readChunkPos(CompoundTag pTag) {
        int[] aint = pTag.getIntArray("Position");
        return new ChunkPos(aint[0], aint[1]);
    }

    private static void writeChunkPos(CompoundTag pTag, ChunkPos pPos) {
        pTag.put("Position", new IntArrayTag(new int[]{pPos.x, pPos.z}));
    }

    private static ChunkEntities<Entity> emptyChunk(ChunkPos pPos) {
        return new ChunkEntities<>(pPos, ImmutableList.of());
    }

    @Override
    public void storeEntities(ChunkEntities<Entity> pEntities) {
        ChunkPos chunkpos = pEntities.getPos();
        if (pEntities.isEmpty()) {
            if (this.emptyChunks.add(chunkpos.toLong())) {
                this.f_314245_.m_321640_(chunkpos, null);
            }
        } else {
            ListTag listtag = new ListTag();
            pEntities.getEntities().forEach(p_156567_ -> {
                CompoundTag compoundtag1 = new CompoundTag();
                try {
                if (p_156567_.save(compoundtag1)) {
                    listtag.add(compoundtag1);
                }
                } catch (Exception e) {
                   LOGGER.error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", p_156567_.getType(), e);
                }
            });
            CompoundTag compoundtag = NbtUtils.addCurrentDataVersion(new CompoundTag());
            compoundtag.put("Entities", listtag);
            writeChunkPos(compoundtag, chunkpos);
            this.f_314245_.m_321640_(chunkpos, compoundtag).exceptionally(p_156554_ -> {
                LOGGER.error("Failed to store chunk {}", chunkpos, p_156554_);
                return null;
            });
            this.emptyChunks.remove(chunkpos.toLong());
        }
    }

    @Override
    public void flush(boolean pSynchronize) {
        this.f_314245_.m_322284_(pSynchronize).join();
        this.entityDeserializerQueue.runAll();
    }

    @Override
    public void close() throws IOException {
        this.f_314245_.close();
    }
}
