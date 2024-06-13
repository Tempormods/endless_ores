package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DebugCommand {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.alreadyRunning"));
    static final SimpleCommandExceptionType f_303735_ = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noRecursion"));
    static final SimpleCommandExceptionType f_302589_ = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noReturnRun"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(
            Commands.literal("debug")
                .requires(p_180073_ -> p_180073_.hasPermission(3))
                .then(Commands.literal("start").executes(p_180069_ -> start(p_180069_.getSource())))
                .then(Commands.literal("stop").executes(p_136918_ -> stop(p_136918_.getSource())))
                .then(
                    Commands.literal("function")
                        .requires(p_180071_ -> p_180071_.hasPermission(3))
                        .then(
                            Commands.argument("name", FunctionArgument.functions())
                                .suggests(FunctionCommand.SUGGEST_FUNCTION)
                                .executes(new DebugCommand.TraceCustomExecutor())
                        )
                )
        );
    }

    private static int start(CommandSourceStack pSource) throws CommandSyntaxException {
        MinecraftServer minecraftserver = pSource.getServer();
        if (minecraftserver.isTimeProfilerRunning()) {
            throw ERROR_ALREADY_RUNNING.create();
        } else {
            minecraftserver.startTimeProfiler();
            pSource.sendSuccess(() -> Component.translatable("commands.debug.started"), true);
            return 0;
        }
    }

    private static int stop(CommandSourceStack pSource) throws CommandSyntaxException {
        MinecraftServer minecraftserver = pSource.getServer();
        if (!minecraftserver.isTimeProfilerRunning()) {
            throw ERROR_NOT_RUNNING.create();
        } else {
            ProfileResults profileresults = minecraftserver.stopTimeProfiler();
            double d0 = (double)profileresults.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
            double d1 = (double)profileresults.getTickDuration() / d0;
            pSource.sendSuccess(
                () -> Component.translatable(
                        "commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d0), profileresults.getTickDuration(), String.format(Locale.ROOT, "%.2f", d1)
                    ),
                true
            );
            return (int)d1;
        }
    }

    static class TraceCustomExecutor
        extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack>
        implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {
        public void m_305065_(
            CommandSourceStack p_309819_, ContextChain<CommandSourceStack> p_311173_, ChainModifiers p_312111_, ExecutionControl<CommandSourceStack> p_311988_
        ) throws CommandSyntaxException {
            if (p_312111_.m_305036_()) {
                throw DebugCommand.f_302589_.create();
            } else if (p_311988_.m_305414_() != null) {
                throw DebugCommand.f_303735_.create();
            } else {
                CommandContext<CommandSourceStack> commandcontext = p_311173_.getTopContext();
                Collection<CommandFunction<CommandSourceStack>> collection = FunctionArgument.getFunctions(commandcontext, "name");
                MinecraftServer minecraftserver = p_309819_.getServer();
                String s = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";
                CommandDispatcher<CommandSourceStack> commanddispatcher = p_309819_.getServer().getFunctions().getDispatcher();
                int i = 0;

                try {
                    Path path = minecraftserver.getFile("debug").toPath();
                    Files.createDirectories(path);
                    final PrintWriter printwriter = new PrintWriter(Files.newBufferedWriter(path.resolve(s), StandardCharsets.UTF_8));
                    DebugCommand.Tracer debugcommand$tracer = new DebugCommand.Tracer(printwriter);
                    p_311988_.m_304947_(debugcommand$tracer);

                    for (final CommandFunction<CommandSourceStack> commandfunction : collection) {
                        try {
                            CommandSourceStack commandsourcestack = p_309819_.withSource(debugcommand$tracer).withMaximumPermission(2);
                            InstantiatedFunction<CommandSourceStack> instantiatedfunction = commandfunction.m_304684_(null, commanddispatcher);
                            p_311988_.m_306270_((new CallFunction<CommandSourceStack>(instantiatedfunction, CommandResultCallback.f_302577_, false) {
                                public void m_304778_(CommandSourceStack p_310186_, ExecutionContext<CommandSourceStack> p_311250_, Frame p_311262_) {
                                    printwriter.println(commandfunction.m_304900_());
                                    super.m_304778_(p_310186_, p_311250_, p_311262_);
                                }
                            }).m_307530_(commandsourcestack));
                            i += instantiatedfunction.m_306124_().size();
                        } catch (FunctionInstantiationException functioninstantiationexception) {
                            p_309819_.sendFailure(functioninstantiationexception.messageComponent());
                        }
                    }
                } catch (IOException | UncheckedIOException uncheckedioexception) {
                    DebugCommand.LOGGER.warn("Tracing failed", (Throwable)uncheckedioexception);
                    p_309819_.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
                }

                int j = i;
                p_311988_.m_306270_(
                    (p_311688_, p_310332_) -> {
                        if (collection.size() == 1) {
                            p_309819_.sendSuccess(
                                () -> Component.translatable(
                                        "commands.debug.function.success.single", j, Component.m_305236_(collection.iterator().next().m_304900_()), s
                                    ),
                                true
                            );
                        } else {
                            p_309819_.sendSuccess(() -> Component.translatable("commands.debug.function.success.multiple", j, collection.size(), s), true);
                        }
                    }
                );
            }
        }
    }

    static class Tracer implements CommandSource, TraceCallbacks {
        public static final int INDENT_OFFSET = 1;
        private final PrintWriter output;
        private int lastIndent;
        private boolean waitingForResult;

        Tracer(PrintWriter pOutput) {
            this.output = pOutput;
        }

        private void indentAndSave(int pIndent) {
            this.printIndent(pIndent);
            this.lastIndent = pIndent;
        }

        private void printIndent(int pIndent) {
            for (int i = 0; i < pIndent + 1; i++) {
                this.output.write("    ");
            }
        }

        private void newLine() {
            if (this.waitingForResult) {
                this.output.println();
                this.waitingForResult = false;
            }
        }

        @Override
        public void m_180083_(int pIndent, String pCommand) {
            this.newLine();
            this.indentAndSave(pIndent);
            this.output.print("[C] ");
            this.output.print(pCommand);
            this.waitingForResult = true;
        }

        @Override
        public void m_180086_(int pIndent, String pReturnValue, int pCommand) {
            if (this.waitingForResult) {
                this.output.print(" -> ");
                this.output.println(pCommand);
                this.waitingForResult = false;
            } else {
                this.indentAndSave(pIndent);
                this.output.print("[R = ");
                this.output.print(pCommand);
                this.output.print("] ");
                this.output.println(pReturnValue);
            }
        }

        @Override
        public void m_180090_(int pIndent, ResourceLocation pCommand, int pSize) {
            this.newLine();
            this.indentAndSave(pIndent);
            this.output.print("[F] ");
            this.output.print(pCommand);
            this.output.print(" size=");
            this.output.println(pSize);
        }

        @Override
        public void m_180099_(String pCommand) {
            this.newLine();
            this.indentAndSave(this.lastIndent + 1);
            this.output.print("[E] ");
            this.output.print(pCommand);
        }

        @Override
        public void sendSystemMessage(Component pComponent) {
            this.newLine();
            this.printIndent(this.lastIndent + 1);
            this.output.print("[M] ");
            this.output.println(pComponent.getString());
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }

        @Override
        public boolean alwaysAccepts() {
            return true;
        }

        @Override
        public void close() {
            IOUtils.closeQuietly((Writer)this.output);
        }
    }
}