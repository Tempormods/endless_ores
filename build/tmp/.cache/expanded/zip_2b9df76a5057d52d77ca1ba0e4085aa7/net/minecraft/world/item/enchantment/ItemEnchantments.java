package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public class ItemEnchantments implements TooltipProvider {
    public static final ItemEnchantments f_314789_ = new ItemEnchantments(new Object2IntOpenHashMap<>(), true);
    public static final int f_314187_ = 255;
    private static final Codec<Integer> f_314123_ = Codec.intRange(0, 255);
    private static final Codec<Object2IntOpenHashMap<Holder<Enchantment>>> f_316166_ = Codec.unboundedMap(BuiltInRegistries.ENCHANTMENT.holderByNameCodec(), f_314123_)
        .xmap(Object2IntOpenHashMap::new, Function.identity());
    private static final Codec<ItemEnchantments> f_314760_ = RecordCodecBuilder.create(
        p_330315_ -> p_330315_.group(
                    f_316166_.fieldOf("levels").forGetter(p_334450_ -> p_334450_.f_315809_),
                    Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(p_330292_ -> p_330292_.f_316957_)
                )
                .apply(p_330315_, ItemEnchantments::new)
    );
    public static final Codec<ItemEnchantments> f_315579_ = Codec.withAlternative(f_314760_, f_316166_, p_330983_ -> new ItemEnchantments(p_330983_, true));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantments> f_316523_ = StreamCodec.m_320349_(
        ByteBufCodecs.m_322136_(Object2IntOpenHashMap::new, ByteBufCodecs.m_322636_(Registries.ENCHANTMENT), ByteBufCodecs.f_316730_),
        p_334569_ -> p_334569_.f_315809_,
        ByteBufCodecs.f_315514_,
        p_328412_ -> p_328412_.f_316957_,
        ItemEnchantments::new
    );
    final Object2IntOpenHashMap<Holder<Enchantment>> f_315809_;
    final boolean f_316957_;

    ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>> p_329796_, boolean p_331323_) {
        this.f_315809_ = p_329796_;
        this.f_316957_ = p_331323_;

        for (Entry<Holder<Enchantment>> entry : p_329796_.object2IntEntrySet()) {
            int i = entry.getIntValue();
            if (i < 0 || i > 255) {
                throw new IllegalArgumentException("Enchantment " + entry.getKey() + " has invalid level " + i);
            }
        }
    }

    public int m_320299_(Enchantment p_334964_) {
        return this.f_315809_.getInt(p_334964_.builtInRegistryHolder());
    }

    @Override
    public void m_319025_(Item.TooltipContext p_332503_, Consumer<Component> p_333731_, TooltipFlag p_332196_) {
        if (this.f_316957_) {
            HolderLookup.Provider holderlookup$provider = p_332503_.m_320287_();
            HolderSet<Enchantment> holderset = m_318888_(holderlookup$provider, Registries.ENCHANTMENT, EnchantmentTags.f_315440_);

            for (Holder<Enchantment> holder : holderset) {
                int i = this.f_315809_.getInt(holder);
                if (i > 0) {
                    p_333731_.accept(holder.value().getFullname(i));
                }
            }

            for (Entry<Holder<Enchantment>> entry : this.f_315809_.object2IntEntrySet()) {
                Holder<Enchantment> holder1 = entry.getKey();
                if (!holderset.contains(holder1)) {
                    p_333731_.accept(holder1.value().getFullname(entry.getIntValue()));
                }
            }
        }
    }

    private static <T> HolderSet<T> m_318888_(@Nullable HolderLookup.Provider p_327799_, ResourceKey<Registry<T>> p_330565_, TagKey<T> p_327764_) {
        if (p_327799_ != null) {
            Optional<HolderSet.Named<T>> optional = p_327799_.lookupOrThrow(p_330565_).m_255050_(p_327764_);
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return HolderSet.direct();
    }

    public ItemEnchantments m_323674_(boolean p_333031_) {
        return new ItemEnchantments(this.f_315809_, p_333031_);
    }

    public Set<Holder<Enchantment>> m_324420_() {
        return Collections.unmodifiableSet(this.f_315809_.keySet());
    }

    public Set<Entry<Holder<Enchantment>>> m_320130_() {
        return Collections.unmodifiableSet(this.f_315809_.object2IntEntrySet());
    }

    public int m_322852_() {
        return this.f_315809_.size();
    }

    public boolean m_324000_() {
        return this.f_315809_.isEmpty();
    }

    @Override
    public boolean equals(Object p_328229_) {
        if (this == p_328229_) {
            return true;
        } else {
            return !(p_328229_ instanceof ItemEnchantments itemenchantments)
                ? false
                : this.f_316957_ == itemenchantments.f_316957_ && this.f_315809_.equals(itemenchantments.f_315809_);
        }
    }

    @Override
    public int hashCode() {
        int i = this.f_315809_.hashCode();
        return 31 * i + (this.f_316957_ ? 1 : 0);
    }

    @Override
    public String toString() {
        return "ItemEnchantments{enchantments=" + this.f_315809_ + ", showInTooltip=" + this.f_316957_ + "}";
    }

    public static class Mutable {
        private final Object2IntOpenHashMap<Holder<Enchantment>> f_314155_ = new Object2IntOpenHashMap<>();
        private final boolean f_314097_;

        public Mutable(ItemEnchantments p_328128_) {
            this.f_314155_.putAll(p_328128_.f_315809_);
            this.f_314097_ = p_328128_.f_316957_;
        }

        public void m_319152_(Enchantment p_334196_, int p_330613_) {
            if (p_330613_ <= 0) {
                this.f_314155_.removeInt(p_334196_.builtInRegistryHolder());
            } else {
                this.f_314155_.put(p_334196_.builtInRegistryHolder(), Math.min(p_330613_, 255));
            }
        }

        public void m_323014_(Enchantment p_328282_, int p_332549_) {
            if (p_332549_ > 0) {
                this.f_314155_.merge(p_328282_.builtInRegistryHolder(), Math.min(p_332549_, 255), Integer::max);
            }
        }

        public void m_319910_(Predicate<Holder<Enchantment>> p_330896_) {
            this.f_314155_.keySet().removeIf(p_330896_);
        }

        public int m_319403_(Enchantment p_330501_) {
            return this.f_314155_.getOrDefault(p_330501_.builtInRegistryHolder(), 0);
        }

        public Set<Holder<Enchantment>> m_318718_() {
            return this.f_314155_.keySet();
        }

        public ItemEnchantments m_321565_() {
            return new ItemEnchantments(this.f_314155_, this.f_314097_);
        }
    }
}