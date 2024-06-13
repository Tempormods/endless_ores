package net.minecraft.util.worldupdate;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RecreatingChunkStorage;
import net.minecraft.world.level.chunk.storage.RecreatingSimpleRegionStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private static final String f_315260_ = "new_";
    static final MutableComponent f_314932_ = Component.translatable("optimizeWorld.stage.upgrading.poi");
    static final MutableComponent f_316185_ = Component.translatable("optimizeWorld.stage.finished.poi");
    static final MutableComponent f_315886_ = Component.translatable("optimizeWorld.stage.upgrading.entities");
    static final MutableComponent f_317146_ = Component.translatable("optimizeWorld.stage.finished.entities");
    static final MutableComponent f_314844_ = Component.translatable("optimizeWorld.stage.upgrading.chunks");
    static final MutableComponent f_316515_ = Component.translatable("optimizeWorld.stage.finished.chunks");
    final Registry<LevelStem> dimensions;
    final Set<ResourceKey<Level>> levels;
    final boolean eraseCache;
    final boolean f_316532_;
    final LevelStorageSource.LevelStorageAccess levelStorage;
    private final Thread thread;
    final DataFixer dataFixer;
    volatile boolean running = true;
    private volatile boolean finished;
    volatile float progress;
    volatile int totalChunks;
    volatile int f_314234_;
    volatile int converted;
    volatile int skipped;
    final Reference2FloatMap<ResourceKey<Level>> progressMap = Reference2FloatMaps.synchronize(new Reference2FloatOpenHashMap<>());
    volatile Component status = Component.translatable("optimizeWorld.stage.counting");
    static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    final DimensionDataStorage overworldDataStorage;

    public WorldUpgrader(LevelStorageSource.LevelStorageAccess pLevelStoarge, DataFixer pDataFixer, RegistryAccess p_334652_, boolean pEraseCache, boolean p_335488_) {
        this.dimensions = p_334652_.registryOrThrow(Registries.LEVEL_STEM);
        this.levels = this.dimensions.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
        this.eraseCache = pEraseCache;
        this.dataFixer = pDataFixer;
        this.levelStorage = pLevelStoarge;
        this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data").toFile(), pDataFixer, p_334652_);
        this.f_316532_ = p_335488_;
        this.thread = THREAD_FACTORY.newThread(this::work);
        this.thread.setUncaughtExceptionHandler((p_18825_, p_18826_) -> {
            LOGGER.error("Error upgrading world", p_18826_);
            this.status = Component.translatable("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.running = false;

        try {
            this.thread.join();
        } catch (InterruptedException interruptedexception) {
        }
    }

    private void work() {
        long i = Util.getMillis();
        LOGGER.info("Upgrading entities");
        new WorldUpgrader.EntityUpgrader().m_319692_();
        LOGGER.info("Upgrading POIs");
        new WorldUpgrader.PoiUpgrader().m_319692_();
        LOGGER.info("Upgrading blocks");
        new WorldUpgrader.ChunkUpgrader().m_319692_();
        this.overworldDataStorage.save();
        i = Util.getMillis() - i;
        LOGGER.info("World optimizaton finished after {} seconds", i / 1000L);
        this.finished = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public float dimensionProgress(ResourceKey<Level> pLevel) {
        return this.progressMap.getFloat(pLevel);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public Component getStatus() {
        return this.status;
    }

    static Path m_324714_(Path p_330107_) {
        return p_330107_.resolveSibling("new_" + p_330107_.getFileName().toString());
    }

    abstract class AbstractUpgrader<T extends AutoCloseable> {
        private final MutableComponent f_316754_;
        private final MutableComponent f_315934_;
        private final String f_317099_;
        private final String f_315401_;
        @Nullable
        protected CompletableFuture<Void> f_314130_;
        protected final DataFixTypes f_317025_;

        AbstractUpgrader(
            final DataFixTypes p_332379_, final String p_334432_, final String p_334138_, final MutableComponent p_332782_, final MutableComponent p_331966_
        ) {
            this.f_317025_ = p_332379_;
            this.f_317099_ = p_334432_;
            this.f_315401_ = p_334138_;
            this.f_316754_ = p_332782_;
            this.f_315934_ = p_331966_;
        }

        public void m_319692_() {
            WorldUpgrader.this.f_314234_ = 0;
            WorldUpgrader.this.totalChunks = 0;
            WorldUpgrader.this.converted = 0;
            WorldUpgrader.this.skipped = 0;
            List<WorldUpgrader.DimensionToUpgrade<T>> list = this.m_321355_();
            if (WorldUpgrader.this.totalChunks != 0) {
                float f = (float)WorldUpgrader.this.f_314234_;
                WorldUpgrader.this.status = this.f_316754_;

                while (WorldUpgrader.this.running) {
                    boolean flag = false;
                    float f1 = 0.0F;

                    for (WorldUpgrader.DimensionToUpgrade<T> dimensiontoupgrade : list) {
                        ResourceKey<Level> resourcekey = dimensiontoupgrade.f_316108_;
                        ListIterator<WorldUpgrader.FileToUpgrade> listiterator = dimensiontoupgrade.f_317046_;
                        T t = dimensiontoupgrade.f_314885_;
                        if (listiterator.hasNext()) {
                            WorldUpgrader.FileToUpgrade worldupgrader$filetoupgrade = listiterator.next();
                            boolean flag1 = true;

                            for (ChunkPos chunkpos : worldupgrader$filetoupgrade.f_315240_) {
                                flag1 = flag1 && this.m_322885_(resourcekey, t, chunkpos);
                                flag = true;
                            }

                            if (WorldUpgrader.this.f_316532_) {
                                if (flag1) {
                                    this.m_323061_(worldupgrader$filetoupgrade.f_314711_);
                                } else {
                                    WorldUpgrader.LOGGER.error("Failed to convert region file {}", worldupgrader$filetoupgrade.f_314711_.m_321600_());
                                }
                            }
                        }

                        float f2 = (float)listiterator.nextIndex() / f;
                        WorldUpgrader.this.progressMap.put(resourcekey, f2);
                        f1 += f2;
                    }

                    WorldUpgrader.this.progress = f1;
                    if (!flag) {
                        break;
                    }
                }

                WorldUpgrader.this.status = this.f_315934_;

                for (WorldUpgrader.DimensionToUpgrade<T> dimensiontoupgrade1 : list) {
                    try {
                        dimensiontoupgrade1.f_314885_.close();
                    } catch (Exception exception) {
                        WorldUpgrader.LOGGER.error("Error upgrading chunk", (Throwable)exception);
                    }
                }
            }
        }

        private List<WorldUpgrader.DimensionToUpgrade<T>> m_321355_() {
            List<WorldUpgrader.DimensionToUpgrade<T>> list = Lists.newArrayList();

            for (ResourceKey<Level> resourcekey : WorldUpgrader.this.levels) {
                RegionStorageInfo regionstorageinfo = new RegionStorageInfo(WorldUpgrader.this.levelStorage.getLevelId(), resourcekey, this.f_317099_);
                Path path = WorldUpgrader.this.levelStorage.getDimensionPath(resourcekey).resolve(this.f_315401_);
                T t = this.m_322985_(regionstorageinfo, path);
                ListIterator<WorldUpgrader.FileToUpgrade> listiterator = this.m_323602_(regionstorageinfo, path);
                list.add(new WorldUpgrader.DimensionToUpgrade<>(resourcekey, t, listiterator));
            }

            return list;
        }

        protected abstract T m_322985_(RegionStorageInfo p_328836_, Path p_332071_);

        private ListIterator<WorldUpgrader.FileToUpgrade> m_323602_(RegionStorageInfo p_332870_, Path p_331013_) {
            List<WorldUpgrader.FileToUpgrade> list = m_323291_(p_332870_, p_331013_);
            WorldUpgrader.this.f_314234_ = WorldUpgrader.this.f_314234_ + list.size();
            WorldUpgrader.this.totalChunks = WorldUpgrader.this.totalChunks + list.stream().mapToInt(p_328536_ -> p_328536_.f_315240_.size()).sum();
            return list.listIterator();
        }

        private static List<WorldUpgrader.FileToUpgrade> m_323291_(RegionStorageInfo p_330333_, Path p_330743_) {
            File[] afile = p_330743_.toFile().listFiles((p_336334_, p_329184_) -> p_329184_.endsWith(".mca"));
            if (afile == null) {
                return List.of();
            } else {
                List<WorldUpgrader.FileToUpgrade> list = Lists.newArrayList();

                for (File file1 : afile) {
                    Matcher matcher = WorldUpgrader.REGEX.matcher(file1.getName());
                    if (matcher.matches()) {
                        int i = Integer.parseInt(matcher.group(1)) << 5;
                        int j = Integer.parseInt(matcher.group(2)) << 5;
                        List<ChunkPos> list1 = Lists.newArrayList();

                        try (RegionFile regionfile = new RegionFile(p_330333_, file1.toPath(), p_330743_, true)) {
                            for (int k = 0; k < 32; k++) {
                                for (int l = 0; l < 32; l++) {
                                    ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                                    if (regionfile.doesChunkExist(chunkpos)) {
                                        list1.add(chunkpos);
                                    }
                                }
                            }

                            if (!list1.isEmpty()) {
                                list.add(new WorldUpgrader.FileToUpgrade(regionfile, list1));
                            }
                        } catch (Throwable throwable) {
                            WorldUpgrader.LOGGER.error("Failed to read chunks from region file {}", file1.toPath(), throwable);
                        }
                    }
                }

                return list;
            }
        }

        private boolean m_322885_(ResourceKey<Level> p_328452_, T p_333889_, ChunkPos p_332028_) {
            boolean flag = false;

            try {
                flag = this.m_318768_(p_333889_, p_332028_, p_328452_);
            } catch (CompletionException | ReportedException reportedexception) {
                Throwable throwable = reportedexception.getCause();
                if (!(throwable instanceof IOException)) {
                    throw reportedexception;
                }

                WorldUpgrader.LOGGER.error("Error upgrading chunk {}", p_332028_, throwable);
            }

            if (flag) {
                WorldUpgrader.this.converted++;
            } else {
                WorldUpgrader.this.skipped++;
            }

            return flag;
        }

        protected abstract boolean m_318768_(T p_329483_, ChunkPos p_327751_, ResourceKey<Level> p_335733_);

        private void m_323061_(RegionFile p_332836_) {
            if (WorldUpgrader.this.f_316532_) {
                if (this.f_314130_ != null) {
                    this.f_314130_.join();
                }

                Path path = p_332836_.m_321600_();
                Path path1 = path.getParent();
                Path path2 = WorldUpgrader.m_324714_(path1).resolve(path.getFileName().toString());

                try {
                    if (path2.toFile().exists()) {
                        Files.delete(path);
                        Files.move(path2, path);
                    } else {
                        WorldUpgrader.LOGGER.error("Failed to replace an old region file. New file {} does not exist.", path2);
                    }
                } catch (IOException ioexception) {
                    WorldUpgrader.LOGGER.error("Failed to replace an old region file", (Throwable)ioexception);
                }
            }
        }
    }

    class ChunkUpgrader extends WorldUpgrader.AbstractUpgrader<ChunkStorage> {
        ChunkUpgrader() {
            super(DataFixTypes.CHUNK, "chunk", "region", WorldUpgrader.f_314844_, WorldUpgrader.f_316515_);
        }

        protected boolean m_318768_(ChunkStorage p_330540_, ChunkPos p_331086_, ResourceKey<Level> p_327850_) {
            CompoundTag compoundtag = p_330540_.read(p_331086_).join().orElse(null);
            if (compoundtag != null) {
                int i = ChunkStorage.getVersion(compoundtag);
                ChunkGenerator chunkgenerator = WorldUpgrader.this.dimensions.getOrThrow(Registries.levelToLevelStem(p_327850_)).generator();
                CompoundTag compoundtag1 = p_330540_.upgradeChunkTag(p_327850_, () -> WorldUpgrader.this.overworldDataStorage, compoundtag, chunkgenerator.getTypeNameForDataFixer());
                ChunkPos chunkpos = new ChunkPos(compoundtag1.getInt("xPos"), compoundtag1.getInt("zPos"));
                if (!chunkpos.equals(p_331086_)) {
                    WorldUpgrader.LOGGER.warn("Chunk {} has invalid position {}", p_331086_, chunkpos);
                }

                boolean flag = i < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                if (WorldUpgrader.this.eraseCache) {
                    flag = flag || compoundtag1.contains("Heightmaps");
                    compoundtag1.remove("Heightmaps");
                    flag = flag || compoundtag1.contains("isLightOn");
                    compoundtag1.remove("isLightOn");
                    ListTag listtag = compoundtag1.getList("sections", 10);

                    for (int j = 0; j < listtag.size(); j++) {
                        CompoundTag compoundtag2 = listtag.getCompound(j);
                        flag = flag || compoundtag2.contains("BlockLight");
                        compoundtag2.remove("BlockLight");
                        flag = flag || compoundtag2.contains("SkyLight");
                        compoundtag2.remove("SkyLight");
                    }
                }

                if (flag || WorldUpgrader.this.f_316532_) {
                    if (this.f_314130_ != null) {
                        this.f_314130_.join();
                    }

                    this.f_314130_ = p_330540_.write(p_331086_, compoundtag1);
                    return true;
                }
            }

            return false;
        }

        protected ChunkStorage m_322985_(RegionStorageInfo p_333791_, Path p_332463_) {
            return (ChunkStorage)(WorldUpgrader.this.f_316532_
                ? new RecreatingChunkStorage(
                    p_333791_.m_324592_("source"),
                    p_332463_,
                    p_333791_.m_324592_("target"),
                    WorldUpgrader.m_324714_(p_332463_),
                    WorldUpgrader.this.dataFixer,
                    true
                )
                : new ChunkStorage(p_333791_, p_332463_, WorldUpgrader.this.dataFixer, true));
        }
    }

    static record DimensionToUpgrade<T>(ResourceKey<Level> f_316108_, T f_314885_, ListIterator<WorldUpgrader.FileToUpgrade> f_317046_) {
    }

    class EntityUpgrader extends WorldUpgrader.SimpleRegionStorageUpgrader {
        EntityUpgrader() {
            super(DataFixTypes.ENTITY_CHUNK, "entities", WorldUpgrader.f_315886_, WorldUpgrader.f_317146_);
        }

        @Override
        protected CompoundTag m_320157_(SimpleRegionStorage p_334286_, CompoundTag p_335346_) {
            return p_334286_.m_323126_(p_335346_, -1);
        }
    }

    static record FileToUpgrade(RegionFile f_314711_, List<ChunkPos> f_315240_) {
    }

    class PoiUpgrader extends WorldUpgrader.SimpleRegionStorageUpgrader {
        PoiUpgrader() {
            super(DataFixTypes.POI_CHUNK, "poi", WorldUpgrader.f_314932_, WorldUpgrader.f_316185_);
        }

        @Override
        protected CompoundTag m_320157_(SimpleRegionStorage p_329642_, CompoundTag p_336180_) {
            return p_329642_.m_323126_(p_336180_, 1945);
        }
    }

    abstract class SimpleRegionStorageUpgrader extends WorldUpgrader.AbstractUpgrader<SimpleRegionStorage> {
        SimpleRegionStorageUpgrader(final DataFixTypes p_332054_, final String p_328150_, final MutableComponent p_336376_, final MutableComponent p_335930_) {
            super(p_332054_, p_328150_, p_328150_, p_336376_, p_335930_);
        }

        protected SimpleRegionStorage m_322985_(RegionStorageInfo p_328549_, Path p_333111_) {
            return (SimpleRegionStorage)(WorldUpgrader.this.f_316532_
                ? new RecreatingSimpleRegionStorage(
                    p_328549_.m_324592_("source"),
                    p_333111_,
                    p_328549_.m_324592_("target"),
                    WorldUpgrader.m_324714_(p_333111_),
                    WorldUpgrader.this.dataFixer,
                    true,
                    this.f_317025_
                )
                : new SimpleRegionStorage(p_328549_, p_333111_, WorldUpgrader.this.dataFixer, true, this.f_317025_));
        }

        protected boolean m_318768_(SimpleRegionStorage p_327888_, ChunkPos p_328250_, ResourceKey<Level> p_329996_) {
            CompoundTag compoundtag = p_327888_.m_321984_(p_328250_).join().orElse(null);
            if (compoundtag != null) {
                int i = ChunkStorage.getVersion(compoundtag);
                CompoundTag compoundtag1 = this.m_320157_(p_327888_, compoundtag);
                boolean flag = i < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                if (flag || WorldUpgrader.this.f_316532_) {
                    if (this.f_314130_ != null) {
                        this.f_314130_.join();
                    }

                    this.f_314130_ = p_327888_.m_321640_(p_328250_, compoundtag1);
                    return true;
                }
            }

            return false;
        }

        protected abstract CompoundTag m_320157_(SimpleRegionStorage p_328302_, CompoundTag p_330493_);
    }
}