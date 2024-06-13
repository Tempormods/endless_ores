package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

public record ItemWritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, ItemWritableBookPredicate.PagePredicate>> f_317004_)
    implements SingleComponentItemPredicate<WritableBookContent> {
    public static final Codec<ItemWritableBookPredicate> f_316382_ = RecordCodecBuilder.create(
        p_328529_ -> p_328529_.group(
                    CollectionPredicate.<Filterable<String>, ItemWritableBookPredicate.PagePredicate>m_321514_(
                            ItemWritableBookPredicate.PagePredicate.f_314632_
                        )
                        .optionalFieldOf("pages")
                        .forGetter(ItemWritableBookPredicate::f_317004_)
                )
                .apply(p_328529_, ItemWritableBookPredicate::new)
    );

    @Override
    public DataComponentType<WritableBookContent> m_318698_() {
        return DataComponents.f_314472_;
    }

    public boolean m_318913_(ItemStack p_335022_, WritableBookContent p_331059_) {
        return !this.f_317004_.isPresent() || this.f_317004_.get().test(p_331059_.m_319402_());
    }

    public static record PagePredicate(String f_316074_) implements Predicate<Filterable<String>> {
        public static final Codec<ItemWritableBookPredicate.PagePredicate> f_314632_ = Codec.STRING
            .xmap(ItemWritableBookPredicate.PagePredicate::new, ItemWritableBookPredicate.PagePredicate::f_316074_);

        public boolean test(Filterable<String> p_327840_) {
            return p_327840_.f_315590_().equals(this.f_316074_);
        }
    }
}