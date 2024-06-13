package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ShotCrossbowTrigger extends SimpleCriterionTrigger<ShotCrossbowTrigger.TriggerInstance> {
    @Override
    public Codec<ShotCrossbowTrigger.TriggerInstance> createInstance() {
        return ShotCrossbowTrigger.TriggerInstance.f_303851_;
    }

    public void trigger(ServerPlayer pShooter, ItemStack pStack) {
        this.trigger(pShooter, p_65467_ -> p_65467_.matches(pStack));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303408_, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ShotCrossbowTrigger.TriggerInstance> f_303851_ = RecordCodecBuilder.create(
            p_325245_ -> p_325245_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(ShotCrossbowTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ShotCrossbowTrigger.TriggerInstance::item)
                    )
                    .apply(p_325245_, ShotCrossbowTrigger.TriggerInstance::new)
        );

        public static Criterion<ShotCrossbowTrigger.TriggerInstance> shotCrossbow(Optional<ItemPredicate> pItem) {
            return CriteriaTriggers.SHOT_CROSSBOW.createCriterion(new ShotCrossbowTrigger.TriggerInstance(Optional.empty(), pItem));
        }

        public static Criterion<ShotCrossbowTrigger.TriggerInstance> shotCrossbow(ItemLike pItem) {
            return CriteriaTriggers.SHOT_CROSSBOW
                .createCriterion(
                    new ShotCrossbowTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(pItem).build()))
                );
        }

        public boolean matches(ItemStack pItem) {
            return this.item.isEmpty() || this.item.get().test(pItem);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303408_;
        }
    }
}