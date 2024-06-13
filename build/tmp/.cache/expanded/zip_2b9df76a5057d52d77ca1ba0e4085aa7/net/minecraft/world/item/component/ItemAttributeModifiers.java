package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record ItemAttributeModifiers(List<ItemAttributeModifiers.Entry> f_314826_, boolean f_315588_) {
    public static final ItemAttributeModifiers f_314473_ = new ItemAttributeModifiers(List.of(), true);
    private static final Codec<ItemAttributeModifiers> f_317123_ = RecordCodecBuilder.create(
        p_333950_ -> p_333950_.group(
                    ItemAttributeModifiers.Entry.f_316349_.listOf().fieldOf("modifiers").forGetter(ItemAttributeModifiers::f_314826_),
                    Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(ItemAttributeModifiers::f_315588_)
                )
                .apply(p_333950_, ItemAttributeModifiers::new)
    );
    public static final Codec<ItemAttributeModifiers> f_316595_ = Codec.withAlternative(
        f_317123_, ItemAttributeModifiers.Entry.f_316349_.listOf(), p_331984_ -> new ItemAttributeModifiers(p_331984_, true)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> f_314793_ = StreamCodec.m_320349_(
        ItemAttributeModifiers.Entry.f_315537_.m_321801_(ByteBufCodecs.m_324765_()),
        ItemAttributeModifiers::f_314826_,
        ByteBufCodecs.f_315514_,
        ItemAttributeModifiers::f_315588_,
        ItemAttributeModifiers::new
    );
    public static final DecimalFormat f_315079_ = Util.make(
        new DecimalFormat("#.##"), p_334862_ -> p_334862_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
    );

    public ItemAttributeModifiers m_323423_(boolean p_332852_) {
        return new ItemAttributeModifiers(this.f_314826_, p_332852_);
    }

    public static ItemAttributeModifiers.Builder m_324327_() {
        return new ItemAttributeModifiers.Builder();
    }

    public ItemAttributeModifiers m_320732_(Holder<Attribute> p_335092_, AttributeModifier p_327974_, EquipmentSlotGroup p_328449_) {
        ImmutableList.Builder<ItemAttributeModifiers.Entry> builder = ImmutableList.builderWithExpectedSize(this.f_314826_.size() + 1);

        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : this.f_314826_) {
            if (!itemattributemodifiers$entry.f_316263_.id().equals(p_327974_.id())) {
                builder.add(itemattributemodifiers$entry);
            }
        }

        builder.add(new ItemAttributeModifiers.Entry(p_335092_, p_327974_, p_328449_));
        return new ItemAttributeModifiers(builder.build(), this.f_315588_);
    }

    public void m_322073_(EquipmentSlot p_334753_, BiConsumer<Holder<Attribute>, AttributeModifier> p_331767_) {
        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : this.f_314826_) {
            if (itemattributemodifiers$entry.f_317045_.m_318881_(p_334753_)) {
                p_331767_.accept(itemattributemodifiers$entry.f_316116_, itemattributemodifiers$entry.f_316263_);
            }
        }
    }

    public double m_324178_(double p_332865_, EquipmentSlot p_329615_) {
        double d0 = p_332865_;

        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : this.f_314826_) {
            if (itemattributemodifiers$entry.f_317045_.m_318881_(p_329615_)) {
                double d1 = itemattributemodifiers$entry.f_316263_.amount();

                d0 += switch (itemattributemodifiers$entry.f_316263_.operation()) {
                    case ADD_VALUE -> d1;
                    case ADD_MULTIPLIED_BASE -> d1 * p_332865_;
                    case ADD_MULTIPLIED_TOTAL -> d1 * d0;
                };
            }
        }

        return d0;
    }

    public static class Builder {
        private final ImmutableList.Builder<ItemAttributeModifiers.Entry> f_316066_ = ImmutableList.builder();

        Builder() {
        }

        public ItemAttributeModifiers.Builder m_324947_(Holder<Attribute> p_330104_, AttributeModifier p_333549_, EquipmentSlotGroup p_332621_) {
            this.f_316066_.add(new ItemAttributeModifiers.Entry(p_330104_, p_333549_, p_332621_));
            return this;
        }

        public ItemAttributeModifiers m_320246_() {
            return new ItemAttributeModifiers(this.f_316066_.build(), true);
        }
    }

    public static record Entry(Holder<Attribute> f_316116_, AttributeModifier f_316263_, EquipmentSlotGroup f_317045_) {
        public static final Codec<ItemAttributeModifiers.Entry> f_316349_ = RecordCodecBuilder.create(
            p_327889_ -> p_327889_.group(
                        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("type").forGetter(ItemAttributeModifiers.Entry::f_316116_),
                        AttributeModifier.f_316599_.forGetter(ItemAttributeModifiers.Entry::f_316263_),
                        EquipmentSlotGroup.f_315768_.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(ItemAttributeModifiers.Entry::f_317045_)
                    )
                    .apply(p_327889_, ItemAttributeModifiers.Entry::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers.Entry> f_315537_ = StreamCodec.m_321516_(
            ByteBufCodecs.m_322636_(Registries.ATTRIBUTE),
            ItemAttributeModifiers.Entry::f_316116_,
            AttributeModifier.f_315334_,
            ItemAttributeModifiers.Entry::f_316263_,
            EquipmentSlotGroup.f_316872_,
            ItemAttributeModifiers.Entry::f_317045_,
            ItemAttributeModifiers.Entry::new
        );
    }
}