package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;

public record ItemFireworkExplosionPredicate(ItemFireworkExplosionPredicate.FireworkPredicate f_315606_)
    implements SingleComponentItemPredicate<FireworkExplosion> {
    public static final Codec<ItemFireworkExplosionPredicate> f_314276_ = ItemFireworkExplosionPredicate.FireworkPredicate.f_316939_
        .xmap(ItemFireworkExplosionPredicate::new, ItemFireworkExplosionPredicate::f_315606_);

    @Override
    public DataComponentType<FireworkExplosion> m_318698_() {
        return DataComponents.f_315608_;
    }

    public boolean m_318913_(ItemStack p_327775_, FireworkExplosion p_332956_) {
        return this.f_315606_.test(p_332956_);
    }

    public static record FireworkPredicate(Optional<FireworkExplosion.Shape> f_315216_, Optional<Boolean> f_315735_, Optional<Boolean> f_316972_)
        implements Predicate<FireworkExplosion> {
        public static final Codec<ItemFireworkExplosionPredicate.FireworkPredicate> f_316939_ = RecordCodecBuilder.create(
            p_335267_ -> p_335267_.group(
                        FireworkExplosion.Shape.f_314578_.optionalFieldOf("shape").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::f_315216_),
                        Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::f_315735_),
                        Codec.BOOL.optionalFieldOf("has_trail").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::f_316972_)
                    )
                    .apply(p_335267_, ItemFireworkExplosionPredicate.FireworkPredicate::new)
        );

        public boolean test(FireworkExplosion p_330464_) {
            if (this.f_315216_.isPresent() && this.f_315216_.get() != p_330464_.f_316547_()) {
                return false;
            } else {
                return this.f_315735_.isPresent() && this.f_315735_.get() != p_330464_.f_316285_()
                    ? false
                    : !this.f_316972_.isPresent() || this.f_316972_.get() == p_330464_.f_316522_();
            }
        }
    }
}