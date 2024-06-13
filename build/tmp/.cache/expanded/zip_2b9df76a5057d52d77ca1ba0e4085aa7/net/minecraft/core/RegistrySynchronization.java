package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.KnownPack;

public class RegistrySynchronization {
    public static final Set<ResourceKey<? extends Registry<?>>> NETWORKABLE_REGISTRIES = RegistryDataLoader.f_314951_
        .stream()
        .map(RegistryDataLoader.RegistryData::key)
        .collect(Collectors.toUnmodifiableSet());

    public static void m_319314_(
        DynamicOps<Tag> p_330752_,
        RegistryAccess p_332359_,
        Set<KnownPack> p_331327_,
        BiConsumer<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> p_335166_
    ) {
        RegistryDataLoader.f_314951_.forEach(p_325710_ -> m_321996_(p_330752_, (RegistryDataLoader.RegistryData<?>)p_325710_, p_332359_, p_331327_, p_335166_));
    }

    private static <T> void m_321996_(
        DynamicOps<Tag> p_328835_,
        RegistryDataLoader.RegistryData<T> p_329218_,
        RegistryAccess p_335981_,
        Set<KnownPack> p_330196_,
        BiConsumer<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> p_330046_
    ) {
        p_335981_.registry(p_329218_.key())
            .ifPresent(
                p_325705_ -> {
                    List<RegistrySynchronization.PackedRegistryEntry> list = new ArrayList<>(p_325705_.size());
                    p_325705_.holders()
                        .forEach(
                            p_325717_ -> {
                                boolean flag = p_325705_.lifecycle(p_325717_.key())
                                    .flatMap(RegistrationInfo::f_315839_)
                                    .filter(p_330196_::contains)
                                    .isPresent();
                                Optional<Tag> optional;
                                if (flag) {
                                    optional = Optional.empty();
                                } else {
                                    Tag tag = p_329218_.elementCodec()
                                        .encodeStart(p_328835_, p_325717_.value())
                                        .getOrThrow(
                                            p_325700_ -> new IllegalArgumentException("Failed to serialize " + p_325717_.key() + ": " + p_325700_)
                                        );
                                    optional = Optional.of(tag);
                                }

                                list.add(new RegistrySynchronization.PackedRegistryEntry(p_325717_.key().location(), optional));
                            }
                        );
                    p_330046_.accept(p_325705_.key(), list);
                }
            );
    }

    private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess pRegistryAccess) {
        return pRegistryAccess.registries().filter(p_325711_ -> NETWORKABLE_REGISTRIES.contains(p_325711_.key()));
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> pRegistryAccess) {
        return ownedNetworkableRegistries(pRegistryAccess.getAccessFrom(RegistryLayer.WORLDGEN));
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> pRegistryAccess) {
        Stream<RegistryAccess.RegistryEntry<?>> stream = pRegistryAccess.getLayer(RegistryLayer.STATIC).registries();
        Stream<RegistryAccess.RegistryEntry<?>> stream1 = networkedRegistries(pRegistryAccess);
        return Stream.concat(stream1, stream);
    }

    public static record PackedRegistryEntry(ResourceLocation f_316651_, Optional<Tag> f_314493_) {
        public static final StreamCodec<ByteBuf, RegistrySynchronization.PackedRegistryEntry> f_316015_ = StreamCodec.m_320349_(
            ResourceLocation.f_314488_,
            RegistrySynchronization.PackedRegistryEntry::f_316651_,
            ByteBufCodecs.f_316427_.m_321801_(ByteBufCodecs::m_319027_),
            RegistrySynchronization.PackedRegistryEntry::f_314493_,
            RegistrySynchronization.PackedRegistryEntry::new
        );
    }
}