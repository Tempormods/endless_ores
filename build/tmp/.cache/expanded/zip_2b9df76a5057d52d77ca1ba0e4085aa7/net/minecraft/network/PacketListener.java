package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;

/**
 * Describes how packets are handled. There are various implementations of this class for each possible protocol (e.g.
 * PLAY, CLIENTBOUND; PLAY, SERVERBOUND; etc.)
 */
public interface PacketListener {
    PacketFlow flow();

    ConnectionProtocol protocol();

    void onDisconnect(Component pReason);

    default void m_322364_(Packet p_330857_, Exception p_328275_) throws ReportedException {
        throw PacketUtils.m_322247_(p_328275_, p_330857_, this);
    }

    boolean isAcceptingMessages();

    default boolean shouldHandleMessage(Packet<?> pPacket) {
        return this.isAcceptingMessages();
    }

    default void m_307358_(CrashReport p_311292_) {
        CrashReportCategory crashreportcategory = p_311292_.addCategory("Connection");
        crashreportcategory.setDetail("Protocol", () -> this.protocol().id());
        crashreportcategory.setDetail("Flow", () -> this.flow().toString());
        this.m_306579_(crashreportcategory);
    }

    default void m_306579_(CrashReportCategory p_310872_) {
    }
}