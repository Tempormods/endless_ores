package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
    @Override
    public Codec<LootTableTrigger.TriggerInstance> createInstance() {
        return LootTableTrigger.TriggerInstance.f_303158_;
    }

    public void trigger(ServerPlayer pPlayer, ResourceKey<LootTable> p_332064_) {
        this.trigger(pPlayer, p_325231_ -> p_325231_.matches(p_332064_));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_302659_, ResourceKey<LootTable> lootTable)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<LootTableTrigger.TriggerInstance> f_303158_ = RecordCodecBuilder.create(
            p_325232_ -> p_325232_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(LootTableTrigger.TriggerInstance::playerPredicate),
                        ResourceKey.codec(Registries.f_314309_).fieldOf("loot_table").forGetter(LootTableTrigger.TriggerInstance::lootTable)
                    )
                    .apply(p_325232_, LootTableTrigger.TriggerInstance::new)
        );

        public static Criterion<LootTableTrigger.TriggerInstance> lootTableUsed(ResourceKey<LootTable> p_330710_) {
            return CriteriaTriggers.GENERATE_LOOT.createCriterion(new LootTableTrigger.TriggerInstance(Optional.empty(), p_330710_));
        }

        public boolean matches(ResourceKey<LootTable> p_333185_) {
            return this.lootTable == p_333185_;
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302659_;
        }
    }
}