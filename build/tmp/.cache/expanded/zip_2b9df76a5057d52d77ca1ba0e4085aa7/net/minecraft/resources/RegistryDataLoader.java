package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class RegistryDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RegistrationInfo f_315375_ = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
    private static final Function<Optional<KnownPack>, RegistrationInfo> f_315605_ = Util.memoize(p_326161_ -> {
        Lifecycle lifecycle = p_326161_.map(KnownPack::m_323138_).map(p_326166_ -> Lifecycle.stable()).orElse(Lifecycle.experimental());
        return new RegistrationInfo(p_326161_, lifecycle);
    });
    public static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = net.minecraftforge.registries.DataPackRegistriesHooks.grabWorldgenRegistries(
        new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.f_316491_),
        new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE, Structure.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.f_317086_, WolfVariant.f_314617_),
        new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.f_314803_),
        new RegistryDataLoader.RegistryData<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.BANNER_PATTERN, BannerPattern.f_316004_)
    );
    public static final List<RegistryDataLoader.RegistryData<?>> DIMENSION_REGISTRIES = List.of(
        new RegistryDataLoader.RegistryData<>(Registries.LEVEL_STEM, LevelStem.CODEC)
    );
    public static final List<RegistryDataLoader.RegistryData<?>> f_314951_ = List.of(
        new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.NETWORK_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.f_316491_),
        new RegistryDataLoader.RegistryData<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.f_317086_, WolfVariant.f_314617_),
        new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
        new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.f_314803_),
        new RegistryDataLoader.RegistryData<>(Registries.BANNER_PATTERN, BannerPattern.f_316004_)
    );

    public static java.util.stream.Stream<RegistryDataLoader.RegistryData<?>> getWorldGenAndDimensionStream() {
        return java.util.stream.Stream.concat(RegistryDataLoader.WORLDGEN_REGISTRIES.stream(), RegistryDataLoader.DIMENSION_REGISTRIES.stream());
    }

    public static RegistryAccess.Frozen load(ResourceManager pResourceManager, RegistryAccess pRegistryAccess, List<RegistryDataLoader.RegistryData<?>> pRegistryData) {
        return m_324686_((p_326156_, p_326157_) -> p_326156_.m_320768_(pResourceManager, p_326157_), pRegistryAccess, pRegistryData);
    }

    public static RegistryAccess.Frozen m_321840_(
        Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> p_328212_,
        ResourceProvider p_335625_,
        RegistryAccess p_334195_,
        List<RegistryDataLoader.RegistryData<?>> p_329346_
    ) {
        return m_324686_((p_326153_, p_326154_) -> p_326153_.m_323419_(p_328212_, p_335625_, p_326154_), p_334195_, p_329346_);
    }

    public static RegistryAccess.Frozen m_324686_(
        RegistryDataLoader.LoadingFunction p_332256_, RegistryAccess p_331736_, List<RegistryDataLoader.RegistryData<?>> p_333463_
    ) {
        Map<ResourceKey<?>, Exception> map = new HashMap<>();
        List<RegistryDataLoader.Loader<?>> list = p_333463_.stream()
            .map(p_326168_ -> p_326168_.create(Lifecycle.stable(), map))
            .collect(Collectors.toUnmodifiableList());
        RegistryOps.RegistryInfoLookup registryops$registryinfolookup = createContext(p_331736_, list);
        list.forEach(p_326160_ -> p_332256_.m_324362_((RegistryDataLoader.Loader<?>)p_326160_, registryops$registryinfolookup));
        list.forEach(p_326165_ -> {
            Registry<?> registry = p_326165_.f_314680_();

            try {
                registry.freeze();
            } catch (Exception exception) {
                map.put(registry.key(), exception);
            }
        });
        if (!map.isEmpty()) {
            logErrors(map);
            var buf = new StringBuilder("Failed to load registries, see debug.log for more details:");
            map.forEach((k,v) -> buf.append("\n\t").append(k.toString()).append(": ").append(v.getMessage()));
            throw new IllegalStateException(buf.toString());
        } else {
            return new RegistryAccess.ImmutableRegistryAccess(list.stream().map(RegistryDataLoader.Loader::f_314680_).toList()).freeze();
        }
    }

    private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess pRegistryAccess, List<RegistryDataLoader.Loader<?>> pRegistryLoaders) {
        final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap<>();
        pRegistryAccess.registries().forEach(p_255505_ -> map.put(p_255505_.key(), createInfoForContextRegistry(p_255505_.value())));
        pRegistryLoaders.forEach(p_326163_ -> map.put(p_326163_.f_314680_.key(), createInfoForNewRegistry(p_326163_.f_314680_)));
        return new RegistryOps.RegistryInfoLookup() {
            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256014_) {
                return Optional.ofNullable((RegistryOps.RegistryInfo<T>)map.get(p_256014_));
            }
        };
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> pRegistry) {
        return new RegistryOps.RegistryInfo<>(pRegistry.asLookup(), pRegistry.createRegistrationLookup(), pRegistry.registryLifecycle());
    }

    private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(Registry<T> pRegistry) {
        return new RegistryOps.RegistryInfo<>(pRegistry.asLookup(), pRegistry.asTagAddingLookup(), pRegistry.registryLifecycle());
    }

    private static void logErrors(Map<ResourceKey<?>, Exception> pErrors) {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        Map<ResourceLocation, Map<ResourceLocation, Exception>> map = pErrors.entrySet()
            .stream()
            .collect(
                Collectors.groupingBy(
                    p_249353_ -> p_249353_.getKey().registry(), Collectors.toMap(p_251444_ -> p_251444_.getKey().location(), Entry::getValue)
                )
            );
        map.entrySet().stream().sorted(Entry.comparingByKey()).forEach(p_249838_ -> {
            printwriter.printf("> Errors in registry %s:%n", p_249838_.getKey());
            p_249838_.getValue().entrySet().stream().sorted(Entry.comparingByKey()).forEach(p_250688_ -> {
                printwriter.printf(">> Errors in element %s:%n", p_250688_.getKey());
                p_250688_.getValue().printStackTrace(printwriter);
            });
        });
        printwriter.flush();
        LOGGER.error("Registry loading errors:\n{}", stringwriter);
    }

    private static String registryDirPath(ResourceLocation pLocation) {
        return net.minecraftforge.common.ForgeHooks.prefixNamespace(pLocation); // FORGE: add non-vanilla registry namespace to loader directory, same format as tag directory (see net.minecraft.tags.TagManager#getTagDir(ResourceKey))
    }

    private static <E> void m_323888_(
        WritableRegistry<E> p_330991_,
        Decoder<Optional<E>> p_333909_,
        RegistryOps<JsonElement> p_332135_,
        ResourceKey<E> p_332850_,
        Resource p_335244_,
        RegistrationInfo p_332222_
    ) throws IOException {
        try (Reader reader = p_335244_.openAsReader()) {
            JsonElement jsonelement = JsonParser.parseReader(reader);

            var result = p_333909_.decode(p_332135_, jsonelement);
            if (result.result().map(p -> p.getFirst()).isEmpty()) {
               LOGGER.debug("Skipping {} conditions not met", p_332850_);
               return;
            }
            DataResult<E> dataresult = result.map(p -> p.mapFirst(Optional::get)).map(p -> p.getFirst());

            E e = dataresult.getOrThrow();
            p_330991_.register(p_332850_, e, p_332222_);
        }
    }

    static <E> void m_324595_(
        ResourceManager p_335634_,
        RegistryOps.RegistryInfoLookup p_333035_,
        WritableRegistry<E> p_331358_,
        Decoder<E> p_329404_,
        Map<ResourceKey<?>, Exception> p_335074_
    ) {
        String s = registryDirPath(p_331358_.key().location());
        FileToIdConverter filetoidconverter = FileToIdConverter.json(s);
        RegistryOps<JsonElement> registryops = RegistryOps.create(JsonOps.INSTANCE, p_333035_);
        Decoder<Optional<E>> conditional = net.minecraftforge.common.crafting.conditions.ConditionCodec.wrap(p_329404_);

        for (Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(p_335634_).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceKey<E> resourcekey = ResourceKey.create(p_331358_.key(), filetoidconverter.fileToId(resourcelocation));
            Resource resource = entry.getValue();
            RegistrationInfo registrationinfo = f_315605_.apply(resource.m_322763_());

            try {
                m_323888_(p_331358_, conditional, registryops, resourcekey, resource, registrationinfo);
            } catch (Exception exception) {
                p_335074_.put(
                    resourcekey,
                    new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", resourcelocation, resource.sourcePackId()), exception)
                );
            }
        }
    }

    static <E> void m_321304_(
        Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> p_331925_,
        ResourceProvider p_332010_,
        RegistryOps.RegistryInfoLookup p_329253_,
        WritableRegistry<E> p_332518_,
        Decoder<E> p_328898_,
        Map<ResourceKey<?>, Exception> p_335768_
    ) {
        List<RegistrySynchronization.PackedRegistryEntry> list = p_331925_.get(p_332518_.key());
        if (list != null) {
            RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, p_329253_);
            RegistryOps<JsonElement> registryops1 = RegistryOps.create(JsonOps.INSTANCE, p_329253_)
                .withContext(net.minecraftforge.common.crafting.conditions.ICondition.IContext.KEY, net.minecraftforge.common.crafting.conditions.ICondition.IContext.TAGS_INVALID);
            Decoder<Optional<E>> conditional = net.minecraftforge.common.crafting.conditions.ConditionCodec.wrap(p_328898_);
            String s = registryDirPath(p_332518_.key().location());
            FileToIdConverter filetoidconverter = FileToIdConverter.json(s);

            for (RegistrySynchronization.PackedRegistryEntry registrysynchronization$packedregistryentry : list) {
                ResourceKey<E> resourcekey = ResourceKey.create(p_332518_.key(), registrysynchronization$packedregistryentry.f_316651_());
                Optional<Tag> optional = registrysynchronization$packedregistryentry.f_314493_();
                if (optional.isPresent()) {
                    try {
                        DataResult<E> dataresult = p_328898_.parse(registryops, optional.get());
                        E e = dataresult.getOrThrow();
                        p_332518_.register(resourcekey, e, f_315375_);
                    } catch (Exception exception) {
                        p_335768_.put(
                            resourcekey,
                            new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", optional.get()), exception)
                        );
                    }
                } else {
                    ResourceLocation resourcelocation = filetoidconverter.idToFile(registrysynchronization$packedregistryentry.f_316651_());

                    try {
                        Resource resource = p_332010_.getResourceOrThrow(resourcelocation);
                        m_323888_(p_332518_, conditional, registryops1, resourcekey, resource, f_315375_);
                    } catch (Exception exception1) {
                        p_335768_.put(resourcekey, new IllegalStateException("Failed to parse local data", exception1));
                    }
                }
            }
        }
    }

    static record Loader<T>(RegistryDataLoader.RegistryData<T> f_316451_, WritableRegistry<T> f_314680_, Map<ResourceKey<?>, Exception> f_315628_) {
        public void m_320768_(ResourceManager p_328137_, RegistryOps.RegistryInfoLookup p_330371_) {
            RegistryDataLoader.m_324595_(p_328137_, p_330371_, this.f_314680_, this.f_316451_.elementCodec, this.f_315628_);
        }

        public void m_323419_(
            Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> p_333047_,
            ResourceProvider p_333682_,
            RegistryOps.RegistryInfoLookup p_330665_
        ) {
            RegistryDataLoader.m_321304_(p_333047_, p_333682_, p_330665_, this.f_314680_, this.f_316451_.elementCodec, this.f_315628_);
        }
    }

    @FunctionalInterface
    interface LoadingFunction {
        void m_324362_(RegistryDataLoader.Loader<?> p_332841_, RegistryOps.RegistryInfoLookup p_332366_);
    }

    public static record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec) {
        RegistryDataLoader.Loader<T> create(Lifecycle pRegistryLifecycle, Map<ResourceKey<?>, Exception> pExceptions) {
            WritableRegistry<T> writableregistry = new MappedRegistry<>(this.key, pRegistryLifecycle);
            return new RegistryDataLoader.Loader<>(this, writableregistry, pExceptions);
        }

        public void m_305182_(BiConsumer<ResourceKey<? extends Registry<T>>, Codec<T>> p_310351_) {
            p_310351_.accept(this.key, this.elementCodec);
        }
    }
}
