package net.minecraft.commands.execution.tasks;

import java.util.List;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class ContinuationTask<T, P> implements EntryAction<T> {
    private final ContinuationTask.TaskProvider<T, P> f_303579_;
    private final List<P> f_302234_;
    private final CommandQueueEntry<T> f_303150_;
    private int f_303329_;

    private ContinuationTask(ContinuationTask.TaskProvider<T, P> p_312248_, List<P> p_311891_, Frame p_311182_) {
        this.f_303579_ = p_312248_;
        this.f_302234_ = p_311891_;
        this.f_303150_ = new CommandQueueEntry<>(p_311182_, this);
    }

    @Override
    public void m_305380_(ExecutionContext<T> p_310507_, Frame p_311035_) {
        P p = this.f_302234_.get(this.f_303329_);
        p_310507_.m_307907_(this.f_303579_.m_306963_(p_311035_, p));
        if (++this.f_303329_ < this.f_302234_.size()) {
            p_310507_.m_307907_(this.f_303150_);
        }
    }

    public static <T, P> void m_304697_(ExecutionContext<T> p_311894_, Frame p_312100_, List<P> p_310159_, ContinuationTask.TaskProvider<T, P> p_309687_) {
        int i = p_310159_.size();
        switch (i) {
            case 0:
                break;
            case 1:
                p_311894_.m_307907_(p_309687_.m_306963_(p_312100_, p_310159_.get(0)));
                break;
            case 2:
                p_311894_.m_307907_(p_309687_.m_306963_(p_312100_, p_310159_.get(0)));
                p_311894_.m_307907_(p_309687_.m_306963_(p_312100_, p_310159_.get(1)));
                break;
            default:
                p_311894_.m_307907_((new ContinuationTask<>(p_309687_, p_310159_, p_312100_)).f_303150_);
        }
    }

    @FunctionalInterface
    public interface TaskProvider<T, P> {
        CommandQueueEntry<T> m_306963_(Frame p_312749_, P p_312271_);
    }
}