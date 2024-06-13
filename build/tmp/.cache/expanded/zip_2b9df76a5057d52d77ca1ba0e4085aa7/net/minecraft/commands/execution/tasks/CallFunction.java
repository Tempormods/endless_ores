package net.minecraft.commands.execution.tasks;

import java.util.List;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.InstantiatedFunction;

public class CallFunction<T extends ExecutionCommandSource<T>> implements UnboundEntryAction<T> {
    private final InstantiatedFunction<T> f_302292_;
    private final CommandResultCallback f_302190_;
    private final boolean f_303519_;

    public CallFunction(InstantiatedFunction<T> p_311175_, CommandResultCallback p_310950_, boolean p_309425_) {
        this.f_302292_ = p_311175_;
        this.f_302190_ = p_310950_;
        this.f_303519_ = p_309425_;
    }

    public void m_304778_(T p_312557_, ExecutionContext<T> p_312618_, Frame p_310825_) {
        p_312618_.m_306457_();
        List<UnboundEntryAction<T>> list = this.f_302292_.m_306124_();
        TraceCallbacks tracecallbacks = p_312618_.m_307286_();
        if (tracecallbacks != null) {
            tracecallbacks.m_180090_(p_310825_.f_303315_(), this.f_302292_.m_304900_(), this.f_302292_.m_306124_().size());
        }

        int i = p_310825_.f_303315_() + 1;
        Frame.FrameControl frame$framecontrol = this.f_303519_ ? p_310825_.f_303027_() : p_312618_.m_306722_(i);
        Frame frame = new Frame(i, this.f_302190_, frame$framecontrol);
        ContinuationTask.m_304697_(p_312618_, frame, list, (p_310328_, p_313182_) -> new CommandQueueEntry<>(p_310328_, p_313182_.m_307530_(p_312557_)));
    }
}