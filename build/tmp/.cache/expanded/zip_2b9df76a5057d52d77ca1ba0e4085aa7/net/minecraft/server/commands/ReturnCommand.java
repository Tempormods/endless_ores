package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.FallthroughTask;

public class ReturnCommand {
    public static <T extends ExecutionCommandSource<T>> void register(CommandDispatcher<T> pDispatcher) {
        pDispatcher.register(
            (LiteralArgumentBuilder<T>)LiteralArgumentBuilder.<ExecutionCommandSource<T>>literal("return")
                .requires(p_308799_ -> p_308799_.hasPermission(2))
                .then(
                    RequiredArgumentBuilder.<ExecutionCommandSource<T>, Integer>argument("value", IntegerArgumentType.integer())
                        .executes((com.mojang.brigadier.Command)new ReturnCommand.ReturnValueCustomExecutor<T>())
                )
                .then(LiteralArgumentBuilder.<T>literal("fail").executes(new ReturnCommand.ReturnFailCustomExecutor<>()))
                .then(LiteralArgumentBuilder.<T>literal("run").forward(pDispatcher.getRoot(), new ReturnCommand.ReturnFromCommandCustomModifier<>(), false))
        );
    }

    static class ReturnFailCustomExecutor<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor.CommandAdapter<T> {
        public void m_305328_(T p_312804_, ContextChain<T> p_313125_, ChainModifiers p_309843_, ExecutionControl<T> p_311523_) {
            p_312804_.m_304794_().m_307284_();
            Frame frame = p_311523_.m_305000_();
            frame.m_307468_();
            frame.m_304718_();
        }
    }

    static class ReturnFromCommandCustomModifier<T extends ExecutionCommandSource<T>> implements CustomModifierExecutor.ModifierAdapter<T> {
        public void m_306103_(T p_310700_, List<T> p_310930_, ContextChain<T> p_313059_, ChainModifiers p_313220_, ExecutionControl<T> p_311638_) {
            if (p_310930_.isEmpty()) {
                if (p_313220_.m_305036_()) {
                    p_311638_.m_306270_(FallthroughTask.m_304796_());
                }
            } else {
                p_311638_.m_305000_().m_304718_();
                ContextChain<T> contextchain = p_313059_.nextStage();
                String s = contextchain.getTopContext().getInput();
                p_311638_.m_306270_(new BuildContexts.Continuation<>(s, contextchain, p_313220_.m_306088_(), p_310700_, p_310930_));
            }
        }
    }

    static class ReturnValueCustomExecutor<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor.CommandAdapter<T> {
        public void m_305328_(T p_309785_, ContextChain<T> p_312976_, ChainModifiers p_309726_, ExecutionControl<T> p_310375_) {
            int i = IntegerArgumentType.getInteger(p_312976_.getTopContext(), "value");
            p_309785_.m_304794_().m_306612_(i);
            Frame frame = p_310375_.m_305000_();
            frame.m_305055_(i);
            frame.m_304718_();
        }
    }
}