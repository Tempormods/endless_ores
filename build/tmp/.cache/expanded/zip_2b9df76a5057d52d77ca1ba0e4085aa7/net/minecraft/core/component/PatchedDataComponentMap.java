package net.minecraft.core.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class PatchedDataComponentMap implements DataComponentMap {
    private final DataComponentMap f_316296_;
    private Reference2ObjectMap<DataComponentType<?>, Optional<?>> f_315990_;
    private boolean f_316660_;

    public PatchedDataComponentMap(DataComponentMap p_331141_) {
        this(p_331141_, Reference2ObjectMaps.emptyMap(), true);
    }

    private PatchedDataComponentMap(DataComponentMap p_335089_, Reference2ObjectMap<DataComponentType<?>, Optional<?>> p_333211_, boolean p_334948_) {
        this.f_316296_ = p_335089_;
        this.f_315990_ = p_333211_;
        this.f_316660_ = p_334948_;
    }

    public static PatchedDataComponentMap m_322493_(DataComponentMap p_334311_, DataComponentPatch p_332061_) {
        if (m_323581_(p_334311_, p_332061_.f_314958_)) {
            return new PatchedDataComponentMap(p_334311_, p_332061_.f_314958_, true);
        } else {
            PatchedDataComponentMap patcheddatacomponentmap = new PatchedDataComponentMap(p_334311_);
            patcheddatacomponentmap.m_320975_(p_332061_);
            return patcheddatacomponentmap;
        }
    }

    private static boolean m_323581_(DataComponentMap p_331971_, Reference2ObjectMap<DataComponentType<?>, Optional<?>> p_332857_) {
        for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(p_332857_)) {
            Object object = p_331971_.m_318834_(entry.getKey());
            Optional<?> optional = entry.getValue();
            if (optional.isPresent() && optional.get().equals(object)) {
                return false;
            }

            if (optional.isEmpty() && object == null) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    @Override
    public <T> T m_318834_(DataComponentType<? extends T> p_331525_) {
        Optional<? extends T> optional = (Optional<? extends T>)this.f_315990_.get(p_331525_);
        return (T)(optional != null ? optional.orElse(null) : this.f_316296_.m_318834_(p_331525_));
    }

    @Nullable
    public <T> T m_322371_(DataComponentType<? super T> p_334181_, @Nullable T p_328828_) {
        this.m_322433_();
        T t = this.f_316296_.m_318834_((DataComponentType<? extends T>)p_334181_);
        Optional<T> optional;
        if (Objects.equals(p_328828_, t)) {
            optional = (Optional<T>)this.f_315990_.remove(p_334181_);
        } else {
            optional = (Optional<T>)this.f_315990_.put(p_334181_, Optional.ofNullable(p_328828_));
        }

        return optional != null ? optional.orElse(t) : t;
    }

    @Nullable
    public <T> T m_321460_(DataComponentType<? extends T> p_331496_) {
        this.m_322433_();
        T t = this.f_316296_.m_318834_(p_331496_);
        Optional<? extends T> optional;
        if (t != null) {
            optional = (Optional<? extends T>)this.f_315990_.put(p_331496_, Optional.empty());
        } else {
            optional = (Optional<? extends T>)this.f_315990_.remove(p_331496_);
        }

        return (T)(optional != null ? optional.orElse(null) : t);
    }

    public void m_320975_(DataComponentPatch p_329626_) {
        this.m_322433_();

        for (Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(p_329626_.f_314958_)) {
            this.m_318645_(entry.getKey(), entry.getValue());
        }
    }

    private void m_318645_(DataComponentType<?> p_327856_, Optional<?> p_331456_) {
        Object object = this.f_316296_.m_318834_(p_327856_);
        if (p_331456_.isPresent()) {
            if (p_331456_.get().equals(object)) {
                this.f_315990_.remove(p_327856_);
            } else {
                this.f_315990_.put(p_327856_, p_331456_);
            }
        } else if (object != null) {
            this.f_315990_.put(p_327856_, Optional.empty());
        } else {
            this.f_315990_.remove(p_327856_);
        }
    }

    public void m_324830_(DataComponentPatch p_331119_) {
        this.m_322433_();
        this.f_315990_.clear();
        this.f_315990_.putAll(p_331119_.f_314958_);
    }

    public void m_324935_(DataComponentMap p_336067_) {
        for (TypedDataComponent<?> typeddatacomponent : p_336067_) {
            typeddatacomponent.m_324030_(this);
        }
    }

    private void m_322433_() {
        if (this.f_316660_) {
            this.f_315990_ = new Reference2ObjectArrayMap<>(this.f_315990_);
            this.f_316660_ = false;
        }
    }

    @Override
    public Set<DataComponentType<?>> m_319675_() {
        if (this.f_315990_.isEmpty()) {
            return this.f_316296_.m_319675_();
        } else {
            Set<DataComponentType<?>> set = new ReferenceArraySet<>(this.f_316296_.m_319675_());

            for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
                this.f_315990_
            )) {
                Optional<?> optional = entry.getValue();
                if (optional.isPresent()) {
                    set.add(entry.getKey());
                } else {
                    set.remove(entry.getKey());
                }
            }

            return set;
        }
    }

    @Override
    public Iterator<TypedDataComponent<?>> iterator() {
        if (this.f_315990_.isEmpty()) {
            return this.f_316296_.iterator();
        } else {
            List<TypedDataComponent<?>> list = new ArrayList<>(this.f_315990_.size() + this.f_316296_.m_319491_());

            for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
                this.f_315990_
            )) {
                if (entry.getValue().isPresent()) {
                    list.add(TypedDataComponent.m_321971_(entry.getKey(), entry.getValue().get()));
                }
            }

            for (TypedDataComponent<?> typeddatacomponent : this.f_316296_) {
                if (!this.f_315990_.containsKey(typeddatacomponent.f_316611_())) {
                    list.add(typeddatacomponent);
                }
            }

            return list.iterator();
        }
    }

    @Override
    public int m_319491_() {
        int i = this.f_316296_.m_319491_();

        for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
            this.f_315990_
        )) {
            boolean flag = entry.getValue().isPresent();
            boolean flag1 = this.f_316296_.m_321946_(entry.getKey());
            if (flag != flag1) {
                i += flag ? 1 : -1;
            }
        }

        return i;
    }

    public DataComponentPatch m_320212_() {
        if (this.f_315990_.isEmpty()) {
            return DataComponentPatch.f_315512_;
        } else {
            this.f_316660_ = true;
            return new DataComponentPatch(this.f_315990_);
        }
    }

    public PatchedDataComponentMap m_319920_() {
        this.f_316660_ = true;
        return new PatchedDataComponentMap(this.f_316296_, this.f_315990_, true);
    }

    @Override
    public boolean equals(Object p_335823_) {
        if (this == p_335823_) {
            return true;
        } else {
            if (p_335823_ instanceof PatchedDataComponentMap patcheddatacomponentmap
                && this.f_316296_.equals(patcheddatacomponentmap.f_316296_)
                && this.f_315990_.equals(patcheddatacomponentmap.f_315990_)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.f_316296_.hashCode() + this.f_315990_.hashCode() * 31;
    }

    @Override
    public String toString() {
        return "{" + this.m_322172_().map(TypedDataComponent::toString).collect(Collectors.joining(", ")) + "}";
    }
}