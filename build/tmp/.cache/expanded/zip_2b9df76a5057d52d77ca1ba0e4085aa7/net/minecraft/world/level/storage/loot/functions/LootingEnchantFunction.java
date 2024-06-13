package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * LootItemFunction that grows the stack's count by multiplying the {@linkplain LootContextParams#KILLER_ENTITY
 * killer}'s looting enchantment level with some multiplier. Optionally a limit to the stack size is applied.
 */
public class LootingEnchantFunction extends LootItemConditionalFunction {
    public static final int NO_LIMIT = 0;
    public static final MapCodec<LootingEnchantFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_327577_ -> commonFields(p_327577_)
                .and(
                    p_327577_.group(
                        NumberProviders.CODEC.fieldOf("count").forGetter(p_300767_ -> p_300767_.value),
                        Codec.INT.optionalFieldOf("limit", Integer.valueOf(0)).forGetter(p_301305_ -> p_301305_.limit)
                    )
                )
                .apply(p_327577_, LootingEnchantFunction::new)
    );
    private final NumberProvider value;
    private final int limit;

    LootingEnchantFunction(List<LootItemCondition> p_299292_, NumberProvider p_165227_, int p_165228_) {
        super(p_299292_);
        this.value = p_165227_;
        this.limit = p_165228_;
    }

    @Override
    public LootItemFunctionType<LootingEnchantFunction> getType() {
        return LootItemFunctions.LOOTING_ENCHANT;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(ImmutableSet.of(LootContextParams.KILLER_ENTITY), this.value.getReferencedContextParams());
    }

    private boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        Entity entity = pContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (entity instanceof LivingEntity) {
            int i = pContext.getLootingModifier();
            if (i == 0) {
                return pStack;
            }

            float f = (float)i * this.value.getFloat(pContext);
            pStack.grow(Math.round(f));
            if (this.hasLimit()) {
                pStack.m_324521_(this.limit);
            }
        }

        return pStack;
    }

    public static LootingEnchantFunction.Builder lootingMultiplier(NumberProvider pLootingMultiplier) {
        return new LootingEnchantFunction.Builder(pLootingMultiplier);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<LootingEnchantFunction.Builder> {
        private final NumberProvider count;
        private int limit = 0;

        public Builder(NumberProvider pLootingMultiplier) {
            this.count = pLootingMultiplier;
        }

        protected LootingEnchantFunction.Builder getThis() {
            return this;
        }

        public LootingEnchantFunction.Builder setLimit(int pLimit) {
            this.limit = pLimit;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
        }
    }
}
