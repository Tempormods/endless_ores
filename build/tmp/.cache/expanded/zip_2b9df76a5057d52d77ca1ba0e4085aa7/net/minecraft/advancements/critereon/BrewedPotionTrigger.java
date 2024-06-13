package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<BrewedPotionTrigger.TriggerInstance> {
    @Override
    public Codec<BrewedPotionTrigger.TriggerInstance> createInstance() {
        return BrewedPotionTrigger.TriggerInstance.f_303483_;
    }

    public void trigger(ServerPlayer pPlayer, Holder<Potion> p_311358_) {
        this.trigger(pPlayer, p_308115_ -> p_308115_.matches(p_311358_));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303062_, Optional<Holder<Potion>> potion)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BrewedPotionTrigger.TriggerInstance> f_303483_ = RecordCodecBuilder.create(
            p_325193_ -> p_325193_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(BrewedPotionTrigger.TriggerInstance::playerPredicate),
                        BuiltInRegistries.POTION.holderByNameCodec().optionalFieldOf("potion").forGetter(BrewedPotionTrigger.TriggerInstance::potion)
                    )
                    .apply(p_325193_, BrewedPotionTrigger.TriggerInstance::new)
        );

        public static Criterion<BrewedPotionTrigger.TriggerInstance> brewedPotion() {
            return CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public boolean matches(Holder<Potion> p_311152_) {
            return !this.potion.isPresent() || this.potion.get().equals(p_311152_);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303062_;
        }
    }
}