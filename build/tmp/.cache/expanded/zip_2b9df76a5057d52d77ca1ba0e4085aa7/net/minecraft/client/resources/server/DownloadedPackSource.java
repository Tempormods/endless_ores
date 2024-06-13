package net.minecraft.client.resources.server;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.util.UndashedUuid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.main.GameConfig;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.packs.DownloadQueue;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadedPackSource implements AutoCloseable {
    private static final Component f_303751_ = Component.translatable("resourcePack.server.name");
    private static final Pattern f_303252_ = Pattern.compile("^[a-fA-F0-9]{40}$");
    static final Logger f_302532_ = LogUtils.getLogger();
    private static final RepositorySource f_302583_ = p_313076_ -> {
    };
    private static final PackSelectionConfig f_314007_ = new PackSelectionConfig(true, Pack.Position.TOP, true);
    private static final PackLoadFeedback f_303352_ = new PackLoadFeedback() {
        @Override
        public void m_304984_(UUID p_310776_, PackLoadFeedback.Update p_309862_) {
            DownloadedPackSource.f_302532_.debug("Downloaded pack {} changed state to {}", p_310776_, p_309862_);
        }

        @Override
        public void m_305621_(UUID p_310730_, PackLoadFeedback.FinalResult p_311165_) {
            DownloadedPackSource.f_302532_.debug("Downloaded pack {} finished with state {}", p_310730_, p_311165_);
        }
    };
    final Minecraft f_302229_;
    private RepositorySource f_302893_ = f_302583_;
    @Nullable
    private PackReloadConfig.Callbacks f_302526_;
    final ServerPackManager f_302848_;
    private final DownloadQueue f_303020_;
    private PackSource f_303185_ = PackSource.SERVER;
    PackLoadFeedback f_303843_ = f_303352_;
    private int f_303014_;

    public DownloadedPackSource(Minecraft p_310367_, Path p_311926_, GameConfig.UserData p_313017_) {
        this.f_302229_ = p_310367_;

        try {
            this.f_303020_ = new DownloadQueue(p_311926_);
        } catch (IOException ioexception) {
            throw new UncheckedIOException("Failed to open download queue in directory " + p_311926_, ioexception);
        }

        Executor executor = p_310367_::tell;
        this.f_302848_ = new ServerPackManager(this.m_304744_(this.f_303020_, executor, p_313017_.user, p_313017_.proxy), new PackLoadFeedback() {
            @Override
            public void m_304984_(UUID p_311063_, PackLoadFeedback.Update p_310840_) {
                DownloadedPackSource.this.f_303843_.m_304984_(p_311063_, p_310840_);
            }

            @Override
            public void m_305621_(UUID p_311502_, PackLoadFeedback.FinalResult p_310552_) {
                DownloadedPackSource.this.f_303843_.m_305621_(p_311502_, p_310552_);
            }
        }, this.m_307112_(), this.m_305401_(executor), ServerPackManager.PackPromptStatus.PENDING);
    }

    HttpUtil.DownloadProgressListener m_305402_(final int p_313003_) {
        return new HttpUtil.DownloadProgressListener() {
            private final SystemToast.SystemToastId f_303200_ = new SystemToast.SystemToastId();
            private Component f_302398_ = Component.empty();
            @Nullable
            private Component f_302645_ = null;
            private int f_303786_;
            private int f_302215_;
            private OptionalLong f_302237_ = OptionalLong.empty();

            private void m_306973_() {
                SystemToast.addOrUpdate(DownloadedPackSource.this.f_302229_.getToasts(), this.f_303200_, this.f_302398_, this.f_302645_);
            }

            private void m_307167_(long p_310910_) {
                if (this.f_302237_.isPresent()) {
                    this.f_302645_ = Component.translatable("download.pack.progress.percent", p_310910_ * 100L / this.f_302237_.getAsLong());
                } else {
                    this.f_302645_ = Component.translatable("download.pack.progress.bytes", Unit.humanReadable(p_310910_));
                }

                this.m_306973_();
            }

            @Override
            public void m_305410_() {
                this.f_303786_++;
                this.f_302398_ = Component.translatable("download.pack.title", this.f_303786_, p_313003_);
                this.m_306973_();
                DownloadedPackSource.f_302532_.debug("Starting pack {}/{} download", this.f_303786_, p_313003_);
            }

            @Override
            public void m_306050_(OptionalLong p_309831_) {
                DownloadedPackSource.f_302532_.debug("File size = {} bytes", p_309831_);
                this.f_302237_ = p_309831_;
                this.m_307167_(0L);
            }

            @Override
            public void m_305341_(long p_313004_) {
                DownloadedPackSource.f_302532_.debug("Progress for pack {}: {} bytes", this.f_303786_, p_313004_);
                this.m_307167_(p_313004_);
            }

            @Override
            public void m_304846_(boolean p_311561_) {
                if (!p_311561_) {
                    DownloadedPackSource.f_302532_.info("Pack {} failed to download", this.f_303786_);
                    this.f_302215_++;
                } else {
                    DownloadedPackSource.f_302532_.debug("Download ended for pack {}", this.f_303786_);
                }

                if (this.f_303786_ == p_313003_) {
                    if (this.f_302215_ > 0) {
                        this.f_302398_ = Component.translatable("download.pack.failed", this.f_302215_, p_313003_);
                        this.f_302645_ = null;
                        this.m_306973_();
                    } else {
                        SystemToast.m_305701_(DownloadedPackSource.this.f_302229_.getToasts(), this.f_303200_);
                    }
                }
            }
        };
    }

    private PackDownloader m_304744_(final DownloadQueue p_310017_, final Executor p_312902_, final User p_312845_, final Proxy p_312022_) {
        return new PackDownloader() {
            private static final int f_302329_ = 262144000;
            private static final HashFunction f_303022_ = Hashing.sha1();

            private Map<String, String> m_307287_() {
                WorldVersion worldversion = SharedConstants.getCurrentVersion();
                return Map.of(
                    "X-Minecraft-Username",
                    p_312845_.getName(),
                    "X-Minecraft-UUID",
                    UndashedUuid.toString(p_312845_.getProfileId()),
                    "X-Minecraft-Version",
                    worldversion.getName(),
                    "X-Minecraft-Version-ID",
                    worldversion.getId(),
                    "X-Minecraft-Pack-Format",
                    String.valueOf(worldversion.getPackVersion(PackType.CLIENT_RESOURCES)),
                    "User-Agent",
                    "Minecraft Java/" + worldversion.getName()
                );
            }

            @Override
            public void m_305313_(Map<UUID, DownloadQueue.DownloadRequest> p_310177_, Consumer<DownloadQueue.BatchResult> p_310806_) {
                p_310017_.m_304862_(
                        new DownloadQueue.BatchConfig(f_303022_, 262144000, this.m_307287_(), p_312022_, DownloadedPackSource.this.m_305402_(p_310177_.size())),
                        p_310177_
                    )
                    .thenAcceptAsync(p_310806_, p_312902_);
            }
        };
    }

    private Runnable m_305401_(final Executor p_312638_) {
        return new Runnable() {
            private boolean f_302430_;
            private boolean f_303443_;

            @Override
            public void run() {
                this.f_303443_ = true;
                if (!this.f_302430_) {
                    this.f_302430_ = true;
                    p_312638_.execute(this::m_305126_);
                }
            }

            private void m_305126_() {
                while (this.f_303443_) {
                    this.f_303443_ = false;
                    DownloadedPackSource.this.f_302848_.m_307766_();
                }

                this.f_302430_ = false;
            }
        };
    }

    private PackReloadConfig m_307112_() {
        return this::m_304661_;
    }

    @Nullable
    private List<Pack> m_305771_(List<PackReloadConfig.IdAndPath> p_313161_) {
        List<Pack> list = new ArrayList<>(p_313161_.size());

        for (PackReloadConfig.IdAndPath packreloadconfig$idandpath : Lists.reverse(p_313161_)) {
            String s = String.format(Locale.ROOT, "server/%08X/%s", this.f_303014_++, packreloadconfig$idandpath.f_302551_());
            Path path = packreloadconfig$idandpath.f_303359_();
            PackLocationInfo packlocationinfo = new PackLocationInfo(s, f_303751_, this.f_303185_, Optional.empty());
            Pack.ResourcesSupplier pack$resourcessupplier = new FilePackResources.FileResourcesSupplier(path);
            int i = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
            Pack.Metadata pack$metadata = Pack.m_324832_(packlocationinfo, pack$resourcessupplier, i);
            if (pack$metadata == null) {
                f_302532_.warn("Invalid pack metadata in {}, ignoring all", path);
                return null;
            }

            list.add(new Pack(packlocationinfo, pack$resourcessupplier, pack$metadata, f_314007_));
        }

        return list;
    }

    public RepositorySource m_307233_() {
        return p_311800_ -> this.f_302893_.loadPacks(p_311800_);
    }

    private static RepositorySource m_305020_(List<Pack> p_310649_) {
        return p_310649_.isEmpty() ? f_302583_ : p_310649_::forEach;
    }

    private void m_304661_(PackReloadConfig.Callbacks p_310818_) {
        this.f_302526_ = p_310818_;
        List<PackReloadConfig.IdAndPath> list = p_310818_.m_305324_();
        List<Pack> list1 = this.m_305771_(list);
        if (list1 == null) {
            p_310818_.m_304685_(false);
            List<PackReloadConfig.IdAndPath> list2 = p_310818_.m_305324_();
            list1 = this.m_305771_(list2);
            if (list1 == null) {
                f_302532_.warn("Double failure in loading server packs");
                list1 = List.of();
            }
        }

        this.f_302893_ = m_305020_(list1);
        this.f_302229_.reloadResourcePacks();
    }

    public void m_306800_() {
        if (this.f_302526_ != null) {
            this.f_302526_.m_304685_(false);
            List<Pack> list = this.m_305771_(this.f_302526_.m_305324_());
            if (list == null) {
                f_302532_.warn("Double failure in loading server packs");
                list = List.of();
            }

            this.f_302893_ = m_305020_(list);
        }
    }

    public void m_307354_() {
        if (this.f_302526_ != null) {
            this.f_302526_.m_304685_(true);
            this.f_302526_ = null;
            this.f_302893_ = f_302583_;
        }
    }

    public void m_306059_() {
        if (this.f_302526_ != null) {
            this.f_302526_.m_304954_();
            this.f_302526_ = null;
        }
    }

    @Nullable
    private static HashCode m_305140_(@Nullable String p_312783_) {
        return p_312783_ != null && f_303252_.matcher(p_312783_).matches() ? HashCode.fromString(p_312783_.toLowerCase(Locale.ROOT)) : null;
    }

    public void m_304637_(UUID p_312781_, URL p_312716_, @Nullable String p_312757_) {
        HashCode hashcode = m_305140_(p_312757_);
        this.f_302848_.m_307356_(p_312781_, p_312716_, hashcode);
    }

    public void m_306353_(UUID p_310453_, Path p_312255_) {
        this.f_302848_.m_306969_(p_310453_, p_312255_);
    }

    public void m_306043_(UUID p_312698_) {
        this.f_302848_.m_307735_(p_312698_);
    }

    public void m_304654_() {
        this.f_302848_.m_304731_();
    }

    private static PackLoadFeedback m_307197_(final Connection p_312565_) {
        return new PackLoadFeedback() {
            @Override
            public void m_304984_(UUID p_310120_, PackLoadFeedback.Update p_313074_) {
                DownloadedPackSource.f_302532_.debug("Pack {} changed status to {}", p_310120_, p_313074_);

                ServerboundResourcePackPacket.Action serverboundresourcepackpacket$action = switch (p_313074_) {
                    case ACCEPTED -> ServerboundResourcePackPacket.Action.ACCEPTED;
                    case DOWNLOADED -> ServerboundResourcePackPacket.Action.DOWNLOADED;
                };
                p_312565_.send(new ServerboundResourcePackPacket(p_310120_, serverboundresourcepackpacket$action));
            }

            @Override
            public void m_305621_(UUID p_310323_, PackLoadFeedback.FinalResult p_312396_) {
                DownloadedPackSource.f_302532_.debug("Pack {} changed status to {}", p_310323_, p_312396_);

                ServerboundResourcePackPacket.Action serverboundresourcepackpacket$action = switch (p_312396_) {
                    case APPLIED -> ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;
                    case DOWNLOAD_FAILED -> ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD;
                    case DECLINED -> ServerboundResourcePackPacket.Action.DECLINED;
                    case DISCARDED -> ServerboundResourcePackPacket.Action.DISCARDED;
                    case ACTIVATION_FAILED -> ServerboundResourcePackPacket.Action.FAILED_RELOAD;
                };
                p_312565_.send(new ServerboundResourcePackPacket(p_310323_, serverboundresourcepackpacket$action));
            }
        };
    }

    public void m_305368_(Connection p_310083_, ServerPackManager.PackPromptStatus p_309566_) {
        this.f_303185_ = PackSource.SERVER;
        this.f_303843_ = m_307197_(p_310083_);
        switch (p_309566_) {
            case ALLOWED:
                this.f_302848_.m_306539_();
                break;
            case DECLINED:
                this.f_302848_.m_305870_();
                break;
            case PENDING:
                this.f_302848_.m_305384_();
        }
    }

    public void m_306738_() {
        this.f_303185_ = PackSource.WORLD;
        this.f_303843_ = f_303352_;
        this.f_302848_.m_306539_();
    }

    public void m_307345_() {
        this.f_302848_.m_306539_();
    }

    public void m_306322_() {
        this.f_302848_.m_305870_();
    }

    public CompletableFuture<Void> m_305490_(final UUID p_309645_) {
        final CompletableFuture<Void> completablefuture = new CompletableFuture<>();
        final PackLoadFeedback packloadfeedback = this.f_303843_;
        this.f_303843_ = new PackLoadFeedback() {
            @Override
            public void m_304984_(UUID p_312518_, PackLoadFeedback.Update p_310008_) {
                packloadfeedback.m_304984_(p_312518_, p_310008_);
            }

            @Override
            public void m_305621_(UUID p_310518_, PackLoadFeedback.FinalResult p_310501_) {
                if (p_309645_.equals(p_310518_)) {
                    DownloadedPackSource.this.f_303843_ = packloadfeedback;
                    if (p_310501_ == PackLoadFeedback.FinalResult.APPLIED) {
                        completablefuture.complete(null);
                    } else {
                        completablefuture.completeExceptionally(new IllegalStateException("Failed to apply pack " + p_310518_ + ", reason: " + p_310501_));
                    }
                }

                packloadfeedback.m_305621_(p_310518_, p_310501_);
            }
        };
        return completablefuture;
    }

    public void m_306775_() {
        this.f_302848_.m_304731_();
        this.f_303843_ = f_303352_;
        this.f_302848_.m_305384_();
    }

    @Override
    public void close() throws IOException {
        this.f_303020_.close();
    }
}