package net.minecraft.commands.execution.tasks;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;

public class ExecuteCommand<T extends ExecutionCommandSource<T>> implements UnboundEntryAction<T> {
    private final String f_302576_;
    private final ChainModifiers f_302662_;
    private final CommandContext<T> f_303232_;

    public ExecuteCommand(String p_310766_, ChainModifiers p_309629_, CommandContext<T> p_310460_) {
        this.f_302576_ = p_310766_;
        this.f_302662_ = p_309629_;
        this.f_303232_ = p_310460_;
    }

    public void m_304778_(T p_310632_, ExecutionContext<T> p_310757_, Frame p_311301_) {
        p_310757_.m_305697_().push(() -> "execute " + this.f_302576_);

        try {
            p_310757_.m_306457_();
            int i = ContextChain.runExecutable(this.f_303232_, p_310632_, ExecutionCommandSource.m_304809_(), this.f_302662_.m_306471_());
            TraceCallbacks tracecallbacks = p_310757_.m_307286_();
            if (tracecallbacks != null) {
                tracecallbacks.m_180086_(p_311301_.f_303315_(), this.f_302576_, i);
            }
        } catch (CommandSyntaxException commandsyntaxexception) {
            p_310632_.m_305442_(commandsyntaxexception, this.f_302662_.m_306471_(), p_310757_.m_307286_());
        } finally {
            p_310757_.m_305697_().pop();
        }
    }
}