package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.execution.TraceCallbacks;

public interface ExecutionCommandSource<T extends ExecutionCommandSource<T>> {
    boolean hasPermission(int p_309473_);

    T withCallback(CommandResultCallback p_311254_);

    CommandResultCallback m_304794_();

    default T m_305986_() {
        return this.withCallback(CommandResultCallback.f_302577_);
    }

    CommandDispatcher<T> m_305649_();

    void m_305988_(CommandExceptionType p_311834_, Message p_310647_, boolean p_310226_, @Nullable TraceCallbacks p_312033_);

    boolean m_306225_();

    default void m_305442_(CommandSyntaxException p_311076_, boolean p_310707_, @Nullable TraceCallbacks p_311569_) {
        this.m_305988_(p_311076_.getType(), p_311076_.getRawMessage(), p_310707_, p_311569_);
    }

    static <T extends ExecutionCommandSource<T>> ResultConsumer<T> m_304809_() {
        return (p_310000_, p_311414_, p_311999_) -> p_310000_.getSource().m_304794_().m_306252_(p_311414_, p_311999_);
    }
}