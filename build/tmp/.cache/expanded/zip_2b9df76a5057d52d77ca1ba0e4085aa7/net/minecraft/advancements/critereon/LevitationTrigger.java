package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<LevitationTrigger.TriggerInstance> {
    @Override
    public Codec<LevitationTrigger.TriggerInstance> createInstance() {
        return LevitationTrigger.TriggerInstance.f_303228_;
    }

    public void trigger(ServerPlayer pPlayer, Vec3 pStartPos, int pDuration) {
        this.trigger(pPlayer, p_49124_ -> p_49124_.matches(pPlayer, pStartPos, pDuration));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_302967_, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<LevitationTrigger.TriggerInstance> f_303228_ = RecordCodecBuilder.create(
            p_325225_ -> p_325225_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(LevitationTrigger.TriggerInstance::playerPredicate),
                        DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(LevitationTrigger.TriggerInstance::distance),
                        MinMaxBounds.Ints.CODEC
                            .optionalFieldOf("duration", MinMaxBounds.Ints.ANY)
                            .forGetter(LevitationTrigger.TriggerInstance::duration)
                    )
                    .apply(p_325225_, LevitationTrigger.TriggerInstance::new)
        );

        public static Criterion<LevitationTrigger.TriggerInstance> levitated(DistancePredicate pDistance) {
            return CriteriaTriggers.LEVITATION
                .createCriterion(new LevitationTrigger.TriggerInstance(Optional.empty(), Optional.of(pDistance), MinMaxBounds.Ints.ANY));
        }

        public boolean matches(ServerPlayer pPlayer, Vec3 pStartPos, int pDuration) {
            return this.distance.isPresent()
                    && !this.distance
                        .get()
                        .matches(pStartPos.x, pStartPos.y, pStartPos.z, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ())
                ? false
                : this.duration.matches(pDuration);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302967_;
        }
    }
}