package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MatchTool(Optional<ItemPredicate> predicate) implements LootItemCondition {
    public static final MapCodec<MatchTool> CODEC = RecordCodecBuilder.mapCodec(
        p_327654_ -> p_327654_.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchTool::predicate)).apply(p_327654_, MatchTool::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.MATCH_TOOL;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext pContext) {
        ItemStack itemstack = pContext.getParamOrNull(LootContextParams.TOOL);
        return itemstack != null && (this.predicate.isEmpty() || this.predicate.get().test(itemstack));
    }

    public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder pToolPredicateBuilder) {
        return () -> new MatchTool(Optional.of(pToolPredicateBuilder.build()));
    }
}