package net.minecraft.commands.execution.tasks;

import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class FallthroughTask<T extends ExecutionCommandSource<T>> implements EntryAction<T> {
    private static final FallthroughTask<? extends ExecutionCommandSource<?>> f_303135_ = (FallthroughTask<? extends ExecutionCommandSource<?>>)(new FallthroughTask<>());

    public static <T extends ExecutionCommandSource<T>> EntryAction<T> m_304796_() {
        return (EntryAction<T>)f_303135_;
    }

    @Override
    public void m_305380_(ExecutionContext<T> p_311441_, Frame p_309937_) {
        p_309937_.m_307468_();
        p_309937_.m_304718_();
    }
}