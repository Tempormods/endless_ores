package net.minecraft.util.debugchart;

public abstract class AbstractSampleLogger implements SampleLogger {
    protected final long[] f_316224_;
    protected final long[] f_313991_;

    protected AbstractSampleLogger(int p_330199_, long[] p_328152_) {
        if (p_328152_.length != p_330199_) {
            throw new IllegalArgumentException("defaults have incorrect length of " + p_328152_.length);
        } else {
            this.f_313991_ = new long[p_330199_];
            this.f_316224_ = p_328152_;
        }
    }

    @Override
    public void m_320889_(long[] p_334735_) {
        System.arraycopy(p_334735_, 0, this.f_313991_, 0, p_334735_.length);
        this.m_322272_();
        this.m_323180_();
    }

    @Override
    public void m_322732_(long p_328993_) {
        this.f_313991_[0] = p_328993_;
        this.m_322272_();
        this.m_323180_();
    }

    @Override
    public void m_319503_(long p_330576_, int p_334353_) {
        if (p_334353_ >= 1 && p_334353_ < this.f_313991_.length) {
            this.f_313991_[p_334353_] = p_330576_;
        } else {
            throw new IndexOutOfBoundsException(p_334353_ + " out of bounds for dimensions " + this.f_313991_.length);
        }
    }

    protected abstract void m_322272_();

    protected void m_323180_() {
        System.arraycopy(this.f_316224_, 0, this.f_313991_, 0, this.f_316224_.length);
    }
}