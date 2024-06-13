package net.minecraft.client.sounds;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface FloatSampleSource extends FiniteAudioStream {
    int f_315975_ = 8192;

    boolean m_319484_(FloatConsumer p_328436_) throws IOException;

    @Override
    default ByteBuffer read(int p_332929_) throws IOException {
        ChunkedSampleByteBuf chunkedsamplebytebuf = new ChunkedSampleByteBuf(p_332929_ + 8192);

        while (this.m_319484_(chunkedsamplebytebuf) && chunkedsamplebytebuf.m_320382_() < p_332929_) {
        }

        return chunkedsamplebytebuf.m_324173_();
    }

    @Override
    default ByteBuffer m_319707_() throws IOException {
        ChunkedSampleByteBuf chunkedsamplebytebuf = new ChunkedSampleByteBuf(16384);

        while (this.m_319484_(chunkedsamplebytebuf)) {
        }

        return chunkedsamplebytebuf.m_324173_();
    }
}