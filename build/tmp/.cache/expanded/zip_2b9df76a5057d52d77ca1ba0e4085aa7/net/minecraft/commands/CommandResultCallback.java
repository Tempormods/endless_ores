package net.minecraft.commands;

@FunctionalInterface
public interface CommandResultCallback {
    CommandResultCallback f_302577_ = new CommandResultCallback() {
        @Override
        public void m_306252_(boolean p_310694_, int p_309781_) {
        }

        @Override
        public String toString() {
            return "<empty>";
        }
    };

    void m_306252_(boolean p_312490_, int p_311494_);

    default void m_306612_(int p_312969_) {
        this.m_306252_(true, p_312969_);
    }

    default void m_307284_() {
        this.m_306252_(false, 0);
    }

    static CommandResultCallback m_304670_(CommandResultCallback p_312991_, CommandResultCallback p_310583_) {
        if (p_312991_ == f_302577_) {
            return p_310583_;
        } else {
            return p_310583_ == f_302577_ ? p_312991_ : (p_311372_, p_312527_) -> {
                p_312991_.m_306252_(p_311372_, p_312527_);
                p_310583_.m_306252_(p_311372_, p_312527_);
            };
        }
    }
}