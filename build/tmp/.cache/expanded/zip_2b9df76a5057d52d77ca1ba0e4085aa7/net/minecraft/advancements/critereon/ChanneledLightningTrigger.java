package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
    @Override
    public Codec<ChanneledLightningTrigger.TriggerInstance> createInstance() {
        return ChanneledLightningTrigger.TriggerInstance.f_303276_;
    }

    public void trigger(ServerPlayer pPlayer, Collection<? extends Entity> pEntityTriggered) {
        List<LootContext> list = pEntityTriggered.stream().map(p_21720_ -> EntityPredicate.createContext(pPlayer, p_21720_)).collect(Collectors.toList());
        this.trigger(pPlayer, p_21730_ -> p_21730_.matches(list));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303599_, List<ContextAwarePredicate> victims)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ChanneledLightningTrigger.TriggerInstance> f_303276_ = RecordCodecBuilder.create(
            p_325195_ -> p_325195_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(ChanneledLightningTrigger.TriggerInstance::playerPredicate),
                        EntityPredicate.f_303210_.listOf().optionalFieldOf("victims", List.of()).forGetter(ChanneledLightningTrigger.TriggerInstance::victims)
                    )
                    .apply(p_325195_, ChanneledLightningTrigger.TriggerInstance::new)
        );

        public static Criterion<ChanneledLightningTrigger.TriggerInstance> channeledLightning(EntityPredicate.Builder... pVictims) {
            return CriteriaTriggers.CHANNELED_LIGHTNING.createCriterion(new ChanneledLightningTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(pVictims)));
        }

        public boolean matches(Collection<? extends LootContext> pVictims) {
            for (ContextAwarePredicate contextawarepredicate : this.victims) {
                boolean flag = false;

                for (LootContext lootcontext : pVictims) {
                    if (contextawarepredicate.matches(lootcontext)) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void serializeToJson(CriterionValidator p_312774_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_312774_);
            p_312774_.m_307251_(this.victims, ".victims");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303599_;
        }
    }
}