package net.minecraft.gametest.framework;

public record RetryOptions(int f_316969_, boolean f_316596_) {
    private static final RetryOptions f_316318_ = new RetryOptions(1, true);

    public static RetryOptions m_321305_() {
        return f_316318_;
    }

    public boolean m_319667_() {
        return this.f_316969_ < 1;
    }

    public boolean m_320775_(int p_334342_, int p_328826_) {
        boolean flag = p_334342_ != p_328826_;
        boolean flag1 = this.m_319667_() || p_334342_ < this.f_316969_;
        return flag1 && (!flag || !this.f_316596_);
    }

    public boolean m_319078_() {
        return this.f_316969_ != 1;
    }
}