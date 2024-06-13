package net.minecraft.world.item.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class ItemContainerContents {
    private static final int f_315580_ = -1;
    private static final int f_316430_ = 256;
    public static final ItemContainerContents f_316619_ = new ItemContainerContents(NonNullList.create());
    public static final Codec<ItemContainerContents> f_315263_ = ItemContainerContents.Slot.f_314136_
        .sizeLimitedListOf(256)
        .xmap(ItemContainerContents::m_323974_, ItemContainerContents::m_323947_);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainerContents> f_315529_ = ItemStack.f_314979_
        .m_321801_(ByteBufCodecs.m_319259_(256))
        .m_323038_(ItemContainerContents::new, p_333580_ -> p_333580_.f_315560_);
    private final NonNullList<ItemStack> f_315560_;
    private final int f_316776_;

    private ItemContainerContents(NonNullList<ItemStack> p_334672_) {
        if (p_334672_.size() > 256) {
            throw new IllegalArgumentException("Got " + p_334672_.size() + " items, but maximum is 256");
        } else {
            this.f_315560_ = p_334672_;
            this.f_316776_ = ItemStack.m_318747_(p_334672_);
        }
    }

    private ItemContainerContents(int p_336350_) {
        this(NonNullList.withSize(p_336350_, ItemStack.EMPTY));
    }

    private ItemContainerContents(List<ItemStack> p_332487_) {
        this(p_332487_.size());

        for (int i = 0; i < p_332487_.size(); i++) {
            this.f_315560_.set(i, p_332487_.get(i));
        }
    }

    private static ItemContainerContents m_323974_(List<ItemContainerContents.Slot> p_334537_) {
        OptionalInt optionalint = p_334537_.stream().mapToInt(ItemContainerContents.Slot::f_315231_).max();
        if (optionalint.isEmpty()) {
            return f_316619_;
        } else {
            ItemContainerContents itemcontainercontents = new ItemContainerContents(optionalint.getAsInt() + 1);

            for (ItemContainerContents.Slot itemcontainercontents$slot : p_334537_) {
                itemcontainercontents.f_315560_.set(itemcontainercontents$slot.f_315231_(), itemcontainercontents$slot.f_317103_());
            }

            return itemcontainercontents;
        }
    }

    public static ItemContainerContents m_320241_(List<ItemStack> p_329219_) {
        int i = m_321762_(p_329219_);
        if (i == -1) {
            return f_316619_;
        } else {
            ItemContainerContents itemcontainercontents = new ItemContainerContents(i + 1);

            for (int j = 0; j <= i; j++) {
                itemcontainercontents.f_315560_.set(j, p_329219_.get(j).copy());
            }

            return itemcontainercontents;
        }
    }

    private static int m_321762_(List<ItemStack> p_332919_) {
        for (int i = p_332919_.size() - 1; i >= 0; i--) {
            if (!p_332919_.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private List<ItemContainerContents.Slot> m_323947_() {
        List<ItemContainerContents.Slot> list = new ArrayList<>();

        for (int i = 0; i < this.f_315560_.size(); i++) {
            ItemStack itemstack = this.f_315560_.get(i);
            if (!itemstack.isEmpty()) {
                list.add(new ItemContainerContents.Slot(i, itemstack));
            }
        }

        return list;
    }

    public void m_322022_(NonNullList<ItemStack> p_333460_) {
        for (int i = 0; i < p_333460_.size(); i++) {
            ItemStack itemstack = i < this.f_315560_.size() ? this.f_315560_.get(i) : ItemStack.EMPTY;
            p_333460_.set(i, itemstack.copy());
        }
    }

    public ItemStack m_322549_() {
        return this.f_315560_.isEmpty() ? ItemStack.EMPTY : this.f_315560_.get(0).copy();
    }

    public Stream<ItemStack> m_324244_() {
        return this.f_315560_.stream().map(ItemStack::copy);
    }

    public Stream<ItemStack> m_320494_() {
        return this.f_315560_.stream().filter(p_332163_ -> !p_332163_.isEmpty()).map(ItemStack::copy);
    }

    public Iterable<ItemStack> m_318832_() {
        return Iterables.filter(this.f_315560_, p_330818_ -> !p_330818_.isEmpty());
    }

    public Iterable<ItemStack> m_324961_() {
        return Iterables.transform(this.m_318832_(), ItemStack::copy);
    }

    @Override
    public boolean equals(Object p_331196_) {
        if (this == p_331196_) {
            return true;
        } else {
            if (p_331196_ instanceof ItemContainerContents itemcontainercontents && ItemStack.m_319597_(this.f_315560_, itemcontainercontents.f_315560_)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.f_316776_;
    }

    static record Slot(int f_315231_, ItemStack f_317103_) {
        public static final Codec<ItemContainerContents.Slot> f_314136_ = RecordCodecBuilder.create(
            p_327964_ -> p_327964_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(ItemContainerContents.Slot::f_315231_),
                        ItemStack.CODEC.fieldOf("item").forGetter(ItemContainerContents.Slot::f_317103_)
                    )
                    .apply(p_327964_, ItemContainerContents.Slot::new)
        );
    }
}