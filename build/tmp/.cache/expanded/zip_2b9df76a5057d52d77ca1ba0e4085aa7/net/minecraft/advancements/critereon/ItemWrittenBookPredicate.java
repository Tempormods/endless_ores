package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

public record ItemWrittenBookPredicate(
    Optional<CollectionPredicate<Filterable<Component>, ItemWrittenBookPredicate.PagePredicate>> f_314561_,
    Optional<String> f_315536_,
    Optional<String> f_314392_,
    MinMaxBounds.Ints f_316125_,
    Optional<Boolean> f_315350_
) implements SingleComponentItemPredicate<WrittenBookContent> {
    public static final Codec<ItemWrittenBookPredicate> f_314892_ = RecordCodecBuilder.create(
        p_330132_ -> p_330132_.group(
                    CollectionPredicate.<Filterable<Component>, ItemWrittenBookPredicate.PagePredicate>m_321514_(
                            ItemWrittenBookPredicate.PagePredicate.f_315088_
                        )
                        .optionalFieldOf("pages")
                        .forGetter(ItemWrittenBookPredicate::f_314561_),
                    Codec.STRING.optionalFieldOf("author").forGetter(ItemWrittenBookPredicate::f_315536_),
                    Codec.STRING.optionalFieldOf("title").forGetter(ItemWrittenBookPredicate::f_314392_),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("generation", MinMaxBounds.Ints.ANY).forGetter(ItemWrittenBookPredicate::f_316125_),
                    Codec.BOOL.optionalFieldOf("resolved").forGetter(ItemWrittenBookPredicate::f_315350_)
                )
                .apply(p_330132_, ItemWrittenBookPredicate::new)
    );

    @Override
    public DataComponentType<WrittenBookContent> m_318698_() {
        return DataComponents.f_315840_;
    }

    public boolean m_318913_(ItemStack p_336266_, WrittenBookContent p_336372_) {
        if (this.f_315536_.isPresent() && !this.f_315536_.get().equals(p_336372_.f_316008_())) {
            return false;
        } else if (this.f_314392_.isPresent() && !this.f_314392_.get().equals(p_336372_.f_316867_().f_315590_())) {
            return false;
        } else if (!this.f_316125_.matches(p_336372_.f_314404_())) {
            return false;
        } else {
            return this.f_315350_.isPresent() && this.f_315350_.get() != p_336372_.f_316486_()
                ? false
                : !this.f_314561_.isPresent() || this.f_314561_.get().test(p_336372_.m_319402_());
        }
    }

    public static record PagePredicate(Component f_316602_) implements Predicate<Filterable<Component>> {
        public static final Codec<ItemWrittenBookPredicate.PagePredicate> f_315088_ = ComponentSerialization.f_303288_
            .xmap(ItemWrittenBookPredicate.PagePredicate::new, ItemWrittenBookPredicate.PagePredicate::f_316602_);

        public boolean test(Filterable<Component> p_327692_) {
            return p_327692_.f_315590_().equals(this.f_316602_);
        }
    }
}