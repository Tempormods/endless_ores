package net.minecraft.commands.execution;

import net.minecraft.commands.CommandResultCallback;

public record Frame(int f_303315_, CommandResultCallback f_302691_, Frame.FrameControl f_303027_) {
    public void m_305055_(int p_309471_) {
        this.f_302691_.m_306612_(p_309471_);
    }

    public void m_307468_() {
        this.f_302691_.m_307284_();
    }

    public void m_304718_() {
        this.f_303027_.m_304753_();
    }

    @FunctionalInterface
    public interface FrameControl {
        void m_304753_();
    }
}