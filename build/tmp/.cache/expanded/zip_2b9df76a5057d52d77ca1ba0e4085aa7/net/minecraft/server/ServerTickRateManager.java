package net.minecraft.server;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;

public class ServerTickRateManager extends TickRateManager {
    private long f_302629_ = 0L;
    private long f_302324_ = 0L;
    private long f_302728_ = 0L;
    private long f_302200_ = 0L;
    private boolean f_303550_ = false;
    private final MinecraftServer f_303799_;

    public ServerTickRateManager(MinecraftServer p_311395_) {
        this.f_303799_ = p_311395_;
    }

    public boolean m_306078_() {
        return this.f_302200_ > 0L;
    }

    @Override
    public void m_306419_(boolean p_313235_) {
        super.m_306419_(p_313235_);
        this.m_306466_();
    }

    private void m_306466_() {
        this.f_303799_.getPlayerList().broadcastAll(ClientboundTickingStatePacket.m_307319_(this));
    }

    private void m_305862_() {
        this.f_303799_.getPlayerList().broadcastAll(ClientboundTickingStepPacket.m_305989_(this));
    }

    public boolean m_305215_(int p_312205_) {
        if (!this.m_306363_()) {
            return false;
        } else {
            this.f_303482_ = p_312205_;
            this.m_305862_();
            return true;
        }
    }

    public boolean m_307971_() {
        if (this.f_303482_ > 0) {
            this.f_303482_ = 0;
            this.m_305862_();
            return true;
        } else {
            return false;
        }
    }

    public boolean m_306594_() {
        if (this.f_302629_ > 0L) {
            this.m_306313_();
            return true;
        } else {
            return false;
        }
    }

    public boolean m_305001_(int p_311983_) {
        boolean flag = this.f_302629_ > 0L;
        this.f_302728_ = 0L;
        this.f_302200_ = (long)p_311983_;
        this.f_302629_ = (long)p_311983_;
        this.f_303550_ = this.m_306363_();
        this.m_306419_(false);
        return flag;
    }

    private void m_306313_() {
        long i = this.f_302200_ - this.f_302629_;
        double d0 = Math.max(1.0, (double)this.f_302728_) / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
        int j = (int)((double)(TimeUtil.f_302812_ * i) / d0);
        String s = String.format("%.2f", i == 0L ? (double)this.m_305111_() : d0 / (double)i);
        this.f_302200_ = 0L;
        this.f_302728_ = 0L;
        this.f_303799_.createCommandSourceStack().sendSuccess(() -> Component.translatable("commands.tick.sprint.report", j, s), true);
        this.f_302629_ = 0L;
        this.m_306419_(this.f_303550_);
        this.f_303799_.m_305364_();
    }

    public boolean m_307076_() {
        if (!this.f_302370_) {
            return false;
        } else if (this.f_302629_ > 0L) {
            this.f_302324_ = System.nanoTime();
            this.f_302629_--;
            return true;
        } else {
            this.m_306313_();
            return false;
        }
    }

    public void m_307618_() {
        this.f_302728_ = this.f_302728_ + (System.nanoTime() - this.f_302324_);
    }

    @Override
    public void m_307254_(float p_312065_) {
        super.m_307254_(p_312065_);
        this.f_303799_.m_305364_();
        this.m_306466_();
    }

    public void m_305873_(ServerPlayer p_310808_) {
        p_310808_.connection.send(ClientboundTickingStatePacket.m_307319_(this));
        p_310808_.connection.send(ClientboundTickingStepPacket.m_305989_(this));
    }
}