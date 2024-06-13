package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;

public class StartRidingTrigger extends SimpleCriterionTrigger<StartRidingTrigger.TriggerInstance> {
    @Override
    public Codec<StartRidingTrigger.TriggerInstance> createInstance() {
        return StartRidingTrigger.TriggerInstance.f_303076_;
    }

    public void trigger(ServerPlayer pPlayer) {
        this.trigger(pPlayer, p_160394_ -> true);
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303042_) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<StartRidingTrigger.TriggerInstance> f_303076_ = RecordCodecBuilder.create(
            p_325250_ -> p_325250_.group(EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(StartRidingTrigger.TriggerInstance::playerPredicate))
                    .apply(p_325250_, StartRidingTrigger.TriggerInstance::new)
        );

        public static Criterion<StartRidingTrigger.TriggerInstance> playerStartsRiding(EntityPredicate.Builder pPlayer) {
            return CriteriaTriggers.START_RIDING_TRIGGER.createCriterion(new StartRidingTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(pPlayer))));
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303042_;
        }
    }
}