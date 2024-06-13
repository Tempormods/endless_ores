package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerFunctionManager {
    private static final Logger f_302783_ = LogUtils.getLogger();
    private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
    private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
    private final MinecraftServer server;
    private List<CommandFunction<CommandSourceStack>> ticking = ImmutableList.of();
    private boolean postReload;
    private ServerFunctionLibrary library;

    public ServerFunctionManager(MinecraftServer pServer, ServerFunctionLibrary pLibrary) {
        this.server = pServer;
        this.library = pLibrary;
        this.postReload(pLibrary);
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }

    public void tick() {
        if (this.server.m_306290_().m_305915_()) {
            if (this.postReload) {
                this.postReload = false;
                Collection<CommandFunction<CommandSourceStack>> collection = this.library.getTag(LOAD_FUNCTION_TAG);
                this.executeTagFunctions(collection, LOAD_FUNCTION_TAG);
            }

            this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
        }
    }

    private void executeTagFunctions(Collection<CommandFunction<CommandSourceStack>> pFunctionObjects, ResourceLocation pIdentifier) {
        this.server.getProfiler().push(pIdentifier::toString);

        for (CommandFunction<CommandSourceStack> commandfunction : pFunctionObjects) {
            this.execute(commandfunction, this.getGameLoopSender());
        }

        this.server.getProfiler().pop();
    }

    public void execute(CommandFunction<CommandSourceStack> p_311911_, CommandSourceStack pSource) {
        ProfilerFiller profilerfiller = this.server.getProfiler();
        profilerfiller.push(() -> "function " + p_311911_.m_304900_());

        try {
            InstantiatedFunction<CommandSourceStack> instantiatedfunction = p_311911_.m_304684_(null, this.getDispatcher());
            Commands.m_306801_(pSource, p_311172_ -> ExecutionContext.m_307938_(p_311172_, instantiatedfunction, pSource, CommandResultCallback.f_302577_));
        } catch (FunctionInstantiationException functioninstantiationexception) {
        } catch (Exception exception) {
            f_302783_.warn("Failed to execute function {}", p_311911_.m_304900_(), exception);
        } finally {
            profilerfiller.pop();
        }
    }

    public void replaceLibrary(ServerFunctionLibrary pReloader) {
        this.library = pReloader;
        this.postReload(pReloader);
    }

    private void postReload(ServerFunctionLibrary pReloader) {
        this.ticking = ImmutableList.copyOf(pReloader.getTag(TICK_FUNCTION_TAG));
        this.postReload = true;
    }

    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }

    public Optional<CommandFunction<CommandSourceStack>> get(ResourceLocation pFunctionIdentifier) {
        return this.library.getFunction(pFunctionIdentifier);
    }

    public Collection<CommandFunction<CommandSourceStack>> getTag(ResourceLocation pFunctionTagIdentifier) {
        return this.library.getTag(pFunctionTagIdentifier);
    }

    public Iterable<ResourceLocation> getFunctionNames() {
        return this.library.getFunctions().keySet();
    }

    public Iterable<ResourceLocation> getTagNames() {
        return this.library.getAvailableTags();
    }
}