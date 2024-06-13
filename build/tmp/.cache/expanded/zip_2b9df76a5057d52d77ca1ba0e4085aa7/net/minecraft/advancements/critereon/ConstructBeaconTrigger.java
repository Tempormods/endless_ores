package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
    @Override
    public Codec<ConstructBeaconTrigger.TriggerInstance> createInstance() {
        return ConstructBeaconTrigger.TriggerInstance.f_302676_;
    }

    public void trigger(ServerPlayer pPlayer, int pLevel) {
        this.trigger(pPlayer, p_148028_ -> p_148028_.matches(pLevel));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303453_, MinMaxBounds.Ints level)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ConstructBeaconTrigger.TriggerInstance> f_302676_ = RecordCodecBuilder.create(
            p_325196_ -> p_325196_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(ConstructBeaconTrigger.TriggerInstance::playerPredicate),
                        MinMaxBounds.Ints.CODEC
                            .optionalFieldOf("level", MinMaxBounds.Ints.ANY)
                            .forGetter(ConstructBeaconTrigger.TriggerInstance::level)
                    )
                    .apply(p_325196_, ConstructBeaconTrigger.TriggerInstance::new)
        );

        public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedBeacon() {
            return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedBeacon(MinMaxBounds.Ints pLevel) {
            return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), pLevel));
        }

        public boolean matches(int pLevel) {
            return this.level.matches(pLevel);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303453_;
        }
    }
}