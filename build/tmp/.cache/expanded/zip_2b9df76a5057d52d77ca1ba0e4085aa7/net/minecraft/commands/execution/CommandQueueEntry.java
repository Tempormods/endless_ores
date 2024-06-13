package net.minecraft.commands.execution;

public record CommandQueueEntry<T>(Frame f_303653_, EntryAction<T> f_302701_) {
    public void m_305080_(ExecutionContext<T> p_310616_) {
        this.f_302701_.m_305380_(p_310616_, this.f_303653_);
    }
}