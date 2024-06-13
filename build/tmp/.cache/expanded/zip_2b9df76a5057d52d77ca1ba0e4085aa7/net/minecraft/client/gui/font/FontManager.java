package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontManager implements PreparableReloadListener, AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_PATH = "fonts.json";
    public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
    private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final FontSet missingFontSet;
    private final List<GlyphProvider> providersToClose = new ArrayList<>();
    private final Map<ResourceLocation, FontSet> fontSets = new HashMap<>();
    private final TextureManager textureManager;
    @Nullable
    private volatile FontSet f_316373_;

    public FontManager(TextureManager pTextureManager) {
        this.textureManager = pTextureManager;
        this.missingFontSet = Util.make(new FontSet(pTextureManager, MISSING_FONT), p_325351_ -> p_325351_.m_321905_(List.of(m_322949_()), Set.of()));
    }

    private static GlyphProvider.Conditional m_322949_() {
        return new GlyphProvider.Conditional(new AllMissingGlyphProvider(), FontOption.Filter.f_315854_);
    }

    @Override
    public CompletableFuture<Void> reload(
        PreparableReloadListener.PreparationBarrier pPreparationBarrier,
        ResourceManager pResourceManager,
        ProfilerFiller pPreparationsProfiler,
        ProfilerFiller pReloadProfiler,
        Executor pBackgroundExecutor,
        Executor pGameExecutor
    ) {
        pPreparationsProfiler.startTick();
        pPreparationsProfiler.endTick();
        return this.prepare(pResourceManager, pBackgroundExecutor)
            .thenCompose(pPreparationBarrier::wait)
            .thenAcceptAsync(p_284609_ -> this.apply(p_284609_, pReloadProfiler), pGameExecutor);
    }

    private CompletableFuture<FontManager.Preparation> prepare(ResourceManager pResourceManager, Executor pExecutor) {
        List<CompletableFuture<FontManager.UnresolvedBuilderBundle>> list = new ArrayList<>();

        for (Entry<ResourceLocation, List<Resource>> entry : FONT_DEFINITIONS.listMatchingResourceStacks(pResourceManager).entrySet()) {
            ResourceLocation resourcelocation = FONT_DEFINITIONS.fileToId(entry.getKey());
            list.add(CompletableFuture.supplyAsync(() -> {
                List<Pair<FontManager.BuilderId, GlyphProviderDefinition.Conditional>> list1 = loadResourceStack(entry.getValue(), resourcelocation);
                FontManager.UnresolvedBuilderBundle fontmanager$unresolvedbuilderbundle = new FontManager.UnresolvedBuilderBundle(resourcelocation);

                for (Pair<FontManager.BuilderId, GlyphProviderDefinition.Conditional> pair : list1) {
                    FontManager.BuilderId fontmanager$builderid = pair.getFirst();
                    FontOption.Filter fontoption$filter = pair.getSecond().f_317087_();
                    pair.getSecond().f_315443_().unpack().ifLeft(p_325337_ -> {
                        CompletableFuture<Optional<GlyphProvider>> completablefuture = this.safeLoad(fontmanager$builderid, p_325337_, pResourceManager, pExecutor);
                        fontmanager$unresolvedbuilderbundle.add(fontmanager$builderid, fontoption$filter, completablefuture);
                    }).ifRight(p_325345_ -> fontmanager$unresolvedbuilderbundle.add(fontmanager$builderid, fontoption$filter, p_325345_));
                }

                return fontmanager$unresolvedbuilderbundle;
            }, pExecutor));
        }

        return Util.sequence(list)
            .thenCompose(
                p_325341_ -> {
                    List<CompletableFuture<Optional<GlyphProvider>>> list1 = p_325341_.stream()
                        .flatMap(FontManager.UnresolvedBuilderBundle::listBuilders)
                        .collect(Util.m_323807_());
                    GlyphProvider.Conditional glyphprovider$conditional = m_322949_();
                    list1.add(CompletableFuture.completedFuture(Optional.of(glyphprovider$conditional.f_316017_())));
                    return Util.sequence(list1)
                        .thenCompose(
                            p_284618_ -> {
                                Map<ResourceLocation, List<GlyphProvider.Conditional>> map = this.resolveProviders(p_325341_);
                                CompletableFuture<?>[] completablefuture = map.values()
                                    .stream()
                                    .map(p_284585_ -> CompletableFuture.runAsync(() -> this.finalizeProviderLoading(p_284585_, glyphprovider$conditional), pExecutor))
                                    .toArray(CompletableFuture[]::new);
                                return CompletableFuture.allOf(completablefuture).thenApply(p_284595_ -> {
                                    List<GlyphProvider> list2 = p_284618_.stream().flatMap(Optional::stream).toList();
                                    return new FontManager.Preparation(map, list2);
                                });
                            }
                        );
                }
            );
    }

    private CompletableFuture<Optional<GlyphProvider>> safeLoad(
        FontManager.BuilderId pBuilderId, GlyphProviderDefinition.Loader pLoader, ResourceManager pResourceManager, Executor pExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(pLoader.load(pResourceManager));
            } catch (Exception exception) {
                LOGGER.warn("Failed to load builder {}, rejecting", pBuilderId, exception);
                return Optional.empty();
            }
        }, pExecutor);
    }

    private Map<ResourceLocation, List<GlyphProvider.Conditional>> resolveProviders(List<FontManager.UnresolvedBuilderBundle> pUnresolvedBuilderBundles) {
        Map<ResourceLocation, List<GlyphProvider.Conditional>> map = new HashMap<>();
        DependencySorter<ResourceLocation, FontManager.UnresolvedBuilderBundle> dependencysorter = new DependencySorter<>();
        pUnresolvedBuilderBundles.forEach(p_284626_ -> dependencysorter.addEntry(p_284626_.fontId, p_284626_));
        dependencysorter.orderByDependencies(
            (p_284620_, p_284621_) -> p_284621_.resolve(map::get).ifPresent(p_284590_ -> map.put(p_284620_, (List<GlyphProvider.Conditional>)p_284590_))
        );
        return map;
    }

    private void finalizeProviderLoading(List<GlyphProvider.Conditional> pProviders, GlyphProvider.Conditional p_328834_) {
        pProviders.add(0, p_328834_);
        IntSet intset = new IntOpenHashSet();

        for (GlyphProvider.Conditional glyphprovider$conditional : pProviders) {
            intset.addAll(glyphprovider$conditional.f_316017_().getSupportedGlyphs());
        }

        intset.forEach(p_325339_ -> {
            if (p_325339_ != 32) {
                for (GlyphProvider.Conditional glyphprovider$conditional1 : Lists.reverse(pProviders)) {
                    if (glyphprovider$conditional1.f_316017_().getGlyph(p_325339_) != null) {
                        break;
                    }
                }
            }
        });
    }

    private static Set<FontOption> m_323936_(Options p_331588_) {
        Set<FontOption> set = EnumSet.noneOf(FontOption.class);
        if (p_331588_.forceUnicodeFont().get()) {
            set.add(FontOption.UNIFORM);
        }

        if (p_331588_.m_321442_().get()) {
            set.add(FontOption.JAPANESE_VARIANTS);
        }

        return set;
    }

    private void apply(FontManager.Preparation pPreperation, ProfilerFiller pProfiler) {
        pProfiler.startTick();
        pProfiler.push("closing");
        this.f_316373_ = null;
        this.fontSets.values().forEach(FontSet::close);
        this.fontSets.clear();
        this.providersToClose.forEach(GlyphProvider::close);
        this.providersToClose.clear();
        Set<FontOption> set = m_323936_(Minecraft.getInstance().options);
        pProfiler.popPush("reloading");
        pPreperation.f_316073_().forEach((p_325349_, p_325350_) -> {
            FontSet fontset = new FontSet(this.textureManager, p_325349_);
            fontset.m_321905_(Lists.reverse((List<GlyphProvider.Conditional>)p_325350_), set);
            this.fontSets.put(p_325349_, fontset);
        });
        this.providersToClose.addAll(pPreperation.allProviders);
        pProfiler.pop();
        pProfiler.endTick();
        if (!this.fontSets.containsKey(Minecraft.DEFAULT_FONT)) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    public void m_324236_(Options p_335215_) {
        Set<FontOption> set = m_323936_(p_335215_);

        for (FontSet fontset : this.fontSets.values()) {
            fontset.reload(set);
        }
    }

    private static List<Pair<FontManager.BuilderId, GlyphProviderDefinition.Conditional>> loadResourceStack(List<Resource> pResources, ResourceLocation pFontId) {
        List<Pair<FontManager.BuilderId, GlyphProviderDefinition.Conditional>> list = new ArrayList<>();

        for (Resource resource : pResources) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement jsonelement = GSON.fromJson(reader, JsonElement.class);
                FontManager.FontDefinitionFile fontmanager$fontdefinitionfile = FontManager.FontDefinitionFile.CODEC
                    .parse(JsonOps.INSTANCE, jsonelement)
                    .getOrThrow(JsonParseException::new);
                List<GlyphProviderDefinition.Conditional> list1 = fontmanager$fontdefinitionfile.providers;

                for (int i = list1.size() - 1; i >= 0; i--) {
                    FontManager.BuilderId fontmanager$builderid = new FontManager.BuilderId(pFontId, resource.sourcePackId(), i);
                    list.add(Pair.of(fontmanager$builderid, list1.get(i)));
                }
            } catch (Exception exception) {
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", pFontId, "fonts.json", resource.sourcePackId(), exception);
            }
        }

        return list;
    }

    public Font createFont() {
        return new Font(this::m_322106_, false);
    }

    public Font createFontFilterFishy() {
        return new Font(this::m_322106_, true);
    }

    private FontSet m_321594_(ResourceLocation p_333296_) {
        return this.fontSets.getOrDefault(p_333296_, this.missingFontSet);
    }

    private FontSet m_322106_(ResourceLocation p_332431_) {
        FontSet fontset = this.f_316373_;
        if (fontset != null && p_332431_.equals(fontset.m_321601_())) {
            return fontset;
        } else {
            FontSet fontset1 = this.m_321594_(p_332431_);
            this.f_316373_ = fontset1;
            return fontset1;
        }
    }

    @Override
    public void close() {
        this.fontSets.values().forEach(FontSet::close);
        this.providersToClose.forEach(GlyphProvider::close);
        this.missingFontSet.close();
    }

    @OnlyIn(Dist.CLIENT)
    static record BuilderId(ResourceLocation fontId, String pack, int index) {
        @Override
        public String toString() {
            return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record BuilderResult(
        FontManager.BuilderId id, FontOption.Filter f_316941_, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result
    ) {
        public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> pProviderResolver) {
            return this.result
                .map(
                    p_325356_ -> p_325356_.join().map(p_325357_ -> List.of(new GlyphProvider.Conditional(p_325357_, this.f_316941_))),
                    p_325359_ -> {
                        List<GlyphProvider.Conditional> list = pProviderResolver.apply(p_325359_);
                        if (list == null) {
                            FontManager.LOGGER
                                .warn(
                                    "Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle",
                                    p_325359_,
                                    this.id
                                );
                            return Optional.empty();
                        } else {
                            return Optional.of(list.stream().map(this::m_320486_).toList());
                        }
                    }
                );
        }

        private GlyphProvider.Conditional m_320486_(GlyphProvider.Conditional p_330532_) {
            return new GlyphProvider.Conditional(p_330532_.f_316017_(), this.f_316941_.m_323896_(p_330532_.f_316533_()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record FontDefinitionFile(List<GlyphProviderDefinition.Conditional> providers) {
        public static final Codec<FontManager.FontDefinitionFile> CODEC = RecordCodecBuilder.create(
            p_325360_ -> p_325360_.group(
                        GlyphProviderDefinition.Conditional.f_316293_.listOf().fieldOf("providers").forGetter(FontManager.FontDefinitionFile::providers)
                    )
                    .apply(p_325360_, FontManager.FontDefinitionFile::new)
        );
    }

    @OnlyIn(Dist.CLIENT)
    static record Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> f_316073_, List<GlyphProvider> allProviders) {
    }

    @OnlyIn(Dist.CLIENT)
    static record UnresolvedBuilderBundle(ResourceLocation fontId, List<FontManager.BuilderResult> builders, Set<ResourceLocation> dependencies)
        implements DependencySorter.Entry<ResourceLocation> {
        public UnresolvedBuilderBundle(ResourceLocation pFontId) {
            this(pFontId, new ArrayList<>(), new HashSet<>());
        }

        public void add(FontManager.BuilderId pId, FontOption.Filter p_336303_, GlyphProviderDefinition.Reference p_334249_) {
            this.builders.add(new FontManager.BuilderResult(pId, p_336303_, Either.right(p_334249_.id())));
            this.dependencies.add(p_334249_.id());
        }

        public void add(FontManager.BuilderId pBuilderId, FontOption.Filter p_334374_, CompletableFuture<Optional<GlyphProvider>> p_331945_) {
            this.builders.add(new FontManager.BuilderResult(pBuilderId, p_334374_, Either.left(p_331945_)));
        }

        private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
            return this.builders.stream().flatMap(p_285041_ -> p_285041_.result.left().stream());
        }

        public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> pProviderResolver) {
            List<GlyphProvider.Conditional> list = new ArrayList<>();

            for (FontManager.BuilderResult fontmanager$builderresult : this.builders) {
                Optional<List<GlyphProvider.Conditional>> optional = fontmanager$builderresult.resolve(pProviderResolver);
                if (!optional.isPresent()) {
                    return Optional.empty();
                }

                list.addAll(optional.get());
            }

            return Optional.of(list);
        }

        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> pVisitor) {
            this.dependencies.forEach(pVisitor);
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> pVisitor) {
        }
    }
}