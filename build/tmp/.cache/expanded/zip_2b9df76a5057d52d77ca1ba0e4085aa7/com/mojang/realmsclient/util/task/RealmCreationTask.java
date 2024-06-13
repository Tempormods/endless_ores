package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmCreationTask extends LongRunningTask {
    private static final Logger f_315267_ = LogUtils.getLogger();
    private static final Component f_315898_ = Component.translatable("mco.create.world.wait");
    private final String f_316782_;
    private final String f_315105_;
    private final long f_314292_;

    public RealmCreationTask(long p_329245_, String p_335972_, String p_329587_) {
        this.f_314292_ = p_329245_;
        this.f_316782_ = p_335972_;
        this.f_315105_ = p_329587_;
    }

    @Override
    public void run() {
        RealmsClient realmsclient = RealmsClient.create();

        try {
            realmsclient.initializeWorld(this.f_314292_, this.f_316782_, this.f_315105_);
        } catch (RealmsServiceException realmsserviceexception) {
            f_315267_.error("Couldn't create world", (Throwable)realmsserviceexception);
            this.error(realmsserviceexception);
        } catch (Exception exception) {
            f_315267_.error("Could not create world", (Throwable)exception);
            this.error(exception);
        }
    }

    @Override
    public Component getTitle() {
        return f_315898_;
    }
}