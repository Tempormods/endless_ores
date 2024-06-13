package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;

@OnlyIn(Dist.CLIENT)
public class ChunkedSampleByteBuf implements FloatConsumer {
    private final List<ByteBuffer> f_314624_ = Lists.newArrayList();
    private final int f_315988_;
    private int f_314867_;
    private ByteBuffer f_316518_;

    public ChunkedSampleByteBuf(int p_330452_) {
        this.f_315988_ = p_330452_ + 1 & -2;
        this.f_316518_ = BufferUtils.createByteBuffer(p_330452_);
    }

    @Override
    public void accept(float p_332948_) {
        if (this.f_316518_.remaining() == 0) {
            this.f_316518_.flip();
            this.f_314624_.add(this.f_316518_);
            this.f_316518_ = BufferUtils.createByteBuffer(this.f_315988_);
        }

        int i = Mth.clamp((int)(p_332948_ * 32767.5F - 0.5F), -32768, 32767);
        this.f_316518_.putShort((short)i);
        this.f_314867_ += 2;
    }

    public ByteBuffer m_324173_() {
        this.f_316518_.flip();
        if (this.f_314624_.isEmpty()) {
            return this.f_316518_;
        } else {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(this.f_314867_);
            this.f_314624_.forEach(bytebuffer::put);
            bytebuffer.put(this.f_316518_);
            bytebuffer.flip();
            return bytebuffer;
        }
    }

    public int m_320382_() {
        return this.f_314867_;
    }
}