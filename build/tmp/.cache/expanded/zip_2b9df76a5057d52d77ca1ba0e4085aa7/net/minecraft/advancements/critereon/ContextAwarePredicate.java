package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ContextAwarePredicate {
    public static final Codec<ContextAwarePredicate> f_303282_ = LootItemConditions.f_317075_
        .listOf()
        .xmap(ContextAwarePredicate::new, p_309450_ -> p_309450_.conditions);
    private final List<LootItemCondition> conditions;
    private final Predicate<LootContext> compositePredicates;

    ContextAwarePredicate(List<LootItemCondition> pConditions) {
        this.conditions = pConditions;
        this.compositePredicates = Util.m_322468_(pConditions);
    }

    public static ContextAwarePredicate create(LootItemCondition... pConditions) {
        return new ContextAwarePredicate(List.of(pConditions));
    }

    public boolean matches(LootContext pContext) {
        return this.compositePredicates.test(pContext);
    }

    public void m_305566_(ValidationContext p_309801_) {
        for (int i = 0; i < this.conditions.size(); i++) {
            LootItemCondition lootitemcondition = this.conditions.get(i);
            lootitemcondition.validate(p_309801_.forChild("[" + i + "]"));
        }
    }
}