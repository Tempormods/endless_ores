package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {
    Stream<Holder.Reference<T>> listElements();

    default Stream<ResourceKey<T>> listElementIds() {
        return this.listElements().map(Holder.Reference::key);
    }

    Stream<HolderSet.Named<T>> listTags();

    default Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    public interface Provider {
        Stream<ResourceKey<? extends Registry<?>>> m_305097_();

        <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> pRegistryKey);

        default <T> HolderLookup.RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> pRegistryKey) {
            return this.lookup(pRegistryKey).orElseThrow(() -> new IllegalStateException("Registry " + pRegistryKey.location() + " not found"));
        }

        default <V> RegistryOps<V> m_318927_(DynamicOps<V> p_330698_) {
            return RegistryOps.create(p_330698_, this);
        }

        default HolderGetter.Provider asGetterLookup() {
            return new HolderGetter.Provider() {
                @Override
                public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256379_) {
                    return Provider.this.lookup(p_256379_).map(p_255952_ -> (HolderGetter<T>)p_255952_);
                }
            };
        }

        static HolderLookup.Provider create(Stream<HolderLookup.RegistryLookup<?>> pLookupStream) {
            final Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> map = pLookupStream.collect(
                Collectors.toUnmodifiableMap(HolderLookup.RegistryLookup::key, p_256335_ -> p_256335_)
            );
            return new HolderLookup.Provider() {
                @Override
                public Stream<ResourceKey<? extends Registry<?>>> m_305097_() {
                    return map.keySet().stream();
                }

                @Override
                public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_255663_) {
                    return Optional.ofNullable((HolderLookup.RegistryLookup<T>)map.get(p_255663_));
                }
            };
        }
    }

    public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
        ResourceKey<? extends Registry<? extends T>> key();

        Lifecycle m_255098_();

        default HolderLookup.RegistryLookup<T> filterFeatures(FeatureFlagSet pEnabledFeatures) {
            return FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.m_318796_(p_250240_ -> ((FeatureElement)p_250240_).isEnabled(pEnabledFeatures)) : this;
        }

        default HolderLookup.RegistryLookup<T> m_318796_(final Predicate<T> p_334671_) {
            return new HolderLookup.RegistryLookup.Delegate<T>() {
                @Override
                public HolderLookup.RegistryLookup<T> parent() {
                    return RegistryLookup.this;
                }

                @Override
                public Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_330384_) {
                    return this.parent().m_254926_(p_330384_).filter(p_330697_ -> p_334671_.test(p_330697_.value()));
                }

                @Override
                public Stream<Holder.Reference<T>> listElements() {
                    return this.parent().listElements().filter(p_331718_ -> p_334671_.test(p_331718_.value()));
                }
            };
        }

        public interface Delegate<T> extends HolderLookup.RegistryLookup<T> {
            HolderLookup.RegistryLookup<T> parent();

            @Override
            default ResourceKey<? extends Registry<? extends T>> key() {
                return this.parent().key();
            }

            @Override
            default Lifecycle m_255098_() {
                return this.parent().m_255098_();
            }

            @Override
            default Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_255619_) {
                return this.parent().m_254926_(p_255619_);
            }

            @Override
            default Stream<Holder.Reference<T>> listElements() {
                return this.parent().listElements();
            }

            @Override
            default Optional<HolderSet.Named<T>> m_255050_(TagKey<T> p_256245_) {
                return this.parent().m_255050_(p_256245_);
            }

            @Override
            default Stream<HolderSet.Named<T>> listTags() {
                return this.parent().listTags();
            }
        }
    }
}