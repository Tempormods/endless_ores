package net.minecraft.client.resources.server;

import com.google.common.hash.HashCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.packs.DownloadQueue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerPackManager {
    private final PackDownloader f_302295_;
    final PackLoadFeedback f_303189_;
    private final PackReloadConfig f_302993_;
    private final Runnable f_302694_;
    private ServerPackManager.PackPromptStatus f_303279_;
    final List<ServerPackManager.ServerPackData> f_302451_ = new ArrayList<>();

    public ServerPackManager(
        PackDownloader p_313039_, PackLoadFeedback p_311463_, PackReloadConfig p_312595_, Runnable p_310909_, ServerPackManager.PackPromptStatus p_311512_
    ) {
        this.f_302295_ = p_313039_;
        this.f_303189_ = p_311463_;
        this.f_302993_ = p_312595_;
        this.f_302694_ = p_310909_;
        this.f_303279_ = p_311512_;
    }

    void m_307110_() {
        this.f_302694_.run();
    }

    private void m_307975_(UUID p_309694_) {
        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (serverpackmanager$serverpackdata.f_303673_.equals(p_309694_)) {
                serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.SERVER_REPLACED);
            }
        }
    }

    public void m_307356_(UUID p_309690_, URL p_312710_, @Nullable HashCode p_312316_) {
        if (this.f_303279_ == ServerPackManager.PackPromptStatus.DECLINED) {
            this.f_303189_.m_305621_(p_309690_, PackLoadFeedback.FinalResult.DECLINED);
        } else {
            this.m_305562_(p_309690_, new ServerPackManager.ServerPackData(p_309690_, p_312710_, p_312316_));
        }
    }

    public void m_306969_(UUID p_312688_, Path p_312014_) {
        if (this.f_303279_ == ServerPackManager.PackPromptStatus.DECLINED) {
            this.f_303189_.m_305621_(p_312688_, PackLoadFeedback.FinalResult.DECLINED);
        } else {
            URL url;
            try {
                url = p_312014_.toUri().toURL();
            } catch (MalformedURLException malformedurlexception) {
                throw new IllegalStateException("Can't convert path to URL " + p_312014_, malformedurlexception);
            }

            ServerPackManager.ServerPackData serverpackmanager$serverpackdata = new ServerPackManager.ServerPackData(p_312688_, url, null);
            serverpackmanager$serverpackdata.f_302455_ = ServerPackManager.PackDownloadStatus.DONE;
            serverpackmanager$serverpackdata.f_303540_ = p_312014_;
            this.m_305562_(p_312688_, serverpackmanager$serverpackdata);
        }
    }

    private void m_305562_(UUID p_312820_, ServerPackManager.ServerPackData p_310310_) {
        this.m_307975_(p_312820_);
        this.f_302451_.add(p_310310_);
        if (this.f_303279_ == ServerPackManager.PackPromptStatus.ALLOWED) {
            this.m_305809_(p_310310_);
        }

        this.m_307110_();
    }

    private void m_305809_(ServerPackManager.ServerPackData p_309901_) {
        this.f_303189_.m_304984_(p_309901_.f_303673_, PackLoadFeedback.Update.ACCEPTED);
        p_309901_.f_302595_ = true;
    }

    @Nullable
    private ServerPackManager.ServerPackData m_307563_(UUID p_312512_) {
        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (!serverpackmanager$serverpackdata.m_306607_() && serverpackmanager$serverpackdata.f_303673_.equals(p_312512_)) {
                return serverpackmanager$serverpackdata;
            }
        }

        return null;
    }

    public void m_307735_(UUID p_312676_) {
        ServerPackManager.ServerPackData serverpackmanager$serverpackdata = this.m_307563_(p_312676_);
        if (serverpackmanager$serverpackdata != null) {
            serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.SERVER_REMOVED);
            this.m_307110_();
        }
    }

    public void m_304731_() {
        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.SERVER_REMOVED);
        }

        this.m_307110_();
    }

    public void m_306539_() {
        this.f_303279_ = ServerPackManager.PackPromptStatus.ALLOWED;

        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (!serverpackmanager$serverpackdata.f_302595_ && !serverpackmanager$serverpackdata.m_306607_()) {
                this.m_305809_(serverpackmanager$serverpackdata);
            }
        }

        this.m_307110_();
    }

    public void m_305870_() {
        this.f_303279_ = ServerPackManager.PackPromptStatus.DECLINED;

        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (!serverpackmanager$serverpackdata.f_302595_) {
                serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.DECLINED);
            }
        }

        this.m_307110_();
    }

    public void m_305384_() {
        this.f_303279_ = ServerPackManager.PackPromptStatus.PENDING;
    }

    public void m_307766_() {
        boolean flag = this.m_305887_();
        if (!flag) {
            this.m_307425_();
        }

        this.m_307551_();
    }

    private void m_307551_() {
        this.f_302451_.removeIf(p_312551_ -> {
            if (p_312551_.f_302874_ != ServerPackManager.ActivationStatus.INACTIVE) {
                return false;
            } else if (p_312551_.f_302652_ != null) {
                PackLoadFeedback.FinalResult packloadfeedback$finalresult = p_312551_.f_302652_.f_303257_;
                if (packloadfeedback$finalresult != null) {
                    this.f_303189_.m_305621_(p_312551_.f_303673_, packloadfeedback$finalresult);
                }

                return true;
            } else {
                return false;
            }
        });
    }

    private void m_307666_(Collection<ServerPackManager.ServerPackData> p_311905_, DownloadQueue.BatchResult p_312404_) {
        if (!p_312404_.f_303809_().isEmpty()) {
            for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
                if (serverpackmanager$serverpackdata.f_302874_ != ServerPackManager.ActivationStatus.ACTIVE) {
                    if (p_312404_.f_303809_().contains(serverpackmanager$serverpackdata.f_303673_)) {
                        serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.DOWNLOAD_FAILED);
                    } else {
                        serverpackmanager$serverpackdata.m_306869_(ServerPackManager.RemovalReason.DISCARDED);
                    }
                }
            }
        }

        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata1 : p_311905_) {
            Path path = p_312404_.f_302807_().get(serverpackmanager$serverpackdata1.f_303673_);
            if (path != null) {
                serverpackmanager$serverpackdata1.f_302455_ = ServerPackManager.PackDownloadStatus.DONE;
                serverpackmanager$serverpackdata1.f_303540_ = path;
                if (!serverpackmanager$serverpackdata1.m_306607_()) {
                    this.f_303189_.m_304984_(serverpackmanager$serverpackdata1.f_303673_, PackLoadFeedback.Update.DOWNLOADED);
                }
            }
        }

        this.m_307110_();
    }

    private boolean m_305887_() {
        List<ServerPackManager.ServerPackData> list = new ArrayList<>();
        boolean flag = false;

        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (!serverpackmanager$serverpackdata.m_306607_() && serverpackmanager$serverpackdata.f_302595_) {
                if (serverpackmanager$serverpackdata.f_302455_ != ServerPackManager.PackDownloadStatus.DONE) {
                    flag = true;
                }

                if (serverpackmanager$serverpackdata.f_302455_ == ServerPackManager.PackDownloadStatus.REQUESTED) {
                    serverpackmanager$serverpackdata.f_302455_ = ServerPackManager.PackDownloadStatus.PENDING;
                    list.add(serverpackmanager$serverpackdata);
                }
            }
        }

        if (!list.isEmpty()) {
            Map<UUID, DownloadQueue.DownloadRequest> map = new HashMap<>();

            for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata1 : list) {
                map.put(
                    serverpackmanager$serverpackdata1.f_303673_,
                    new DownloadQueue.DownloadRequest(serverpackmanager$serverpackdata1.f_303202_, serverpackmanager$serverpackdata1.f_303122_)
                );
            }

            this.f_302295_.m_305313_(map, p_310750_ -> this.m_307666_(list, p_310750_));
        }

        return flag;
    }

    private void m_307425_() {
        boolean flag = false;
        final List<ServerPackManager.ServerPackData> list = new ArrayList<>();
        final List<ServerPackManager.ServerPackData> list1 = new ArrayList<>();

        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata : this.f_302451_) {
            if (serverpackmanager$serverpackdata.f_302874_ == ServerPackManager.ActivationStatus.PENDING) {
                return;
            }

            boolean flag1 = serverpackmanager$serverpackdata.f_302595_
                && serverpackmanager$serverpackdata.f_302455_ == ServerPackManager.PackDownloadStatus.DONE
                && !serverpackmanager$serverpackdata.m_306607_();
            if (flag1 && serverpackmanager$serverpackdata.f_302874_ == ServerPackManager.ActivationStatus.INACTIVE) {
                list.add(serverpackmanager$serverpackdata);
                flag = true;
            }

            if (serverpackmanager$serverpackdata.f_302874_ == ServerPackManager.ActivationStatus.ACTIVE) {
                if (!flag1) {
                    flag = true;
                    list1.add(serverpackmanager$serverpackdata);
                } else {
                    list.add(serverpackmanager$serverpackdata);
                }
            }
        }

        if (flag) {
            for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata1 : list) {
                if (serverpackmanager$serverpackdata1.f_302874_ != ServerPackManager.ActivationStatus.ACTIVE) {
                    serverpackmanager$serverpackdata1.f_302874_ = ServerPackManager.ActivationStatus.PENDING;
                }
            }

            for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata2 : list1) {
                serverpackmanager$serverpackdata2.f_302874_ = ServerPackManager.ActivationStatus.PENDING;
            }

            this.f_302993_.m_305726_(new PackReloadConfig.Callbacks() {
                @Override
                public void m_304954_() {
                    for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata3 : list) {
                        serverpackmanager$serverpackdata3.f_302874_ = ServerPackManager.ActivationStatus.ACTIVE;
                        if (serverpackmanager$serverpackdata3.f_302652_ == null) {
                            ServerPackManager.this.f_303189_.m_305621_(serverpackmanager$serverpackdata3.f_303673_, PackLoadFeedback.FinalResult.APPLIED);
                        }
                    }

                    for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata4 : list1) {
                        serverpackmanager$serverpackdata4.f_302874_ = ServerPackManager.ActivationStatus.INACTIVE;
                    }

                    ServerPackManager.this.m_307110_();
                }

                @Override
                public void m_304685_(boolean p_311939_) {
                    if (!p_311939_) {
                        list.clear();

                        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata3 : ServerPackManager.this.f_302451_) {
                            switch (serverpackmanager$serverpackdata3.f_302874_) {
                                case INACTIVE:
                                    serverpackmanager$serverpackdata3.m_306869_(ServerPackManager.RemovalReason.DISCARDED);
                                    break;
                                case PENDING:
                                    serverpackmanager$serverpackdata3.f_302874_ = ServerPackManager.ActivationStatus.INACTIVE;
                                    serverpackmanager$serverpackdata3.m_306869_(ServerPackManager.RemovalReason.ACTIVATION_FAILED);
                                    break;
                                case ACTIVE:
                                    list.add(serverpackmanager$serverpackdata3);
                            }
                        }

                        ServerPackManager.this.m_307110_();
                    } else {
                        for (ServerPackManager.ServerPackData serverpackmanager$serverpackdata4 : ServerPackManager.this.f_302451_) {
                            if (serverpackmanager$serverpackdata4.f_302874_ == ServerPackManager.ActivationStatus.PENDING) {
                                serverpackmanager$serverpackdata4.f_302874_ = ServerPackManager.ActivationStatus.INACTIVE;
                            }
                        }
                    }
                }

                @Override
                public List<PackReloadConfig.IdAndPath> m_305324_() {
                    return list.stream().map(p_312955_ -> new PackReloadConfig.IdAndPath(p_312955_.f_303673_, p_312955_.f_303540_)).toList();
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum ActivationStatus {
        INACTIVE,
        PENDING,
        ACTIVE;
    }

    @OnlyIn(Dist.CLIENT)
    static enum PackDownloadStatus {
        REQUESTED,
        PENDING,
        DONE;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum PackPromptStatus {
        PENDING,
        ALLOWED,
        DECLINED;
    }

    @OnlyIn(Dist.CLIENT)
    static enum RemovalReason {
        DOWNLOAD_FAILED(PackLoadFeedback.FinalResult.DOWNLOAD_FAILED),
        ACTIVATION_FAILED(PackLoadFeedback.FinalResult.ACTIVATION_FAILED),
        DECLINED(PackLoadFeedback.FinalResult.DECLINED),
        DISCARDED(PackLoadFeedback.FinalResult.DISCARDED),
        SERVER_REMOVED(null),
        SERVER_REPLACED(null);

        @Nullable
        final PackLoadFeedback.FinalResult f_303257_;

        private RemovalReason(@Nullable final PackLoadFeedback.FinalResult p_312250_) {
            this.f_303257_ = p_312250_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ServerPackData {
        final UUID f_303673_;
        final URL f_303202_;
        @Nullable
        final HashCode f_303122_;
        @Nullable
        Path f_303540_;
        @Nullable
        ServerPackManager.RemovalReason f_302652_;
        ServerPackManager.PackDownloadStatus f_302455_ = ServerPackManager.PackDownloadStatus.REQUESTED;
        ServerPackManager.ActivationStatus f_302874_ = ServerPackManager.ActivationStatus.INACTIVE;
        boolean f_302595_;

        ServerPackData(UUID p_310861_, URL p_310292_, @Nullable HashCode p_311680_) {
            this.f_303673_ = p_310861_;
            this.f_303202_ = p_310292_;
            this.f_303122_ = p_311680_;
        }

        public void m_306869_(ServerPackManager.RemovalReason p_312334_) {
            if (this.f_302652_ == null) {
                this.f_302652_ = p_312334_;
            }
        }

        public boolean m_306607_() {
            return this.f_302652_ != null;
        }
    }
}