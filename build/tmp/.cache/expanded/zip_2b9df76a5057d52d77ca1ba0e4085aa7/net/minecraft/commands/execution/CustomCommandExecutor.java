package net.minecraft.commands.execution;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.ExecutionCommandSource;

public interface CustomCommandExecutor<T> {
    void m_305328_(T p_310884_, ContextChain<T> p_312906_, ChainModifiers p_310837_, ExecutionControl<T> p_310586_);

    public interface CommandAdapter<T> extends Command<T>, CustomCommandExecutor<T> {
        @Override
        default int run(CommandContext<T> p_309955_) throws CommandSyntaxException {
            throw new UnsupportedOperationException("This function should not run");
        }
    }

    public abstract static class WithErrorHandling<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor<T> {
        public final void m_305328_(T p_310241_, ContextChain<T> p_311766_, ChainModifiers p_310779_, ExecutionControl<T> p_309382_) {
            try {
                this.m_305065_(p_310241_, p_311766_, p_310779_, p_309382_);
            } catch (CommandSyntaxException commandsyntaxexception) {
                this.m_305483_(commandsyntaxexception, p_310241_, p_310779_, p_309382_.m_305414_());
                p_310241_.m_304794_().m_307284_();
            }
        }

        protected void m_305483_(CommandSyntaxException p_313040_, T p_312743_, ChainModifiers p_309642_, @Nullable TraceCallbacks p_309545_) {
            p_312743_.m_305442_(p_313040_, p_309642_.m_306471_(), p_309545_);
        }

        protected abstract void m_305065_(T p_311664_, ContextChain<T> p_312225_, ChainModifiers p_309888_, ExecutionControl<T> p_313051_) throws CommandSyntaxException;
    }
}