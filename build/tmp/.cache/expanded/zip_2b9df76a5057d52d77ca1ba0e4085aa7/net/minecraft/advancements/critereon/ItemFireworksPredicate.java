package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public record ItemFireworksPredicate(
    Optional<CollectionPredicate<FireworkExplosion, ItemFireworkExplosionPredicate.FireworkPredicate>> f_315848_, MinMaxBounds.Ints f_316539_
) implements SingleComponentItemPredicate<Fireworks> {
    public static final Codec<ItemFireworksPredicate> f_315729_ = RecordCodecBuilder.create(
        p_333914_ -> p_333914_.group(
                    CollectionPredicate.<FireworkExplosion, ItemFireworkExplosionPredicate.FireworkPredicate>m_321514_(
                            ItemFireworkExplosionPredicate.FireworkPredicate.f_316939_
                        )
                        .optionalFieldOf("explosions")
                        .forGetter(ItemFireworksPredicate::f_315848_),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("flight_duration", MinMaxBounds.Ints.ANY).forGetter(ItemFireworksPredicate::f_316539_)
                )
                .apply(p_333914_, ItemFireworksPredicate::new)
    );

    @Override
    public DataComponentType<Fireworks> m_318698_() {
        return DataComponents.f_316632_;
    }

    public boolean m_318913_(ItemStack p_334969_, Fireworks p_332444_) {
        return this.f_315848_.isPresent() && !this.f_315848_.get().test(p_332444_.f_314926_()) ? false : this.f_316539_.matches(p_332444_.f_317050_());
    }
}