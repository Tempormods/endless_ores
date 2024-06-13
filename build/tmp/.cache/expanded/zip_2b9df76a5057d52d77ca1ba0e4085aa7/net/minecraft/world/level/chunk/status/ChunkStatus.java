package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChunkStatus {
    public static final int f_316469_ = 8;
    private static final EnumSet<Heightmap.Types> f_314980_ = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
    public static final EnumSet<Heightmap.Types> f_316038_ = EnumSet.of(
        Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
    );
    public static final ChunkStatus f_314297_ = m_324712_(
        "empty", null, -1, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_321603_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_317114_ = m_324712_(
        "structure_starts", f_314297_, 0, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_319607_, ChunkStatusTasks::m_322589_
    );
    public static final ChunkStatus f_316571_ = m_324712_(
        "structure_references", f_317114_, 8, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_319021_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_316460_ = m_324712_(
        "biomes", f_316571_, 8, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_323653_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_316398_ = m_324712_(
        "noise", f_316460_, 8, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_324468_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_316036_ = m_324712_(
        "surface", f_316398_, 8, false, f_314980_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_324558_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_314171_ = m_324712_(
        "carvers", f_316036_, 8, false, f_316038_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_321114_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_314060_ = m_324712_(
        "features", f_314171_, 8, false, f_316038_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_324159_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_315473_ = m_324712_(
        "initialize_light", f_314060_, 0, false, f_316038_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_322335_, ChunkStatusTasks::m_320949_
    );
    public static final ChunkStatus f_316967_ = m_324712_(
        "light", f_315473_, 1, true, f_316038_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_319997_, ChunkStatusTasks::m_321108_
    );
    public static final ChunkStatus f_316387_ = m_324712_(
        "spawn", f_316967_, 1, false, f_316038_, ChunkType.PROTOCHUNK, ChunkStatusTasks::m_324388_, ChunkStatusTasks::m_318681_
    );
    public static final ChunkStatus f_315432_ = m_324712_(
        "full", f_316387_, 0, false, f_316038_, ChunkType.LEVELCHUNK, ChunkStatusTasks::m_322482_, ChunkStatusTasks::m_323743_
    );
    private static final List<ChunkStatus> f_317018_ = ImmutableList.of(
        f_315432_, f_315473_, f_314171_, f_316460_, f_317114_, f_317114_, f_317114_, f_317114_, f_317114_, f_317114_, f_317114_, f_317114_
    );
    private static final IntList f_315758_ = Util.make(new IntArrayList(m_323667_().size()), p_335012_ -> {
        int i = 0;

        for (int j = m_323667_().size() - 1; j >= 0; j--) {
            while (i + 1 < f_317018_.size() && j <= f_317018_.get(i + 1).m_323297_()) {
                i++;
            }

            p_335012_.add(0, i);
        }
    });
    private final int f_317089_;
    private final ChunkStatus f_316061_;
    private final ChunkStatus.GenerationTask f_314693_;
    private final ChunkStatus.LoadingTask f_315856_;
    private final int f_315063_;
    private final boolean f_316775_;
    private final ChunkType f_316704_;
    private final EnumSet<Heightmap.Types> f_317064_;

    private static ChunkStatus m_324712_(
        String p_334704_,
        @Nullable ChunkStatus p_335238_,
        int p_331152_,
        boolean p_332303_,
        EnumSet<Heightmap.Types> p_335194_,
        ChunkType p_333808_,
        ChunkStatus.GenerationTask p_328792_,
        ChunkStatus.LoadingTask p_335536_
    ) {
        return Registry.register(
            BuiltInRegistries.CHUNK_STATUS, p_334704_, new ChunkStatus(p_335238_, p_331152_, p_332303_, p_335194_, p_333808_, p_328792_, p_335536_)
        );
    }

    public static List<ChunkStatus> m_323667_() {
        List<ChunkStatus> list = Lists.newArrayList();

        ChunkStatus chunkstatus;
        for (chunkstatus = f_315432_; chunkstatus.m_322072_() != chunkstatus; chunkstatus = chunkstatus.m_322072_()) {
            list.add(chunkstatus);
        }

        list.add(chunkstatus);
        Collections.reverse(list);
        return list;
    }

    public static ChunkStatus m_323212_(int p_334095_) {
        if (p_334095_ >= f_317018_.size()) {
            return f_314297_;
        } else {
            return p_334095_ < 0 ? f_315432_ : f_317018_.get(p_334095_);
        }
    }

    public static int m_324169_() {
        return f_317018_.size();
    }

    public static int m_319816_(ChunkStatus p_331292_) {
        return f_315758_.getInt(p_331292_.m_323297_());
    }

    public ChunkStatus(
        @Nullable ChunkStatus p_334696_,
        int p_328357_,
        boolean p_329678_,
        EnumSet<Heightmap.Types> p_329876_,
        ChunkType p_336141_,
        ChunkStatus.GenerationTask p_328064_,
        ChunkStatus.LoadingTask p_333773_
    ) {
        this.f_316061_ = p_334696_ == null ? this : p_334696_;
        this.f_314693_ = p_328064_;
        this.f_315856_ = p_333773_;
        this.f_315063_ = p_328357_;
        this.f_316775_ = p_329678_;
        this.f_316704_ = p_336141_;
        this.f_317064_ = p_329876_;
        this.f_317089_ = p_334696_ == null ? 0 : p_334696_.m_323297_() + 1;
    }

    public int m_323297_() {
        return this.f_317089_;
    }

    public ChunkStatus m_322072_() {
        return this.f_316061_;
    }

    public CompletableFuture<ChunkAccess> m_319901_(WorldGenContext p_333542_, Executor p_332959_, ToFullChunk p_332442_, List<ChunkAccess> p_328194_) {
        ChunkAccess chunkaccess = p_328194_.get(p_328194_.size() / 2);
        ProfiledDuration profiledduration = JvmProfiler.INSTANCE.onChunkGenerate(chunkaccess.getPos(), p_333542_.f_314224_().dimension(), this.toString());
        return this.f_314693_.m_321853_(p_333542_, this, p_332959_, p_332442_, p_328194_, chunkaccess).thenApply(p_330327_ -> {
            if (p_330327_ instanceof ProtoChunk protochunk && !protochunk.getStatus().m_319325_(this)) {
                protochunk.setStatus(this);
            }

            if (profiledduration != null) {
                profiledduration.finish();
            }

            return (ChunkAccess)p_330327_;
        });
    }

    public CompletableFuture<ChunkAccess> m_320857_(WorldGenContext p_336003_, ToFullChunk p_329647_, ChunkAccess p_335394_) {
        return this.f_315856_.m_324213_(p_336003_, this, p_329647_, p_335394_);
    }

    public int m_324557_() {
        return this.f_315063_;
    }

    public boolean m_323882_() {
        return this.f_316775_;
    }

    public ChunkType m_321717_() {
        return this.f_316704_;
    }

    public static ChunkStatus m_322436_(String p_329723_) {
        return BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(p_329723_));
    }

    public EnumSet<Heightmap.Types> m_324137_() {
        return this.f_317064_;
    }

    public boolean m_319325_(ChunkStatus p_334516_) {
        return this.m_323297_() >= p_334516_.m_323297_();
    }

    @Override
    public String toString() {
        return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
    }

    @FunctionalInterface
    protected interface GenerationTask {
        CompletableFuture<ChunkAccess> m_321853_(
            WorldGenContext p_335125_, ChunkStatus p_330585_, Executor p_330238_, ToFullChunk p_330981_, List<ChunkAccess> p_331880_, ChunkAccess p_336215_
        );
    }

    @FunctionalInterface
    protected interface LoadingTask {
        CompletableFuture<ChunkAccess> m_324213_(WorldGenContext p_330561_, ChunkStatus p_332376_, ToFullChunk p_330749_, ChunkAccess p_332821_);
    }
}