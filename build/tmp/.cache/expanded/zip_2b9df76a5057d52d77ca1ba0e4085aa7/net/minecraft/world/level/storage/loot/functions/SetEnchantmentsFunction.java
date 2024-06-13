package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * LootItemFunction that sets a stack's enchantments. If {@code add} is set, will add to any already existing
 * enchantment levels instead of replacing them (ignored for enchanted books).
 */
public class SetEnchantmentsFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetEnchantmentsFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_327606_ -> commonFields(p_327606_)
                .and(
                    p_327606_.group(
                        Codec.unboundedMap(BuiltInRegistries.ENCHANTMENT.holderByNameCodec(), NumberProviders.CODEC)
                            .optionalFieldOf("enchantments", Map.of())
                            .forGetter(p_297131_ -> p_297131_.enchantments),
                        Codec.BOOL.fieldOf("add").orElse(false).forGetter(p_297132_ -> p_297132_.add)
                    )
                )
                .apply(p_327606_, SetEnchantmentsFunction::new)
    );
    private final Map<Holder<Enchantment>, NumberProvider> enchantments;
    private final boolean add;

    SetEnchantmentsFunction(List<LootItemCondition> p_300544_, Map<Holder<Enchantment>, NumberProvider> p_165338_, boolean p_165339_) {
        super(p_300544_);
        this.enchantments = Map.copyOf(p_165338_);
        this.add = p_165339_;
    }

    @Override
    public LootItemFunctionType<SetEnchantmentsFunction> getType() {
        return LootItemFunctions.SET_ENCHANTMENTS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.enchantments.values().stream().flatMap(p_279081_ -> p_279081_.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        Object2IntMap<Enchantment> object2intmap = new Object2IntOpenHashMap<>();
        this.enchantments.forEach((p_327609_, p_327610_) -> object2intmap.put(p_327609_.value(), Mth.clamp(p_327610_.getInt(pContext), 0, 255)));
        if (pStack.is(Items.BOOK)) {
            pStack = pStack.m_319323_(Items.ENCHANTED_BOOK, pStack.getCount());
            pStack.m_322496_(DataComponents.f_314515_, pStack.m_319322_(DataComponents.f_314658_));
        }

        EnchantmentHelper.m_320959_(pStack, p_327602_ -> {
            if (this.add) {
                object2intmap.forEach((p_327604_, p_327605_) -> p_327602_.m_319152_(p_327604_, p_327602_.m_319403_(p_327604_) + p_327605_));
            } else {
                object2intmap.forEach(p_327602_::m_319152_);
            }
        });
        return pStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetEnchantmentsFunction.Builder> {
        private final ImmutableMap.Builder<Holder<Enchantment>, NumberProvider> enchantments = ImmutableMap.builder();
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean pAdd) {
            this.add = pAdd;
        }

        protected SetEnchantmentsFunction.Builder getThis() {
            return this;
        }

        public SetEnchantmentsFunction.Builder withEnchantment(Enchantment pEnchantment, NumberProvider pLevelProvider) {
            this.enchantments.put(pEnchantment.builtInRegistryHolder(), pLevelProvider);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetEnchantmentsFunction(this.getConditions(), this.enchantments.build(), this.add);
        }
    }
}