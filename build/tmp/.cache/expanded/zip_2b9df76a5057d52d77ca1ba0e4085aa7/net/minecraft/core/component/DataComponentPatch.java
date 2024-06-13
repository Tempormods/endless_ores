package net.minecraft.core.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

public final class DataComponentPatch {
    public static final DataComponentPatch f_315512_ = new DataComponentPatch(Reference2ObjectMaps.emptyMap());
    public static final Codec<DataComponentPatch> f_315187_ = Codec.dispatchedMap(DataComponentPatch.PatchKey.f_315701_, DataComponentPatch.PatchKey::m_318804_)
        .xmap(p_330428_ -> {
            if (p_330428_.isEmpty()) {
                return f_315512_;
            } else {
                Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(p_330428_.size());

                for (Entry<DataComponentPatch.PatchKey, ?> entry : p_330428_.entrySet()) {
                    DataComponentPatch.PatchKey datacomponentpatch$patchkey = entry.getKey();
                    if (datacomponentpatch$patchkey.f_314373_()) {
                        reference2objectmap.put(datacomponentpatch$patchkey.f_314069_(), Optional.empty());
                    } else {
                        reference2objectmap.put(datacomponentpatch$patchkey.f_314069_(), Optional.of(entry.getValue()));
                    }
                }

                return new DataComponentPatch(reference2objectmap);
            }
        }, p_335950_ -> {
            Reference2ObjectMap<DataComponentPatch.PatchKey, Object> reference2objectmap = new Reference2ObjectArrayMap<>(p_335950_.f_314958_.size());

            for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(p_335950_.f_314958_)) {
                DataComponentType<?> datacomponenttype = entry.getKey();
                if (!datacomponenttype.m_322187_()) {
                    Optional<?> optional = entry.getValue();
                    if (optional.isPresent()) {
                        reference2objectmap.put(new DataComponentPatch.PatchKey(datacomponenttype, false), optional.get());
                    } else {
                        reference2objectmap.put(new DataComponentPatch.PatchKey(datacomponenttype, true), Unit.INSTANCE);
                    }
                }
            }

            return (Reference2ObjectMap)reference2objectmap;
        });
    public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> f_314779_ = new StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch>() {
        public DataComponentPatch m_318688_(RegistryFriendlyByteBuf p_330298_) {
            int i = p_330298_.readVarInt();
            int j = p_330298_.readVarInt();
            if (i == 0 && j == 0) {
                return DataComponentPatch.f_315512_;
            } else {
                Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(i + j);

                for (int k = 0; k < i; k++) {
                    DataComponentType<?> datacomponenttype = DataComponentType.f_315368_.m_318688_(p_330298_);
                    Object object = datacomponenttype.m_318786_().m_318688_(p_330298_);
                    reference2objectmap.put(datacomponenttype, Optional.of(object));
                }

                for (int l = 0; l < j; l++) {
                    DataComponentType<?> datacomponenttype1 = DataComponentType.f_315368_.m_318688_(p_330298_);
                    reference2objectmap.put(datacomponenttype1, Optional.empty());
                }

                return new DataComponentPatch(reference2objectmap);
            }
        }

        public void m_318638_(RegistryFriendlyByteBuf p_334360_, DataComponentPatch p_336144_) {
            if (p_336144_.m_323586_()) {
                p_334360_.writeVarInt(0);
                p_334360_.writeVarInt(0);
            } else {
                int i = 0;
                int j = 0;

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
                    p_336144_.f_314958_
                )) {
                    if (entry.getValue().isPresent()) {
                        i++;
                    } else {
                        j++;
                    }
                }

                p_334360_.writeVarInt(i);
                p_334360_.writeVarInt(j);

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry1 : Reference2ObjectMaps.fastIterable(
                    p_336144_.f_314958_
                )) {
                    Optional<?> optional = entry1.getValue();
                    if (optional.isPresent()) {
                        DataComponentType<?> datacomponenttype = entry1.getKey();
                        DataComponentType.f_315368_.m_318638_(p_334360_, datacomponenttype);
                        m_321968_(p_334360_, datacomponenttype, optional.get());
                    }
                }

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry2 : Reference2ObjectMaps.fastIterable(
                    p_336144_.f_314958_
                )) {
                    if (entry2.getValue().isEmpty()) {
                        DataComponentType<?> datacomponenttype1 = entry2.getKey();
                        DataComponentType.f_315368_.m_318638_(p_334360_, datacomponenttype1);
                    }
                }
            }
        }

        private static <T> void m_321968_(RegistryFriendlyByteBuf p_331641_, DataComponentType<T> p_335861_, Object p_335951_) {
            p_335861_.m_318786_().m_318638_(p_331641_, (T)p_335951_);
        }
    };
    private static final String f_315891_ = "!";
    final Reference2ObjectMap<DataComponentType<?>, Optional<?>> f_314958_;

    DataComponentPatch(Reference2ObjectMap<DataComponentType<?>, Optional<?>> p_329783_) {
        this.f_314958_ = p_329783_;
    }

    public static DataComponentPatch.Builder m_322543_() {
        return new DataComponentPatch.Builder();
    }

    @Nullable
    public <T> Optional<? extends T> m_319281_(DataComponentType<? extends T> p_330742_) {
        return (Optional<? extends T>)this.f_314958_.get(p_330742_);
    }

    public Set<Entry<DataComponentType<?>, Optional<?>>> m_320936_() {
        return this.f_314958_.entrySet();
    }

    public int m_325008_() {
        return this.f_314958_.size();
    }

    public DataComponentPatch m_318691_(Predicate<DataComponentType<?>> p_333810_) {
        if (this.m_323586_()) {
            return f_315512_;
        } else {
            Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(this.f_314958_);
            reference2objectmap.keySet().removeIf(p_333810_);
            return reference2objectmap.isEmpty() ? f_315512_ : new DataComponentPatch(reference2objectmap);
        }
    }

    public boolean m_323586_() {
        return this.f_314958_.isEmpty();
    }

    public DataComponentPatch.SplitResult m_324808_() {
        if (this.m_323586_()) {
            return DataComponentPatch.SplitResult.f_316605_;
        } else {
            DataComponentMap.Builder datacomponentmap$builder = DataComponentMap.m_323371_();
            Set<DataComponentType<?>> set = Sets.newIdentityHashSet();
            this.f_314958_.forEach((p_336136_, p_328765_) -> {
                if (p_328765_.isPresent()) {
                    datacomponentmap$builder.m_321201_((DataComponentType<?>)p_336136_, p_328765_.get());
                } else {
                    set.add((DataComponentType<?>)p_336136_);
                }
            });
            return new DataComponentPatch.SplitResult(datacomponentmap$builder.m_318826_(), set);
        }
    }

    @Override
    public boolean equals(Object p_334345_) {
        if (this == p_334345_) {
            return true;
        } else {
            if (p_334345_ instanceof DataComponentPatch datacomponentpatch && this.f_314958_.equals(datacomponentpatch.f_314958_)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.f_314958_.hashCode();
    }

    @Override
    public String toString() {
        return m_318799_(this.f_314958_);
    }

    static String m_318799_(Reference2ObjectMap<DataComponentType<?>, Optional<?>> p_335670_) {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append('{');
        boolean flag = true;

        for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(p_335670_)) {
            if (flag) {
                flag = false;
            } else {
                stringbuilder.append(", ");
            }

            Optional<?> optional = entry.getValue();
            if (optional.isPresent()) {
                stringbuilder.append(entry.getKey());
                stringbuilder.append("=>");
                stringbuilder.append(optional.get());
            } else {
                stringbuilder.append("!");
                stringbuilder.append(entry.getKey());
            }
        }

        stringbuilder.append('}');
        return stringbuilder.toString();
    }

    public static class Builder {
        private final Reference2ObjectMap<DataComponentType<?>, Optional<?>> f_314444_ = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        public <T> DataComponentPatch.Builder m_323566_(DataComponentType<T> p_329935_, T p_331578_) {
            this.f_314444_.put(p_329935_, Optional.of(p_331578_));
            return this;
        }

        public <T> DataComponentPatch.Builder m_318729_(DataComponentType<T> p_329018_) {
            this.f_314444_.put(p_329018_, Optional.empty());
            return this;
        }

        public <T> DataComponentPatch.Builder m_323094_(TypedDataComponent<T> p_331095_) {
            return this.m_323566_(p_331095_.f_316611_(), p_331095_.f_314804_());
        }

        public DataComponentPatch m_323652_() {
            return this.f_314444_.isEmpty() ? DataComponentPatch.f_315512_ : new DataComponentPatch(this.f_314444_);
        }
    }

    static record PatchKey(DataComponentType<?> f_314069_, boolean f_314373_) {
        public static final Codec<DataComponentPatch.PatchKey> f_315701_ = Codec.STRING
            .flatXmap(
                p_330758_ -> {
                    boolean flag = p_330758_.startsWith("!");
                    if (flag) {
                        p_330758_ = p_330758_.substring("!".length());
                    }

                    ResourceLocation resourcelocation = ResourceLocation.tryParse(p_330758_);
                    DataComponentType<?> datacomponenttype = BuiltInRegistries.f_315333_.get(resourcelocation);
                    if (datacomponenttype == null) {
                        return DataResult.error(() -> "No component with type: '" + resourcelocation + "'");
                    } else {
                        return datacomponenttype.m_322187_()
                            ? DataResult.error(() -> "'" + resourcelocation + "' is not a persistent component")
                            : DataResult.success(new DataComponentPatch.PatchKey(datacomponenttype, flag));
                    }
                },
                p_329482_ -> {
                    DataComponentType<?> datacomponenttype = p_329482_.f_314069_();
                    ResourceLocation resourcelocation = BuiltInRegistries.f_315333_.getKey(datacomponenttype);
                    return resourcelocation == null
                        ? DataResult.error(() -> "Unregistered component: " + datacomponenttype)
                        : DataResult.success(p_329482_.f_314373_() ? "!" + resourcelocation : resourcelocation.toString());
                }
            );

        public Codec<?> m_318804_() {
            return this.f_314373_ ? Codec.EMPTY.codec() : this.f_314069_.m_319588_();
        }
    }

    public static record SplitResult(DataComponentMap f_314173_, Set<DataComponentType<?>> f_315411_) {
        public static final DataComponentPatch.SplitResult f_316605_ = new DataComponentPatch.SplitResult(DataComponentMap.f_314291_, Set.of());
    }
}