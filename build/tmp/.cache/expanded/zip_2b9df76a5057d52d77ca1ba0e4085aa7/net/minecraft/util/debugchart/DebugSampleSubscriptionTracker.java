package net.minecraft.util.debugchart;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class DebugSampleSubscriptionTracker {
    public static final int f_316334_ = 200;
    public static final int f_315995_ = 10000;
    private final PlayerList f_316118_;
    private final EnumMap<RemoteDebugSampleType, Map<ServerPlayer, DebugSampleSubscriptionTracker.SubscriptionStartedAt>> f_317029_;
    private final Queue<DebugSampleSubscriptionTracker.SubscriptionRequest> f_316497_ = new LinkedList<>();

    public DebugSampleSubscriptionTracker(PlayerList p_332100_) {
        this.f_316118_ = p_332100_;
        this.f_317029_ = new EnumMap<>(RemoteDebugSampleType.class);

        for (RemoteDebugSampleType remotedebugsampletype : RemoteDebugSampleType.values()) {
            this.f_317029_.put(remotedebugsampletype, Maps.newHashMap());
        }
    }

    public boolean m_322854_(RemoteDebugSampleType p_328402_) {
        return !this.f_317029_.get(p_328402_).isEmpty();
    }

    public void m_319135_(ClientboundDebugSamplePacket p_331964_) {
        for (ServerPlayer serverplayer : this.f_317029_.get(p_331964_.f_316193_()).keySet()) {
            serverplayer.connection.send(p_331964_);
        }
    }

    public void m_320353_(ServerPlayer p_328157_, RemoteDebugSampleType p_336058_) {
        if (this.f_316118_.isOp(p_328157_.getGameProfile())) {
            this.f_316497_.add(new DebugSampleSubscriptionTracker.SubscriptionRequest(p_328157_, p_336058_));
        }
    }

    public void m_322357_(int p_335345_) {
        long i = Util.getMillis();
        this.m_322060_(i, p_335345_);
        this.m_320531_(i, p_335345_);
    }

    private void m_322060_(long p_331878_, int p_331066_) {
        for (DebugSampleSubscriptionTracker.SubscriptionRequest debugsamplesubscriptiontracker$subscriptionrequest : this.f_316497_) {
            this.f_317029_
                .get(debugsamplesubscriptiontracker$subscriptionrequest.f_316254_())
                .put(
                    debugsamplesubscriptiontracker$subscriptionrequest.f_316670_(),
                    new DebugSampleSubscriptionTracker.SubscriptionStartedAt(p_331878_, p_331066_)
                );
        }
    }

    private void m_320531_(long p_335801_, int p_335929_) {
        for (Map<ServerPlayer, DebugSampleSubscriptionTracker.SubscriptionStartedAt> map : this.f_317029_.values()) {
            map.entrySet()
                .removeIf(
                    p_336353_ -> {
                        boolean flag = !this.f_316118_.isOp(p_336353_.getKey().getGameProfile());
                        DebugSampleSubscriptionTracker.SubscriptionStartedAt debugsamplesubscriptiontracker$subscriptionstartedat = p_336353_.getValue();
                        return flag
                            || p_335929_ > debugsamplesubscriptiontracker$subscriptionstartedat.f_315963_() + 200
                                && p_335801_ > debugsamplesubscriptiontracker$subscriptionstartedat.f_315212_() + 10000L;
                    }
                );
        }
    }

    static record SubscriptionRequest(ServerPlayer f_316670_, RemoteDebugSampleType f_316254_) {
    }

    static record SubscriptionStartedAt(long f_315212_, int f_315963_) {
    }
}