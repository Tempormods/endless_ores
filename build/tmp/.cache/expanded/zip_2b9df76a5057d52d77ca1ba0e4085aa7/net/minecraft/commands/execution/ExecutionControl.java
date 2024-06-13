package net.minecraft.commands.execution;

import javax.annotation.Nullable;
import net.minecraft.commands.ExecutionCommandSource;

public interface ExecutionControl<T> {
    void m_306270_(EntryAction<T> p_309475_);

    void m_304947_(@Nullable TraceCallbacks p_309557_);

    @Nullable
    TraceCallbacks m_305414_();

    Frame m_305000_();

    static <T extends ExecutionCommandSource<T>> ExecutionControl<T> m_307232_(final ExecutionContext<T> p_310088_, final Frame p_312154_) {
        return new ExecutionControl<T>() {
            @Override
            public void m_306270_(EntryAction<T> p_311389_) {
                p_310088_.m_307907_(new CommandQueueEntry<>(p_312154_, p_311389_));
            }

            @Override
            public void m_304947_(@Nullable TraceCallbacks p_313185_) {
                p_310088_.m_305996_(p_313185_);
            }

            @Nullable
            @Override
            public TraceCallbacks m_305414_() {
                return p_310088_.m_307286_();
            }

            @Override
            public Frame m_305000_() {
                return p_312154_;
            }
        };
    }
}