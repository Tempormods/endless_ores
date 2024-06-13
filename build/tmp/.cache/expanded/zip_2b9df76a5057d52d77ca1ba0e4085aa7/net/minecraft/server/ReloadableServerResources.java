package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.slf4j.Logger;

public class ReloadableServerResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableServerRegistries.Holder f_316081_;
    private final ReloadableServerResources.ConfigurableRegistryLookup f_314787_;
    private final Commands commands;
    private final RecipeManager recipes;
    private final TagManager tagManager;
    private final ServerAdvancementManager advancements;
    private final ServerFunctionLibrary functionLibrary;
    private final net.minecraftforge.common.crafting.conditions.ICondition.IContext context;

    private ReloadableServerResources(RegistryAccess.Frozen pRegistryAccess, FeatureFlagSet pEnabledFeatures, Commands.CommandSelection pCommandSelection, int pFunctionCompilationLevel) {
        this.f_316081_ = new ReloadableServerRegistries.Holder(pRegistryAccess);
        this.f_314787_ = new ReloadableServerResources.ConfigurableRegistryLookup(pRegistryAccess);
        this.f_314787_.m_322619_(ReloadableServerResources.MissingTagAccessPolicy.CREATE_NEW);
        this.tagManager = new TagManager(pRegistryAccess);
        this.commands = new Commands(pCommandSelection, CommandBuildContext.simple(this.f_314787_, pEnabledFeatures));
        // Forge: Create context object and pass it to the recipe manager.
        this.context = new net.minecraftforge.common.crafting.conditions.ConditionContext(this.tagManager);
        this.recipes = new RecipeManager(this.f_314787_, this.context);
        this.advancements = new ServerAdvancementManager(this.f_314787_, this.context);
        this.functionLibrary = new ServerFunctionLibrary(pFunctionCompilationLevel, this.commands.getDispatcher());
    }

    public ServerFunctionLibrary getFunctionLibrary() {
        return this.functionLibrary;
    }

    public ReloadableServerRegistries.Holder m_322920_() {
        return this.f_316081_;
    }

    public RecipeManager getRecipeManager() {
        return this.recipes;
    }

    public Commands getCommands() {
        return this.commands;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.advancements;
    }

    public List<PreparableReloadListener> listeners() {
        return List.of(this.tagManager, this.recipes, this.functionLibrary, this.advancements);
    }

    public static CompletableFuture<ReloadableServerResources> loadResources(
        ResourceManager pResourceManager,
        LayeredRegistryAccess<RegistryLayer> p_330376_,
        FeatureFlagSet pEnabledFeatures,
        Commands.CommandSelection pCommandSelection,
        int pFunctionCompilationLevel,
        Executor pBackgroundExecutor,
        Executor pGameExecutor
    ) {
        return ReloadableServerRegistries.m_319076_(p_330376_, pResourceManager, pBackgroundExecutor)
            .thenCompose(
                p_326196_ -> {
                    ReloadableServerResources reloadableserverresources = new ReloadableServerResources(p_326196_.compositeAccess(), pEnabledFeatures, pCommandSelection, pFunctionCompilationLevel);
                    var listeners = new java.util.ArrayList<>(reloadableserverresources.listeners());
                    listeners.addAll(net.minecraftforge.event.ForgeEventFactory.onResourceReload(reloadableserverresources, p_330376_.compositeAccess()));
                    return SimpleReloadInstance.create(
                            pResourceManager, listeners, pBackgroundExecutor, pGameExecutor, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled()
                        )
                        .done()
                        .whenComplete(
                            (p_326199_, p_326200_) -> reloadableserverresources.f_314787_.m_322619_(ReloadableServerResources.MissingTagAccessPolicy.FAIL)
                        )
                        .thenApply(p_214306_ -> reloadableserverresources);
                }
            );
    }

    public void updateRegistryTags() {
        this.tagManager.getResult().forEach(p_326197_ -> updateRegistryTags(this.f_316081_.m_323327_(), (TagManager.LoadResult<?>)p_326197_));
        AbstractFurnaceBlockEntity.m_323223_();
        Blocks.rebuildCache();
        net.minecraftforge.event.ForgeEventFactory.onTagsUpdated(this.f_316081_.m_323327_(), false, false);
    }

    private static <T> void updateRegistryTags(RegistryAccess pRegistryAccess, TagManager.LoadResult<T> pLoadResult) {
        ResourceKey<? extends Registry<T>> resourcekey = pLoadResult.key();
        Map<TagKey<T>, List<Holder<T>>> map = pLoadResult.tags()
            .entrySet()
            .stream()
            .collect(
                Collectors.toUnmodifiableMap(p_214303_ -> TagKey.create(resourcekey, p_214303_.getKey()), p_214312_ -> List.copyOf(p_214312_.getValue()))
            );
        pRegistryAccess.registryOrThrow(resourcekey).bindTags(map);
    }

    /**
     * Exposes the current condition context for usage in other reload listeners.<br>
     * This is not useful outside the reloading stage.
     * @return The condition context for the currently active reload.
     */
    public net.minecraftforge.common.crafting.conditions.ICondition.IContext getConditionContext() {
        return this.context;
    }

    static class ConfigurableRegistryLookup implements HolderLookup.Provider {
        private final RegistryAccess f_316437_;
        ReloadableServerResources.MissingTagAccessPolicy f_316104_ = ReloadableServerResources.MissingTagAccessPolicy.FAIL;

        ConfigurableRegistryLookup(RegistryAccess p_330205_) {
            this.f_316437_ = p_330205_;
        }

        public void m_322619_(ReloadableServerResources.MissingTagAccessPolicy p_328471_) {
            this.f_316104_ = p_328471_;
        }

        @Override
        public Stream<ResourceKey<? extends Registry<?>>> m_305097_() {
            return this.f_316437_.m_305097_();
        }

        @Override
        public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_335510_) {
            return this.f_316437_.registry(p_335510_).map(p_335756_ -> this.m_322228_(p_335756_.asLookup(), p_335756_.asTagAddingLookup()));
        }

        private <T> HolderLookup.RegistryLookup<T> m_322228_(final HolderLookup.RegistryLookup<T> p_335281_, final HolderLookup.RegistryLookup<T> p_329763_) {
            return new HolderLookup.RegistryLookup.Delegate<T>() {
                @Override
                public HolderLookup.RegistryLookup<T> parent() {
                    return switch (ConfigurableRegistryLookup.this.f_316104_) {
                        case CREATE_NEW -> p_329763_;
                        case FAIL -> p_335281_;
                    };
                }
            };
        }
    }

    static enum MissingTagAccessPolicy {
        CREATE_NEW,
        FAIL;
    }
}
