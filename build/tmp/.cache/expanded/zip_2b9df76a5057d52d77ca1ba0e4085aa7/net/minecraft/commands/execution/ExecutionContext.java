package net.minecraft.commands.execution;

import com.google.common.collect.Queues;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ExecutionContext<T> implements AutoCloseable {
    private static final int f_303554_ = 10000000;
    private static final Logger f_303754_ = LogUtils.getLogger();
    private final int f_303134_;
    private final int f_303338_;
    private final ProfilerFiller f_303063_;
    @Nullable
    private TraceCallbacks f_303303_;
    private int f_303772_;
    private boolean f_303790_;
    private final Deque<CommandQueueEntry<T>> f_303117_ = Queues.newArrayDeque();
    private final List<CommandQueueEntry<T>> f_302263_ = new ObjectArrayList<>();
    private int f_302338_;

    public ExecutionContext(int p_313193_, int p_311309_, ProfilerFiller p_309602_) {
        this.f_303134_ = p_313193_;
        this.f_303338_ = p_311309_;
        this.f_303063_ = p_309602_;
        this.f_303772_ = p_313193_;
    }

    private static <T extends ExecutionCommandSource<T>> Frame m_307370_(ExecutionContext<T> p_310887_, CommandResultCallback p_311060_) {
        if (p_310887_.f_302338_ == 0) {
            return new Frame(0, p_311060_, p_310887_.f_303117_::clear);
        } else {
            int i = p_310887_.f_302338_ + 1;
            return new Frame(i, p_311060_, p_310887_.m_306722_(i));
        }
    }

    public static <T extends ExecutionCommandSource<T>> void m_307938_(
        ExecutionContext<T> p_311344_, InstantiatedFunction<T> p_309533_, T p_310187_, CommandResultCallback p_310874_
    ) {
        p_311344_.m_307907_(
            new CommandQueueEntry<>(m_307370_(p_311344_, p_310874_), new CallFunction<>(p_309533_, p_310187_.m_304794_(), false).m_307530_(p_310187_))
        );
    }

    public static <T extends ExecutionCommandSource<T>> void m_307315_(
        ExecutionContext<T> p_311278_, String p_310967_, ContextChain<T> p_311656_, T p_312145_, CommandResultCallback p_309674_
    ) {
        p_311278_.m_307907_(new CommandQueueEntry<>(m_307370_(p_311278_, p_309674_), new BuildContexts.TopLevel<>(p_310967_, p_311656_, p_312145_)));
    }

    private void m_305118_() {
        this.f_303790_ = true;
        this.f_302263_.clear();
        this.f_303117_.clear();
    }

    public void m_307907_(CommandQueueEntry<T> p_311113_) {
        if (this.f_302263_.size() + this.f_303117_.size() > 10000000) {
            this.m_305118_();
        }

        if (!this.f_303790_) {
            this.f_302263_.add(p_311113_);
        }
    }

    public void m_305632_(int p_313117_) {
        while (!this.f_303117_.isEmpty() && this.f_303117_.peek().f_303653_().f_303315_() >= p_313117_) {
            this.f_303117_.removeFirst();
        }
    }

    public Frame.FrameControl m_306722_(int p_311323_) {
        return () -> this.m_305632_(p_311323_);
    }

    public void m_304919_() {
        this.m_306974_();

        while (true) {
            if (this.f_303772_ <= 0) {
                f_303754_.info("Command execution stopped due to limit (executed {} commands)", this.f_303134_);
                break;
            }

            CommandQueueEntry<T> commandqueueentry = this.f_303117_.pollFirst();
            if (commandqueueentry == null) {
                return;
            }

            this.f_302338_ = commandqueueentry.f_303653_().f_303315_();
            commandqueueentry.m_305080_(this);
            if (this.f_303790_) {
                f_303754_.error("Command execution stopped due to command queue overflow (max {})", 10000000);
                break;
            }

            this.m_306974_();
        }

        this.f_302338_ = 0;
    }

    private void m_306974_() {
        for (int i = this.f_302263_.size() - 1; i >= 0; i--) {
            this.f_303117_.addFirst(this.f_302263_.get(i));
        }

        this.f_302263_.clear();
    }

    public void m_305996_(@Nullable TraceCallbacks p_309595_) {
        this.f_303303_ = p_309595_;
    }

    @Nullable
    public TraceCallbacks m_307286_() {
        return this.f_303303_;
    }

    public ProfilerFiller m_305697_() {
        return this.f_303063_;
    }

    public int m_306377_() {
        return this.f_303338_;
    }

    public void m_306457_() {
        this.f_303772_--;
    }

    @Override
    public void close() {
        if (this.f_303303_ != null) {
            this.f_303303_.close();
        }
    }
}