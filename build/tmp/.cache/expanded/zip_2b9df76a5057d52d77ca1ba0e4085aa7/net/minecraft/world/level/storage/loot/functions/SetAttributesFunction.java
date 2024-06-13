package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * LootItemFunction that adds a list of attribute modifiers to the stacks.
 */
public class SetAttributesFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetAttributesFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_327583_ -> commonFields(p_327583_)
                .and(
                    p_327583_.group(
                        ExtraCodecs.nonEmptyList(SetAttributesFunction.Modifier.CODEC.listOf())
                            .fieldOf("modifiers")
                            .forGetter(p_297111_ -> p_297111_.modifiers),
                        Codec.BOOL.optionalFieldOf("replace", Boolean.valueOf(true)).forGetter(p_327579_ -> p_327579_.f_314132_)
                    )
                )
                .apply(p_327583_, SetAttributesFunction::new)
    );
    private final List<SetAttributesFunction.Modifier> modifiers;
    private final boolean f_314132_;

    SetAttributesFunction(List<LootItemCondition> p_80834_, List<SetAttributesFunction.Modifier> p_298826_, boolean p_336168_) {
        super(p_80834_);
        this.modifiers = List.copyOf(p_298826_);
        this.f_314132_ = p_336168_;
    }

    @Override
    public LootItemFunctionType<SetAttributesFunction> getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.modifiers.stream().flatMap(p_279080_ -> p_279080_.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        if (this.f_314132_) {
            pStack.m_322496_(DataComponents.f_316119_, this.m_322651_(pContext, ItemAttributeModifiers.f_314473_));
        } else {
            pStack.m_322591_(
                DataComponents.f_316119_,
                ItemAttributeModifiers.f_314473_,
                p_327582_ -> p_327582_.f_314826_().isEmpty() ? this.m_322651_(pContext, pStack.getItem().getDefaultAttributeModifiers()) : this.m_322651_(pContext, p_327582_)
            );
        }

        return pStack;
    }

    private ItemAttributeModifiers m_322651_(LootContext p_331587_, ItemAttributeModifiers p_333868_) {
        RandomSource randomsource = p_331587_.getRandom();

        for (SetAttributesFunction.Modifier setattributesfunction$modifier : this.modifiers) {
            UUID uuid = setattributesfunction$modifier.id.orElseGet(UUID::randomUUID);
            EquipmentSlotGroup equipmentslotgroup = Util.getRandom(setattributesfunction$modifier.slots, randomsource);
            p_333868_ = p_333868_.m_320732_(
                setattributesfunction$modifier.attribute,
                new AttributeModifier(
                    uuid,
                    setattributesfunction$modifier.name,
                    (double)setattributesfunction$modifier.amount.getFloat(p_331587_),
                    setattributesfunction$modifier.operation
                ),
                equipmentslotgroup
            );
        }

        return p_333868_;
    }

    public static SetAttributesFunction.ModifierBuilder modifier(
        String pName, Holder<Attribute> pAttribute, AttributeModifier.Operation pOperation, NumberProvider pAmount
    ) {
        return new SetAttributesFunction.ModifierBuilder(pName, pAttribute, pOperation, pAmount);
    }

    public static SetAttributesFunction.Builder setAttributes() {
        return new SetAttributesFunction.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetAttributesFunction.Builder> {
        private final boolean f_316621_;
        private final List<SetAttributesFunction.Modifier> modifiers = Lists.newArrayList();

        public Builder(boolean p_330119_) {
            this.f_316621_ = p_330119_;
        }

        public Builder() {
            this(false);
        }

        protected SetAttributesFunction.Builder getThis() {
            return this;
        }

        public SetAttributesFunction.Builder withModifier(SetAttributesFunction.ModifierBuilder pModifierBuilder) {
            this.modifiers.add(pModifierBuilder.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetAttributesFunction(this.getConditions(), this.modifiers, this.f_316621_);
        }
    }

    static record Modifier(
        String name,
        Holder<Attribute> attribute,
        AttributeModifier.Operation operation,
        NumberProvider amount,
        List<EquipmentSlotGroup> slots,
        Optional<UUID> id
    ) {
        private static final Codec<List<EquipmentSlotGroup>> SLOTS_CODEC = ExtraCodecs.nonEmptyList(
            Codec.either(EquipmentSlotGroup.f_315768_, EquipmentSlotGroup.f_315768_.listOf())
                .xmap(
                    p_298787_ -> p_298787_.map(List::of, Function.identity()),
                    p_327584_ -> p_327584_.size() == 1 ? Either.left(p_327584_.getFirst()) : Either.right((List<EquipmentSlotGroup>)p_327584_)
                )
        );
        public static final Codec<SetAttributesFunction.Modifier> CODEC = RecordCodecBuilder.create(
            p_327585_ -> p_327585_.group(
                        Codec.STRING.fieldOf("name").forGetter(SetAttributesFunction.Modifier::name),
                        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(SetAttributesFunction.Modifier::attribute),
                        AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesFunction.Modifier::operation),
                        NumberProviders.CODEC.fieldOf("amount").forGetter(SetAttributesFunction.Modifier::amount),
                        SLOTS_CODEC.fieldOf("slot").forGetter(SetAttributesFunction.Modifier::slots),
                        UUIDUtil.STRING_CODEC.optionalFieldOf("id").forGetter(SetAttributesFunction.Modifier::id)
                    )
                    .apply(p_327585_, SetAttributesFunction.Modifier::new)
        );
    }

    public static class ModifierBuilder {
        private final String name;
        private final Holder<Attribute> attribute;
        private final AttributeModifier.Operation operation;
        private final NumberProvider amount;
        private Optional<UUID> id = Optional.empty();
        private final Set<EquipmentSlotGroup> slots = EnumSet.noneOf(EquipmentSlotGroup.class);

        public ModifierBuilder(String pName, Holder<Attribute> pAttribute, AttributeModifier.Operation pOperation, NumberProvider pAmount) {
            this.name = pName;
            this.attribute = pAttribute;
            this.operation = pOperation;
            this.amount = pAmount;
        }

        public SetAttributesFunction.ModifierBuilder forSlot(EquipmentSlotGroup p_333921_) {
            this.slots.add(p_333921_);
            return this;
        }

        public SetAttributesFunction.ModifierBuilder withUuid(UUID pId) {
            this.id = Optional.of(pId);
            return this;
        }

        public SetAttributesFunction.Modifier build() {
            return new SetAttributesFunction.Modifier(
                this.name, this.attribute, this.operation, this.amount, List.copyOf(this.slots), this.id
            );
        }
    }
}