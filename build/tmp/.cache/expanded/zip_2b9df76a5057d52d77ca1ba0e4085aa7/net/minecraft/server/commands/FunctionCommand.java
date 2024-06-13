package net.minecraft.server.commands;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.execution.tasks.FallthroughTask;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;

public class FunctionCommand {
    private static final DynamicCommandExceptionType ERROR_ARGUMENT_NOT_COMPOUND = new DynamicCommandExceptionType(
        p_308741_ -> Component.m_307043_("commands.function.error.argument_not_compound", p_308741_)
    );
    static final DynamicCommandExceptionType f_302766_ = new DynamicCommandExceptionType(
        p_308742_ -> Component.m_307043_("commands.function.scheduled.no_functions", p_308742_)
    );
    @VisibleForTesting
    public static final Dynamic2CommandExceptionType f_302704_ = new Dynamic2CommandExceptionType(
        (p_308724_, p_308725_) -> Component.m_307043_("commands.function.instantiationFailure", p_308724_, p_308725_)
    );
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (p_137719_, p_137720_) -> {
        ServerFunctionManager serverfunctionmanager = p_137719_.getSource().getServer().getFunctions();
        SharedSuggestionProvider.suggestResource(serverfunctionmanager.getTagNames(), p_137720_, "#");
        return SharedSuggestionProvider.suggestResource(serverfunctionmanager.getFunctionNames(), p_137720_);
    };
    static final FunctionCommand.Callbacks<CommandSourceStack> f_302915_ = new FunctionCommand.Callbacks<CommandSourceStack>() {
        public void m_304864_(CommandSourceStack p_311645_, ResourceLocation p_312021_, int p_313021_) {
            p_311645_.sendSuccess(() -> Component.translatable("commands.function.result", Component.m_305236_(p_312021_), p_313021_), true);
        }
    };

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("with");

        for (DataCommands.DataProvider datacommands$dataprovider : DataCommands.SOURCE_PROVIDERS) {
            datacommands$dataprovider.wrap(literalargumentbuilder, p_308740_ -> p_308740_.executes(new FunctionCommand.FunctionCustomExecutor() {
                    @Override
                    protected CompoundTag m_304821_(CommandContext<CommandSourceStack> p_310697_) throws CommandSyntaxException {
                        return datacommands$dataprovider.access(p_310697_).getData();
                    }
                }).then(Commands.argument("path", NbtPathArgument.nbtPath()).executes(new FunctionCommand.FunctionCustomExecutor() {
                    @Override
                    protected CompoundTag m_304821_(CommandContext<CommandSourceStack> p_310275_) throws CommandSyntaxException {
                        return FunctionCommand.getArgumentTag(NbtPathArgument.getPath(p_310275_, "path"), datacommands$dataprovider.access(p_310275_));
                    }
                })));
        }

        pDispatcher.register(
            Commands.literal("function")
                .requires(p_137722_ -> p_137722_.hasPermission(2))
                .then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes(new FunctionCommand.FunctionCustomExecutor() {
                    @Nullable
                    @Override
                    protected CompoundTag m_304821_(CommandContext<CommandSourceStack> p_310980_) {
                        return null;
                    }
                }).then(Commands.argument("arguments", CompoundTagArgument.compoundTag()).executes(new FunctionCommand.FunctionCustomExecutor() {
                    @Override
                    protected CompoundTag m_304821_(CommandContext<CommandSourceStack> p_310980_) {
                        return CompoundTagArgument.getCompoundTag(p_310980_, "arguments");
                    }
                })).then(literalargumentbuilder))
        );
    }

    static CompoundTag getArgumentTag(NbtPathArgument.NbtPath pNbtPath, DataAccessor pDataAccessor) throws CommandSyntaxException {
        Tag tag = DataCommands.getSingleTag(pNbtPath, pDataAccessor);
        if (tag instanceof CompoundTag) {
            return (CompoundTag)tag;
        } else {
            throw ERROR_ARGUMENT_NOT_COMPOUND.create(tag.getType().getName());
        }
    }

    public static CommandSourceStack m_306912_(CommandSourceStack p_309881_) {
        return p_309881_.withSuppressedOutput().withMaximumPermission(2);
    }

    public static <T extends ExecutionCommandSource<T>> void m_306890_(
        Collection<CommandFunction<T>> p_311080_,
        @Nullable CompoundTag p_311435_,
        T p_310141_,
        T p_312402_,
        ExecutionControl<T> p_309669_,
        FunctionCommand.Callbacks<T> p_312300_,
        ChainModifiers p_312226_
    ) throws CommandSyntaxException {
        if (p_312226_.m_305036_()) {
            m_305345_(p_311080_, p_311435_, p_310141_, p_312402_, p_309669_, p_312300_);
        } else {
            m_305220_(p_311080_, p_311435_, p_310141_, p_312402_, p_309669_, p_312300_);
        }
    }

    private static <T extends ExecutionCommandSource<T>> void m_307223_(
        @Nullable CompoundTag p_312138_,
        ExecutionControl<T> p_309532_,
        CommandDispatcher<T> p_312204_,
        T p_311370_,
        CommandFunction<T> p_310160_,
        ResourceLocation p_311048_,
        CommandResultCallback p_312950_,
        boolean p_312453_
    ) throws CommandSyntaxException {
        try {
            InstantiatedFunction<T> instantiatedfunction = p_310160_.m_304684_(p_312138_, p_312204_);
            p_309532_.m_306270_(new CallFunction<>(instantiatedfunction, p_312950_, p_312453_).m_307530_(p_311370_));
        } catch (FunctionInstantiationException functioninstantiationexception) {
            throw f_302704_.create(p_311048_, functioninstantiationexception.messageComponent());
        }
    }

    private static <T extends ExecutionCommandSource<T>> CommandResultCallback m_305371_(
        T p_309693_, FunctionCommand.Callbacks<T> p_309991_, ResourceLocation p_312510_, CommandResultCallback p_312314_
    ) {
        return p_309693_.m_306225_() ? p_312314_ : (p_326268_, p_326269_) -> {
            p_309991_.m_304864_(p_309693_, p_312510_, p_326269_);
            p_312314_.m_306252_(p_326268_, p_326269_);
        };
    }

    private static <T extends ExecutionCommandSource<T>> void m_305345_(
        Collection<CommandFunction<T>> p_309905_,
        @Nullable CompoundTag p_312616_,
        T p_312541_,
        T p_310023_,
        ExecutionControl<T> p_312344_,
        FunctionCommand.Callbacks<T> p_309916_
    ) throws CommandSyntaxException {
        CommandDispatcher<T> commanddispatcher = p_312541_.m_305649_();
        T t = p_310023_.m_305986_();
        CommandResultCallback commandresultcallback = CommandResultCallback.m_304670_(p_312541_.m_304794_(), p_312344_.m_305000_().f_302691_());

        for (CommandFunction<T> commandfunction : p_309905_) {
            ResourceLocation resourcelocation = commandfunction.m_304900_();
            CommandResultCallback commandresultcallback1 = m_305371_(p_312541_, p_309916_, resourcelocation, commandresultcallback);
            m_307223_(p_312616_, p_312344_, commanddispatcher, t, commandfunction, resourcelocation, commandresultcallback1, true);
        }

        p_312344_.m_306270_(FallthroughTask.m_304796_());
    }

    private static <T extends ExecutionCommandSource<T>> void m_305220_(
        Collection<CommandFunction<T>> p_312947_,
        @Nullable CompoundTag p_311961_,
        T p_310755_,
        T p_312089_,
        ExecutionControl<T> p_310294_,
        FunctionCommand.Callbacks<T> p_311742_
    ) throws CommandSyntaxException {
        CommandDispatcher<T> commanddispatcher = p_310755_.m_305649_();
        T t = p_312089_.m_305986_();
        CommandResultCallback commandresultcallback = p_310755_.m_304794_();
        if (!p_312947_.isEmpty()) {
            if (p_312947_.size() == 1) {
                CommandFunction<T> commandfunction = p_312947_.iterator().next();
                ResourceLocation resourcelocation = commandfunction.m_304900_();
                CommandResultCallback commandresultcallback1 = m_305371_(p_310755_, p_311742_, resourcelocation, commandresultcallback);
                m_307223_(p_311961_, p_310294_, commanddispatcher, t, commandfunction, resourcelocation, commandresultcallback1, false);
            } else if (commandresultcallback == CommandResultCallback.f_302577_) {
                for (CommandFunction<T> commandfunction1 : p_312947_) {
                    ResourceLocation resourcelocation2 = commandfunction1.m_304900_();
                    CommandResultCallback commandresultcallback2 = m_305371_(p_310755_, p_311742_, resourcelocation2, commandresultcallback);
                    m_307223_(p_311961_, p_310294_, commanddispatcher, t, commandfunction1, resourcelocation2, commandresultcallback2, false);
                }
            } else {
                class Accumulator {
                    boolean f_302217_;
                    int f_303513_;

                    public void m_305259_(int p_310205_) {
                        this.f_302217_ = true;
                        this.f_303513_ += p_310205_;
                    }
                }

                Accumulator functioncommand$1accumulator = new Accumulator();
                CommandResultCallback commandresultcallback4 = (p_308727_, p_308728_) -> functioncommand$1accumulator.m_305259_(p_308728_);

                for (CommandFunction<T> commandfunction2 : p_312947_) {
                    ResourceLocation resourcelocation1 = commandfunction2.m_304900_();
                    CommandResultCallback commandresultcallback3 = m_305371_(p_310755_, p_311742_, resourcelocation1, commandresultcallback4);
                    m_307223_(p_311961_, p_310294_, commanddispatcher, t, commandfunction2, resourcelocation1, commandresultcallback3, false);
                }

                p_310294_.m_306270_((p_308731_, p_308732_) -> {
                    if (functioncommand$1accumulator.f_302217_) {
                        commandresultcallback.m_306612_(functioncommand$1accumulator.f_303513_);
                    }
                });
            }
        }
    }

    public interface Callbacks<T> {
        void m_304864_(T p_310906_, ResourceLocation p_310562_, int p_310733_);
    }

    abstract static class FunctionCustomExecutor
        extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack>
        implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {
        @Nullable
        protected abstract CompoundTag m_304821_(CommandContext<CommandSourceStack> p_311128_) throws CommandSyntaxException;

        public void m_305065_(
            CommandSourceStack p_310423_, ContextChain<CommandSourceStack> p_311781_, ChainModifiers p_313209_, ExecutionControl<CommandSourceStack> p_312609_
        ) throws CommandSyntaxException {
            CommandContext<CommandSourceStack> commandcontext = p_311781_.getTopContext().copyFor(p_310423_);
            Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> pair = FunctionArgument.m_306861_(commandcontext, "name");
            Collection<CommandFunction<CommandSourceStack>> collection = pair.getSecond();
            if (collection.isEmpty()) {
                throw FunctionCommand.f_302766_.create(Component.m_305236_(pair.getFirst()));
            } else {
                CompoundTag compoundtag = this.m_304821_(commandcontext);
                CommandSourceStack commandsourcestack = FunctionCommand.m_306912_(p_310423_);
                if (collection.size() == 1) {
                    p_310423_.sendSuccess(
                        () -> Component.translatable("commands.function.scheduled.single", Component.m_305236_(collection.iterator().next().m_304900_())), true
                    );
                } else {
                    p_310423_.sendSuccess(
                        () -> Component.translatable(
                                "commands.function.scheduled.multiple",
                                ComponentUtils.formatList(collection.stream().map(CommandFunction::m_304900_).toList(), Component::m_305236_)
                            ),
                        true
                    );
                }

                FunctionCommand.m_306890_(collection, compoundtag, p_310423_, commandsourcestack, p_312609_, FunctionCommand.f_302915_, p_313209_);
            }
        }
    }
}
