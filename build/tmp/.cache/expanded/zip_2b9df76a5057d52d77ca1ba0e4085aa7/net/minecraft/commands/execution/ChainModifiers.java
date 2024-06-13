package net.minecraft.commands.execution;

public record ChainModifiers(byte f_303171_) {
    public static final ChainModifiers f_302277_ = new ChainModifiers((byte)0);
    private static final byte f_302435_ = 1;
    private static final byte f_302512_ = 2;

    private ChainModifiers m_304751_(byte p_312898_) {
        int i = this.f_303171_ | p_312898_;
        return i != this.f_303171_ ? new ChainModifiers((byte)i) : this;
    }

    public boolean m_306471_() {
        return (this.f_303171_ & 1) != 0;
    }

    public ChainModifiers m_305062_() {
        return this.m_304751_((byte)1);
    }

    public boolean m_305036_() {
        return (this.f_303171_ & 2) != 0;
    }

    public ChainModifiers m_306088_() {
        return this.m_304751_((byte)2);
    }
}