package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ItemAttributeModifiersPredicate(
    Optional<CollectionPredicate<ItemAttributeModifiers.Entry, ItemAttributeModifiersPredicate.EntryPredicate>> f_315820_
) implements SingleComponentItemPredicate<ItemAttributeModifiers> {
    public static final Codec<ItemAttributeModifiersPredicate> f_315051_ = RecordCodecBuilder.create(
        p_334039_ -> p_334039_.group(
                    CollectionPredicate.<ItemAttributeModifiers.Entry, ItemAttributeModifiersPredicate.EntryPredicate>m_321514_(
                            ItemAttributeModifiersPredicate.EntryPredicate.f_313929_
                        )
                        .optionalFieldOf("modifiers")
                        .forGetter(ItemAttributeModifiersPredicate::f_315820_)
                )
                .apply(p_334039_, ItemAttributeModifiersPredicate::new)
    );

    @Override
    public DataComponentType<ItemAttributeModifiers> m_318698_() {
        return DataComponents.f_316119_;
    }

    public boolean m_318913_(ItemStack p_331791_, ItemAttributeModifiers p_328619_) {
        return !this.f_315820_.isPresent() || this.f_315820_.get().test(p_328619_.f_314826_());
    }

    public static record EntryPredicate(
        Optional<HolderSet<Attribute>> f_315176_,
        Optional<UUID> f_315360_,
        Optional<String> f_314682_,
        MinMaxBounds.Doubles f_316857_,
        Optional<AttributeModifier.Operation> f_315016_,
        Optional<EquipmentSlotGroup> f_314217_
    ) implements Predicate<ItemAttributeModifiers.Entry> {
        public static final Codec<ItemAttributeModifiersPredicate.EntryPredicate> f_313929_ = RecordCodecBuilder.create(
            p_328644_ -> p_328644_.group(
                        RegistryCodecs.homogeneousList(Registries.ATTRIBUTE)
                            .optionalFieldOf("attribute")
                            .forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_315176_),
                        UUIDUtil.f_302497_.optionalFieldOf("uuid").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_315360_),
                        Codec.STRING.optionalFieldOf("name").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_314682_),
                        MinMaxBounds.Doubles.CODEC
                            .optionalFieldOf("amount", MinMaxBounds.Doubles.ANY)
                            .forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_316857_),
                        AttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_315016_),
                        EquipmentSlotGroup.f_315768_.optionalFieldOf("slot").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::f_314217_)
                    )
                    .apply(p_328644_, ItemAttributeModifiersPredicate.EntryPredicate::new)
        );

        public boolean test(ItemAttributeModifiers.Entry p_332302_) {
            if (this.f_315176_.isPresent() && !this.f_315176_.get().contains(p_332302_.f_316116_())) {
                return false;
            } else if (this.f_315360_.isPresent() && !this.f_315360_.get().equals(p_332302_.f_316263_().id())) {
                return false;
            } else if (this.f_314682_.isPresent() && !this.f_314682_.get().equals(p_332302_.f_316263_().f_303575_())) {
                return false;
            } else if (!this.f_316857_.matches(p_332302_.f_316263_().amount())) {
                return false;
            } else {
                return this.f_315016_.isPresent() && this.f_315016_.get() != p_332302_.f_316263_().operation()
                    ? false
                    : !this.f_314217_.isPresent() || this.f_314217_.get() == p_332302_.f_317045_();
            }
        }
    }
}