package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final HolderLookup.Provider f_314854_;
    private Multimap<RecipeType<?>, RecipeHolder<?>> f_316587_ = ImmutableMultimap.of();
    private Map<ResourceLocation, RecipeHolder<?>> byName = ImmutableMap.of();
    private boolean hasErrors;
    private final net.minecraftforge.common.crafting.conditions.ICondition.IContext context; //Forge: add context

    /** @deprecated Forge: use {@linkplain RecipeManager#RecipeManager(net.minecraftforge.common.crafting.conditions.ICondition.IContext) constructor with context}. */
    public RecipeManager(HolderLookup.Provider p_330459_) {
        this(p_330459_, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
    }

    public RecipeManager(HolderLookup.Provider p_330459_, net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
        super(GSON, "recipes");
        this.f_314854_ = p_330459_;
        this.context = context;
    }

    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.hasErrors = false;
        Builder<RecipeType<?>, RecipeHolder<?>> builder = ImmutableMultimap.builder();
        com.google.common.collect.ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> builder1 = ImmutableMap.builder();
        RegistryOps<JsonElement> registryops = this.f_314854_.m_318927_(JsonOps.INSTANCE)
            .withContext(net.minecraftforge.common.crafting.conditions.ICondition.IContext.KEY, this.context);

        for (Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.

            try {
                if (entry.getValue().isJsonObject() && !net.minecraftforge.common.ForgeHooks.readAndTestCondition(registryops, entry.getValue().getAsJsonObject())) {
                    LOGGER.debug("Skipping loading recipe {} as it's conditions were not met", resourcelocation);
                    continue;
                }
                Recipe<?> recipe = Recipe.f_302387_.parse(registryops, entry.getValue()).getOrThrow(JsonParseException::new);
                RecipeHolder<?> recipeholder = new RecipeHolder<>(resourcelocation, recipe);
                builder.put(recipe.getType(), recipeholder);
                builder1.put(resourcelocation, recipeholder);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading recipe {}", resourcelocation, jsonparseexception);
            }
        }

        this.f_316587_ = builder.build();
        this.byName = builder1.build();
        LOGGER.info("Loaded {} recipes", this.f_316587_.size());
    }

    public boolean hadErrorsLoading() {
        return this.hasErrors;
    }

    public <C extends Container, T extends Recipe<C>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> pRecipeType, C pInventory, Level pLevel) {
        return this.byType(pRecipeType).stream().filter(p_296918_ -> p_296918_.value().matches(pInventory, pLevel)).findFirst();
    }

    public <C extends Container, T extends Recipe<C>> Optional<RecipeHolder<T>> getRecipeFor(
        RecipeType<T> pRecipeType, C pInventory, Level pLevel, @Nullable ResourceLocation pLastRecipe
    ) {
        if (pLastRecipe != null) {
            RecipeHolder<T> recipeholder = this.m_320711_(pRecipeType, pLastRecipe);
            if (recipeholder != null && recipeholder.value().matches(pInventory, pLevel)) {
                return Optional.of(recipeholder);
            }
        }

        return this.byType(pRecipeType).stream().filter(p_296912_ -> p_296912_.value().matches(pInventory, pLevel)).findFirst();
    }

    public <C extends Container, T extends Recipe<C>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> pRecipeType) {
        return List.copyOf(this.byType(pRecipeType));
    }

    public <C extends Container, T extends Recipe<C>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> pRecipeType, C pInventory, Level pLevel) {
        return this.byType(pRecipeType)
            .stream()
            .filter(p_327199_ -> p_327199_.value().matches(pInventory, pLevel))
            .sorted(Comparator.comparing(p_327196_ -> p_327196_.value().getResultItem(pLevel.registryAccess()).getDescriptionId()))
            .collect(Collectors.toList());
    }

    private <C extends Container, T extends Recipe<C>> Collection<RecipeHolder<T>> byType(RecipeType<T> pRecipeType) {
        return (Collection)this.f_316587_.get(pRecipeType);
    }

    public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> pRecipeType, C pInventory, Level pLevel) {
        Optional<RecipeHolder<T>> optional = this.getRecipeFor(pRecipeType, pInventory, pLevel);
        if (optional.isPresent()) {
            return optional.get().value().getRemainingItems(pInventory);
        } else {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInventory.getContainerSize(), ItemStack.EMPTY);

            for (int i = 0; i < nonnulllist.size(); i++) {
                nonnulllist.set(i, pInventory.getItem(i));
            }

            return nonnulllist;
        }
    }

    public Optional<RecipeHolder<?>> byKey(ResourceLocation pRecipeId) {
        return Optional.ofNullable(this.byName.get(pRecipeId));
    }

    @Nullable
    private <T extends Recipe<?>> RecipeHolder<T> m_320711_(RecipeType<T> p_332930_, ResourceLocation p_335282_) {
        RecipeHolder<?> recipeholder = this.byName.get(p_335282_);
        return (RecipeHolder<T>)(recipeholder != null && recipeholder.value().getType().equals(p_332930_) ? recipeholder : null);
    }

    public Collection<RecipeHolder<?>> m_323867_() {
        return this.f_316587_.values();
    }

    public Collection<RecipeHolder<?>> getRecipes() {
        return this.byName.values();
    }

    public Stream<ResourceLocation> getRecipeIds() {
        return this.byName.keySet().stream();
    }

    @VisibleForTesting
    protected static RecipeHolder<?> fromJson(ResourceLocation pRecipeId, JsonObject pJson, HolderLookup.Provider p_328308_) {
        Recipe<?> recipe = Recipe.f_302387_.parse(p_328308_.m_318927_(JsonOps.INSTANCE), pJson).getOrThrow(JsonParseException::new);
        return new RecipeHolder<>(pRecipeId, recipe);
    }

    public void replaceRecipes(Iterable<RecipeHolder<?>> pRecipes) {
        this.hasErrors = false;
        Builder<RecipeType<?>, RecipeHolder<?>> builder = ImmutableMultimap.builder();
        com.google.common.collect.ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> builder1 = ImmutableMap.builder();

        for (RecipeHolder<?> recipeholder : pRecipes) {
            RecipeType<?> recipetype = recipeholder.value().getType();
            builder.put(recipetype, recipeholder);
            builder1.put(recipeholder.id(), recipeholder);
        }

        this.f_316587_ = builder.build();
        this.byName = builder1.build();
    }

    public static <C extends Container, T extends Recipe<C>> RecipeManager.CachedCheck<C, T> createCheck(final RecipeType<T> pRecipeType) {
        return new RecipeManager.CachedCheck<C, T>() {
            @Nullable
            private ResourceLocation lastRecipe;

            @Override
            public Optional<RecipeHolder<T>> getRecipeFor(C p_220278_, Level p_220279_) {
                RecipeManager recipemanager = p_220279_.getRecipeManager();
                Optional<RecipeHolder<T>> optional = recipemanager.getRecipeFor(pRecipeType, p_220278_, p_220279_, this.lastRecipe);
                if (optional.isPresent()) {
                    RecipeHolder<T> recipeholder = optional.get();
                    this.lastRecipe = recipeholder.id();
                    return Optional.of(recipeholder);
                } else {
                    return Optional.empty();
                }
            }
        };
    }

    public interface CachedCheck<C extends Container, T extends Recipe<C>> {
        Optional<RecipeHolder<T>> getRecipeFor(C pContainer, Level pLevel);
    }
}
