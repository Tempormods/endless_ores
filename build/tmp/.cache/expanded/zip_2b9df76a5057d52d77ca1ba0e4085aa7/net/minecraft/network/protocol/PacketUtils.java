package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketListener;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;

public class PacketUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> pPacket, T pProcessor, ServerLevel pLevel) throws RunningOnDifferentThreadException {
        ensureRunningOnSameThread(pPacket, pProcessor, pLevel.getServer());
    }

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> pPacket, T pProcessor, BlockableEventLoop<?> pExecutor) throws RunningOnDifferentThreadException {
        if (!pExecutor.isSameThread()) {
            pExecutor.executeIfPossible(() -> {
                if (pProcessor.shouldHandleMessage(pPacket)) {
                    try {
                        pPacket.handle(pProcessor);
                    } catch (Exception exception) {
                        if (exception instanceof ReportedException reportedexception && reportedexception.getCause() instanceof OutOfMemoryError) {
                            throw m_322247_(exception, pPacket, pProcessor);
                        }

                        pProcessor.m_322364_(pPacket, exception);
                    }
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: {}", pPacket);
                }
            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
        }
    }

    public static <T extends PacketListener> ReportedException m_322247_(Exception p_331079_, Packet<T> p_335356_, T p_332020_) {
        if (p_331079_ instanceof ReportedException reportedexception) {
            m_323092_(reportedexception.getReport(), p_332020_, p_335356_);
            return reportedexception;
        } else {
            CrashReport crashreport = CrashReport.forThrowable(p_331079_, "Main thread packet handler");
            m_323092_(crashreport, p_332020_, p_335356_);
            return new ReportedException(crashreport);
        }
    }

    private static <T extends PacketListener> void m_323092_(CrashReport p_330590_, T p_333816_, Packet<T> p_330069_) {
        CrashReportCategory crashreportcategory = p_330590_.addCategory("Incoming Packet");
        crashreportcategory.setDetail("Type", () -> p_330069_.write().toString());
        crashreportcategory.setDetail("Is Terminal", () -> Boolean.toString(p_330069_.m_319635_()));
        crashreportcategory.setDetail("Is Skippable", () -> Boolean.toString(p_330069_.isSkippable()));
        p_333816_.m_307358_(p_330590_);
    }
}