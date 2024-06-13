package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreateSnapshotRealmTask extends LongRunningTask {
    private static final Logger f_302716_ = LogUtils.getLogger();
    private static final Component f_302188_ = Component.translatable("mco.snapshot.creating");
    private final long f_302426_;
    private final WorldGenerationInfo f_303420_;
    private final String f_302954_;
    private final String f_303144_;
    private final RealmsMainScreen f_303384_;
    @Nullable
    private RealmCreationTask f_302695_;
    @Nullable
    private ResettingGeneratedWorldTask f_303434_;

    public CreateSnapshotRealmTask(RealmsMainScreen p_310360_, long p_313181_, WorldGenerationInfo p_311456_, String p_310357_, String p_312107_) {
        this.f_302426_ = p_313181_;
        this.f_303420_ = p_311456_;
        this.f_302954_ = p_310357_;
        this.f_303144_ = p_312107_;
        this.f_303384_ = p_310360_;
    }

    @Override
    public void run() {
        RealmsClient realmsclient = RealmsClient.create();

        try {
            RealmsServer realmsserver = realmsclient.m_305378_(this.f_302426_);
            this.f_302695_ = new RealmCreationTask(realmsserver.id, this.f_302954_, this.f_303144_);
            this.f_303434_ = new ResettingGeneratedWorldTask(
                this.f_303420_,
                realmsserver.id,
                RealmsResetWorldScreen.CREATE_WORLD_RESET_TASK_TITLE,
                () -> Minecraft.getInstance().execute(() -> RealmsMainScreen.m_307704_(realmsserver, this.f_303384_, true))
            );
            if (this.aborted()) {
                return;
            }

            this.f_302695_.run();
            if (this.aborted()) {
                return;
            }

            this.f_303434_.run();
        } catch (RealmsServiceException realmsserviceexception) {
            f_302716_.error("Couldn't create snapshot world", (Throwable)realmsserviceexception);
            this.error(realmsserviceexception);
        } catch (Exception exception) {
            f_302716_.error("Couldn't create snapshot world", (Throwable)exception);
            this.error(exception);
        }
    }

    @Override
    public Component getTitle() {
        return f_302188_;
    }

    @Override
    public void abortTask() {
        super.abortTask();
        if (this.f_302695_ != null) {
            this.f_302695_.abortTask();
        }

        if (this.f_303434_ != null) {
            this.f_303434_.abortTask();
        }
    }
}