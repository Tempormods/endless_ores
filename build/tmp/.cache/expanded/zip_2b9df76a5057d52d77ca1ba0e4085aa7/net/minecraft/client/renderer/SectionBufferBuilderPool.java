package net.minecraft.client.renderer;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SectionBufferBuilderPool {
    private static final Logger f_303160_ = LogUtils.getLogger();
    private final Queue<SectionBufferBuilderPack> f_302413_;
    private volatile int f_303717_;

    private SectionBufferBuilderPool(List<SectionBufferBuilderPack> p_312374_) {
        this.f_302413_ = Queues.newArrayDeque(p_312374_);
        this.f_303717_ = this.f_302413_.size();
    }

    public static SectionBufferBuilderPool m_306138_(int p_310783_) {
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / SectionBufferBuilderPack.f_303427_);
        int j = Math.max(1, Math.min(p_310783_, i));
        List<SectionBufferBuilderPack> list = new ArrayList<>(j);

        try {
            for (int k = 0; k < j; k++) {
                list.add(new SectionBufferBuilderPack());
            }
        } catch (OutOfMemoryError outofmemoryerror) {
            f_303160_.warn("Allocated only {}/{} buffers", list.size(), j);
            int l = Math.min(list.size() * 2 / 3, list.size() - 1);

            for (int i1 = 0; i1 < l; i1++) {
                list.remove(list.size() - 1).close();
            }
        }

        return new SectionBufferBuilderPool(list);
    }

    @Nullable
    public SectionBufferBuilderPack m_307873_() {
        SectionBufferBuilderPack sectionbufferbuilderpack = this.f_302413_.poll();
        if (sectionbufferbuilderpack != null) {
            this.f_303717_ = this.f_302413_.size();
            return sectionbufferbuilderpack;
        } else {
            return null;
        }
    }

    public void m_306477_(SectionBufferBuilderPack p_310220_) {
        this.f_302413_.add(p_310220_);
        this.f_303717_ = this.f_302413_.size();
    }

    public boolean m_307681_() {
        return this.f_302413_.isEmpty();
    }

    public int m_306121_() {
        return this.f_303717_;
    }
}