package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record PacketIdentification(String f_316962_, String f_315442_, String f_314890_) {
    public static PacketIdentification m_324080_(RecordedEvent p_333273_) {
        return new PacketIdentification(p_333273_.getString("packetDirection"), p_333273_.getString("protocolId"), p_333273_.getString("packetId"));
    }
}