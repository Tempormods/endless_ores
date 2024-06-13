package net.minecraft.commands.execution.tasks;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.network.chat.Component;

public class BuildContexts<T extends ExecutionCommandSource<T>> {
    @VisibleForTesting
    public static final DynamicCommandExceptionType f_302201_ = new DynamicCommandExceptionType(
        p_311924_ -> Component.m_307043_("command.forkLimit", p_311924_)
    );
    private final String f_303818_;
    private final ContextChain<T> f_302616_;

    public BuildContexts(String p_310420_, ContextChain<T> p_313082_) {
        this.f_303818_ = p_310420_;
        this.f_302616_ = p_313082_;
    }

    protected void m_305670_(T p_309755_, List<T> p_310231_, ExecutionContext<T> p_311779_, Frame p_313162_, ChainModifiers p_310618_) {
        ContextChain<T> contextchain = this.f_302616_;
        ChainModifiers chainmodifiers = p_310618_;
        List<T> list = p_310231_;
        if (contextchain.getStage() != Stage.EXECUTE) {
            p_311779_.m_305697_().push(() -> "prepare " + this.f_303818_);

            try {
                for (int i = p_311779_.m_306377_(); contextchain.getStage() != Stage.EXECUTE; contextchain = contextchain.nextStage()) {
                    CommandContext<T> commandcontext = contextchain.getTopContext();
                    if (commandcontext.isForked()) {
                        chainmodifiers = chainmodifiers.m_305062_();
                    }

                    RedirectModifier<T> redirectmodifier = commandcontext.getRedirectModifier();
                    if (redirectmodifier instanceof CustomModifierExecutor custommodifierexecutor) {
                        custommodifierexecutor.m_306103_(p_309755_, list, contextchain, chainmodifiers, ExecutionControl.m_307232_(p_311779_, p_313162_));
                        return;
                    }

                    if (redirectmodifier != null) {
                        p_311779_.m_306457_();
                        boolean flag = chainmodifiers.m_306471_();
                        List<T> list1 = new ObjectArrayList<>();

                        for (T t : list) {
                            try {
                                Collection<T> collection = ContextChain.runModifier(commandcontext, t, (p_311026_, p_312291_, p_310245_) -> {
                                }, flag);
                                if (list1.size() + collection.size() >= i) {
                                    p_309755_.m_305442_(f_302201_.create(i), flag, p_311779_.m_307286_());
                                    return;
                                }

                                list1.addAll(collection);
                            } catch (CommandSyntaxException commandsyntaxexception) {
                                t.m_305442_(commandsyntaxexception, flag, p_311779_.m_307286_());
                                if (!flag) {
                                    return;
                                }
                            }
                        }

                        list = list1;
                    }
                }
            } finally {
                p_311779_.m_305697_().pop();
            }
        }

        if (list.isEmpty()) {
            if (chainmodifiers.m_305036_()) {
                p_311779_.m_307907_(new CommandQueueEntry<T>(p_313162_, FallthroughTask.m_304796_()));
            }
        } else {
            CommandContext<T> commandcontext1 = contextchain.getTopContext();
            if (commandcontext1.getCommand() instanceof CustomCommandExecutor customcommandexecutor) {
                ExecutionControl<T> executioncontrol = ExecutionControl.m_307232_(p_311779_, p_313162_);

                for (T t2 : list) {
                    customcommandexecutor.m_305328_(t2, contextchain, chainmodifiers, executioncontrol);
                }
            } else {
                if (chainmodifiers.m_305036_()) {
                    T t1 = list.get(0);
                    t1 = t1.withCallback(CommandResultCallback.m_304670_(t1.m_304794_(), p_313162_.f_302691_()));
                    list = List.of(t1);
                }

                ExecuteCommand<T> executecommand = new ExecuteCommand<>(this.f_303818_, chainmodifiers, commandcontext1);
                ContinuationTask.m_304697_(
                    p_311779_, p_313162_, list, (p_311832_, p_309437_) -> new CommandQueueEntry<>(p_311832_, executecommand.m_307530_(p_309437_))
                );
            }
        }
    }

    protected void m_307886_(ExecutionContext<T> p_311913_, Frame p_312311_) {
        TraceCallbacks tracecallbacks = p_311913_.m_307286_();
        if (tracecallbacks != null) {
            tracecallbacks.m_180083_(p_312311_.f_303315_(), this.f_303818_);
        }
    }

    @Override
    public String toString() {
        return this.f_303818_;
    }

    public static class Continuation<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements EntryAction<T> {
        private final ChainModifiers f_302349_;
        private final T f_303654_;
        private final List<T> f_303156_;

        public Continuation(String p_312336_, ContextChain<T> p_312118_, ChainModifiers p_311446_, T p_312390_, List<T> p_311252_) {
            super(p_312336_, p_312118_);
            this.f_303654_ = p_312390_;
            this.f_303156_ = p_311252_;
            this.f_302349_ = p_311446_;
        }

        @Override
        public void m_305380_(ExecutionContext<T> p_310784_, Frame p_310508_) {
            this.m_305670_(this.f_303654_, this.f_303156_, p_310784_, p_310508_, this.f_302349_);
        }
    }

    public static class TopLevel<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements EntryAction<T> {
        private final T f_302189_;

        public TopLevel(String p_312552_, ContextChain<T> p_309758_, T p_313175_) {
            super(p_312552_, p_309758_);
            this.f_302189_ = p_313175_;
        }

        @Override
        public void m_305380_(ExecutionContext<T> p_310161_, Frame p_311746_) {
            this.m_307886_(p_310161_, p_311746_);
            this.m_305670_(this.f_302189_, List.of(this.f_302189_), p_310161_, p_311746_, ChainModifiers.f_302277_);
        }
    }

    public static class Unbound<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements UnboundEntryAction<T> {
        public Unbound(String p_312191_, ContextChain<T> p_309892_) {
            super(p_312191_, p_309892_);
        }

        public void m_304778_(T p_310320_, ExecutionContext<T> p_313071_, Frame p_310123_) {
            this.m_307886_(p_313071_, p_310123_);
            this.m_305670_(p_310320_, List.of(p_310320_), p_313071_, p_310123_, ChainModifiers.f_302277_);
        }
    }
}