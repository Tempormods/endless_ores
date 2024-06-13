package net.minecraft.util.debugchart;

import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;

public class RemoteSampleLogger extends AbstractSampleLogger {
    private final DebugSampleSubscriptionTracker f_315086_;
    private final RemoteDebugSampleType f_316206_;

    public RemoteSampleLogger(int p_329489_, DebugSampleSubscriptionTracker p_332606_, RemoteDebugSampleType p_331596_) {
        this(p_329489_, p_332606_, p_331596_, new long[p_329489_]);
    }

    public RemoteSampleLogger(int p_334352_, DebugSampleSubscriptionTracker p_334313_, RemoteDebugSampleType p_332243_, long[] p_333261_) {
        super(p_334352_, p_333261_);
        this.f_315086_ = p_334313_;
        this.f_316206_ = p_332243_;
    }

    @Override
    protected void m_322272_() {
        this.f_315086_.m_319135_(new ClientboundDebugSamplePacket((long[])this.f_313991_.clone(), this.f_316206_));
    }
}