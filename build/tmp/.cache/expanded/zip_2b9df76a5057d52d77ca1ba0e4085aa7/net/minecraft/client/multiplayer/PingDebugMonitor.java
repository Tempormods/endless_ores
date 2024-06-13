package net.minecraft.client.multiplayer;

import net.minecraft.Util;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PingDebugMonitor {
    private final ClientPacketListener connection;
    private final LocalSampleLogger delayTimer;

    public PingDebugMonitor(ClientPacketListener pConnection, LocalSampleLogger p_334867_) {
        this.connection = pConnection;
        this.delayTimer = p_334867_;
    }

    public void tick() {
        this.connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
    }

    public void onPongReceived(ClientboundPongResponsePacket p_328021_) {
        this.delayTimer.m_322732_(Util.getMillis() - p_328021_.f_315106_());
    }
}