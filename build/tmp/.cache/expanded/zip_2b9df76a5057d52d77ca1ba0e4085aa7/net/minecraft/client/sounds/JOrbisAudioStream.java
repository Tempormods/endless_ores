package net.minecraft.client.sounds;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JOrbisAudioStream implements FloatSampleSource {
    private static final int f_315741_ = 8192;
    private static final int f_315262_ = -1;
    private static final int f_315700_ = 0;
    private static final int f_314653_ = 1;
    private static final int f_314092_ = -1;
    private static final int f_316317_ = 0;
    private static final int f_313936_ = 1;
    private final SyncState f_316477_ = new SyncState();
    private final Page f_313915_ = new Page();
    private final StreamState f_315291_ = new StreamState();
    private final Packet f_314058_ = new Packet();
    private final Info f_314420_ = new Info();
    private final DspState f_313950_ = new DspState();
    private final Block f_314207_ = new Block(this.f_313950_);
    private final AudioFormat f_316992_;
    private final InputStream f_316695_;
    private long f_314457_;
    private long f_316426_ = Long.MAX_VALUE;

    public JOrbisAudioStream(InputStream p_329659_) throws IOException {
        this.f_316695_ = p_329659_;
        Comment comment = new Comment();
        Page page = this.m_321182_();
        if (page == null) {
            throw new IOException("Invalid Ogg file - can't find first page");
        } else {
            Packet packet = this.m_319002_(page);
            if (m_320752_(this.f_314420_.synthesis_headerin(comment, packet))) {
                throw new IOException("Invalid Ogg identification packet");
            } else {
                for (int i = 0; i < 2; i++) {
                    packet = this.m_321871_();
                    if (packet == null) {
                        throw new IOException("Unexpected end of Ogg stream");
                    }

                    if (m_320752_(this.f_314420_.synthesis_headerin(comment, packet))) {
                        throw new IOException("Invalid Ogg header packet " + i);
                    }
                }

                this.f_313950_.synthesis_init(this.f_314420_);
                this.f_314207_.init(this.f_313950_);
                this.f_316992_ = new AudioFormat((float)this.f_314420_.rate, 16, this.f_314420_.channels, true, false);
            }
        }
    }

    private static boolean m_320752_(int p_335098_) {
        return p_335098_ < 0;
    }

    @Override
    public AudioFormat getFormat() {
        return this.f_316992_;
    }

    private boolean m_319989_() throws IOException {
        int i = this.f_316477_.buffer(8192);
        byte[] abyte = this.f_316477_.data;
        int j = this.f_316695_.read(abyte, i, 8192);
        if (j == -1) {
            return false;
        } else {
            this.f_316477_.wrote(j);
            return true;
        }
    }

    @Nullable
    private Page m_321182_() throws IOException {
        while (true) {
            int i = this.f_316477_.pageout(this.f_313915_);
            switch (i) {
                case -1:
                    throw new IllegalStateException("Corrupt or missing data in bitstream");
                case 0:
                    if (this.m_319989_()) {
                        break;
                    }

                    return null;
                case 1:
                    if (this.f_313915_.eos() != 0) {
                        this.f_316426_ = this.f_313915_.granulepos();
                    }

                    return this.f_313915_;
                default:
                    throw new IllegalStateException("Unknown page decode result: " + i);
            }
        }
    }

    private Packet m_319002_(Page p_329701_) throws IOException {
        this.f_315291_.init(p_329701_.serialno());
        if (m_320752_(this.f_315291_.pagein(p_329701_))) {
            throw new IOException("Failed to parse page");
        } else {
            int i = this.f_315291_.packetout(this.f_314058_);
            if (i != 1) {
                throw new IOException("Failed to read identification packet: " + i);
            } else {
                return this.f_314058_;
            }
        }
    }

    @Nullable
    private Packet m_321871_() throws IOException {
        while (true) {
            int i = this.f_315291_.packetout(this.f_314058_);
            switch (i) {
                case -1:
                    throw new IOException("Failed to parse packet");
                case 0:
                    Page page = this.m_321182_();
                    if (page == null) {
                        return null;
                    }

                    if (!m_320752_(this.f_315291_.pagein(page))) {
                        break;
                    }

                    throw new IOException("Failed to parse page");
                case 1:
                    return this.f_314058_;
                default:
                    throw new IllegalStateException("Unknown packet decode result: " + i);
            }
        }
    }

    private long m_321538_(int p_328687_) {
        long i = this.f_314457_ + (long)p_328687_;
        long j;
        if (i > this.f_316426_) {
            j = this.f_316426_ - this.f_314457_;
            this.f_314457_ = this.f_316426_;
        } else {
            this.f_314457_ = i;
            j = (long)p_328687_;
        }

        return j;
    }

    @Override
    public boolean m_319484_(FloatConsumer p_335177_) throws IOException {
        float[][][] afloat = new float[1][][];
        int[] aint = new int[this.f_314420_.channels];
        Packet packet = this.m_321871_();
        if (packet == null) {
            return false;
        } else if (m_320752_(this.f_314207_.synthesis(packet))) {
            throw new IOException("Can't decode audio packet");
        } else {
            this.f_313950_.synthesis_blockin(this.f_314207_);

            int i;
            while ((i = this.f_313950_.synthesis_pcmout(afloat, aint)) > 0) {
                float[][] afloat1 = afloat[0];
                long j = this.m_321538_(i);
                switch (this.f_314420_.channels) {
                    case 1:
                        m_324724_(afloat1[0], aint[0], j, p_335177_);
                        break;
                    case 2:
                        m_320725_(afloat1[0], aint[0], afloat1[1], aint[1], j, p_335177_);
                        break;
                    default:
                        m_322238_(afloat1, this.f_314420_.channels, aint, j, p_335177_);
                }

                this.f_313950_.synthesis_read(i);
            }

            return true;
        }
    }

    private static void m_322238_(float[][] p_327919_, int p_335236_, int[] p_332016_, long p_329945_, FloatConsumer p_335757_) {
        for (int i = 0; (long)i < p_329945_; i++) {
            for (int j = 0; j < p_335236_; j++) {
                int k = p_332016_[j];
                float f = p_327919_[j][k + i];
                p_335757_.accept(f);
            }
        }
    }

    private static void m_324724_(float[] p_332068_, int p_333939_, long p_329906_, FloatConsumer p_336173_) {
        for (int i = p_333939_; (long)i < (long)p_333939_ + p_329906_; i++) {
            p_336173_.accept(p_332068_[i]);
        }
    }

    private static void m_320725_(float[] p_329921_, int p_328265_, float[] p_331752_, int p_331871_, long p_328398_, FloatConsumer p_335978_) {
        for (int i = 0; (long)i < p_328398_; i++) {
            p_335978_.accept(p_329921_[p_328265_ + i]);
            p_335978_.accept(p_331752_[p_331871_ + i]);
        }
    }

    @Override
    public void close() throws IOException {
        this.f_316695_.close();
    }
}