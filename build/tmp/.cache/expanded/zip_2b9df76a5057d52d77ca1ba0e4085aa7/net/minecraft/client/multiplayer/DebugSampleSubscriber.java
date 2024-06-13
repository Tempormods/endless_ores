package net.minecraft.client.multiplayer;

import java.util.EnumMap;
import net.minecraft.Util;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.protocol.game.ServerboundDebugSampleSubscriptionPacket;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugSampleSubscriber {
    public static final int f_314359_ = 5000;
    private final ClientPacketListener f_315675_;
    private final DebugScreenOverlay f_316341_;
    private final EnumMap<RemoteDebugSampleType, Long> f_316120_;

    public DebugSampleSubscriber(ClientPacketListener p_334264_, DebugScreenOverlay p_327939_) {
        this.f_316341_ = p_327939_;
        this.f_315675_ = p_334264_;
        this.f_316120_ = new EnumMap<>(RemoteDebugSampleType.class);
    }

    public void m_322605_() {
        if (this.f_316341_.m_321865_()) {
            this.m_322957_(RemoteDebugSampleType.TICK_TIME);
        }
    }

    private void m_322957_(RemoteDebugSampleType p_333324_) {
        long i = Util.getMillis();
        if (i > this.f_316120_.getOrDefault(p_333324_, Long.valueOf(0L)) + 5000L) {
            this.f_315675_.send(new ServerboundDebugSampleSubscriptionPacket(p_333324_));
            this.f_316120_.put(p_333324_, i);
        }
    }
}