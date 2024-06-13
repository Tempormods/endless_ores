package net.minecraft.commands.execution.tasks;

import java.util.function.Consumer;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;

public class IsolatedCall<T extends ExecutionCommandSource<T>> implements EntryAction<T> {
    private final Consumer<ExecutionControl<T>> f_302491_;
    private final CommandResultCallback f_303355_;

    public IsolatedCall(Consumer<ExecutionControl<T>> p_309522_, CommandResultCallback p_309763_) {
        this.f_302491_ = p_309522_;
        this.f_303355_ = p_309763_;
    }

    @Override
    public void m_305380_(ExecutionContext<T> p_312137_, Frame p_311608_) {
        int i = p_311608_.f_303315_() + 1;
        Frame frame = new Frame(i, this.f_303355_, p_312137_.m_306722_(i));
        this.f_302491_.accept(ExecutionControl.m_307232_(p_312137_, frame));
    }
}