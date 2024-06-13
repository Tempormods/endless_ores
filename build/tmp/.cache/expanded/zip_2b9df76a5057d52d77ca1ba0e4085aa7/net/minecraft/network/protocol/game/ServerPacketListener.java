package net.minecraft.network.protocol.game;

import com.mojang.logging.LogUtils;
import net.minecraft.ReportedException;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public interface ServerPacketListener extends ServerboundPacketListener {
    Logger f_315882_ = LogUtils.getLogger();

    @Override
    default void m_322364_(Packet p_335210_, Exception p_330447_) throws ReportedException {
        f_315882_.error("Failed to handle packet {}, suppressing error", p_335210_, p_330447_);
    }
}