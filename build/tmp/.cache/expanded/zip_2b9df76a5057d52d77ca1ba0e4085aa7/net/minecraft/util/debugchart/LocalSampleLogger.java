package net.minecraft.util.debugchart;

public class LocalSampleLogger extends AbstractSampleLogger implements SampleStorage {
    public static final int f_315422_ = 240;
    private final long[][] f_314303_;
    private int f_316546_;
    private int f_314454_;

    public LocalSampleLogger(int p_334158_) {
        this(p_334158_, new long[p_334158_]);
    }

    public LocalSampleLogger(int p_330975_, long[] p_333573_) {
        super(p_330975_, p_333573_);
        this.f_314303_ = new long[240][p_330975_];
    }

    @Override
    protected void m_322272_() {
        int i = this.m_321217_(this.f_316546_ + this.f_314454_);
        System.arraycopy(this.f_313991_, 0, this.f_314303_[i], 0, this.f_313991_.length);
        if (this.f_314454_ < 240) {
            this.f_314454_++;
        } else {
            this.f_316546_ = this.m_321217_(this.f_316546_ + 1);
        }
    }

    @Override
    public int m_323740_() {
        return this.f_314303_.length;
    }

    @Override
    public int m_322219_() {
        return this.f_314454_;
    }

    @Override
    public long m_318870_(int p_334223_) {
        return this.m_320960_(p_334223_, 0);
    }

    @Override
    public long m_320960_(int p_335582_, int p_331656_) {
        if (p_335582_ >= 0 && p_335582_ < this.f_314454_) {
            long[] along = this.f_314303_[this.m_321217_(this.f_316546_ + p_335582_)];
            if (p_331656_ >= 0 && p_331656_ < along.length) {
                return along[p_331656_];
            } else {
                throw new IndexOutOfBoundsException(p_331656_ + " out of bounds for dimensions " + along.length);
            }
        } else {
            throw new IndexOutOfBoundsException(p_335582_ + " out of bounds for length " + this.f_314454_);
        }
    }

    private int m_321217_(int p_330672_) {
        return p_330672_ % 240;
    }

    @Override
    public void m_320406_() {
        this.f_316546_ = 0;
        this.f_314454_ = 0;
    }
}