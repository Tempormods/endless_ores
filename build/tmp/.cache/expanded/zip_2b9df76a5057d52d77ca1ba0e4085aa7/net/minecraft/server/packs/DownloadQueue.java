package net.minecraft.server.packs;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class DownloadQueue implements AutoCloseable {
    private static final Logger f_303188_ = LogUtils.getLogger();
    private static final int f_303802_ = 20;
    private final Path f_303578_;
    private final JsonEventLog<DownloadQueue.LogEntry> f_303172_;
    private final ProcessorMailbox<Runnable> f_302641_ = ProcessorMailbox.create(Util.m_306705_(), "download-queue");

    public DownloadQueue(Path p_311573_) throws IOException {
        this.f_303578_ = p_311573_;
        FileUtil.createDirectoriesSafe(p_311573_);
        this.f_303172_ = JsonEventLog.open(DownloadQueue.LogEntry.f_302665_, p_311573_.resolve("log.json"));
        DownloadCacheCleaner.m_307501_(p_311573_, 20);
    }

    private DownloadQueue.BatchResult m_306535_(DownloadQueue.BatchConfig p_312964_, Map<UUID, DownloadQueue.DownloadRequest> p_311709_) {
        DownloadQueue.BatchResult downloadqueue$batchresult = new DownloadQueue.BatchResult();
        p_311709_.forEach(
            (p_311290_, p_311466_) -> {
                Path path = this.f_303578_.resolve(p_311290_.toString());
                Path path1 = null;

                try {
                    path1 = HttpUtil.m_305661_(
                        path,
                        p_311466_.f_303187_,
                        p_312964_.f_303230_,
                        p_312964_.f_302484_,
                        p_311466_.f_303050_,
                        p_312964_.f_303205_,
                        p_312964_.f_302686_,
                        p_312964_.f_302265_
                    );
                    downloadqueue$batchresult.f_302807_.put(p_311290_, path1);
                } catch (Exception exception1) {
                    f_303188_.error("Failed to download {}", p_311466_.f_303187_, exception1);
                    downloadqueue$batchresult.f_303809_.add(p_311290_);
                }

                try {
                    this.f_303172_
                        .write(
                            new DownloadQueue.LogEntry(
                                p_311290_,
                                p_311466_.f_303187_.toString(),
                                Instant.now(),
                                Optional.ofNullable(p_311466_.f_303050_).map(HashCode::toString),
                                path1 != null ? this.m_306845_(path1) : Either.left("download_failed")
                            )
                        );
                } catch (Exception exception) {
                    f_303188_.error("Failed to log download of {}", p_311466_.f_303187_, exception);
                }
            }
        );
        return downloadqueue$batchresult;
    }

    private Either<String, DownloadQueue.FileInfoEntry> m_306845_(Path p_310185_) {
        try {
            long i = Files.size(p_310185_);
            Path path = this.f_303578_.relativize(p_310185_);
            return Either.right(new DownloadQueue.FileInfoEntry(path.toString(), i));
        } catch (IOException ioexception) {
            f_303188_.error("Failed to get file size of {}", p_310185_, ioexception);
            return Either.left("no_access");
        }
    }

    public CompletableFuture<DownloadQueue.BatchResult> m_304862_(DownloadQueue.BatchConfig p_312532_, Map<UUID, DownloadQueue.DownloadRequest> p_312658_) {
        return CompletableFuture.supplyAsync(() -> this.m_306535_(p_312532_, p_312658_), this.f_302641_::tell);
    }

    @Override
    public void close() throws IOException {
        this.f_302641_.close();
        this.f_303172_.close();
    }

    public static record BatchConfig(
        HashFunction f_302484_, int f_303205_, Map<String, String> f_303230_, Proxy f_302686_, HttpUtil.DownloadProgressListener f_302265_
    ) {
    }

    public static record BatchResult(Map<UUID, Path> f_302807_, Set<UUID> f_303809_) {
        public BatchResult() {
            this(new HashMap<>(), new HashSet<>());
        }
    }

    public static record DownloadRequest(URL f_303187_, @Nullable HashCode f_303050_) {
    }

    static record FileInfoEntry(String f_302319_, long f_302603_) {
        public static final Codec<DownloadQueue.FileInfoEntry> f_302756_ = RecordCodecBuilder.create(
            p_311514_ -> p_311514_.group(
                        Codec.STRING.fieldOf("name").forGetter(DownloadQueue.FileInfoEntry::f_302319_),
                        Codec.LONG.fieldOf("size").forGetter(DownloadQueue.FileInfoEntry::f_302603_)
                    )
                    .apply(p_311514_, DownloadQueue.FileInfoEntry::new)
        );
    }

    static record LogEntry(
        UUID f_303431_, String f_302712_, Instant f_303846_, Optional<String> f_302640_, Either<String, DownloadQueue.FileInfoEntry> f_302525_
    ) {
        public static final Codec<DownloadQueue.LogEntry> f_302665_ = RecordCodecBuilder.create(
            p_310865_ -> p_310865_.group(
                        UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(DownloadQueue.LogEntry::f_303431_),
                        Codec.STRING.fieldOf("url").forGetter(DownloadQueue.LogEntry::f_302712_),
                        ExtraCodecs.INSTANT_ISO8601.fieldOf("time").forGetter(DownloadQueue.LogEntry::f_303846_),
                        Codec.STRING.optionalFieldOf("hash").forGetter(DownloadQueue.LogEntry::f_302640_),
                        Codec.mapEither(Codec.STRING.fieldOf("error"), DownloadQueue.FileInfoEntry.f_302756_.fieldOf("file"))
                            .forGetter(DownloadQueue.LogEntry::f_302525_)
                    )
                    .apply(p_310865_, DownloadQueue.LogEntry::new)
        );
    }
}