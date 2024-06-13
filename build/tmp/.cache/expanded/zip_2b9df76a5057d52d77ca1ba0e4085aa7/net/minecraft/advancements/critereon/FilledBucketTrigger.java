package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
    @Override
    public Codec<FilledBucketTrigger.TriggerInstance> createInstance() {
        return FilledBucketTrigger.TriggerInstance.f_302759_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pStack) {
        this.trigger(pPlayer, p_38777_ -> p_38777_.matches(pStack));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_302253_, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<FilledBucketTrigger.TriggerInstance> f_302759_ = RecordCodecBuilder.create(
            p_325212_ -> p_325212_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(FilledBucketTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FilledBucketTrigger.TriggerInstance::item)
                    )
                    .apply(p_325212_, FilledBucketTrigger.TriggerInstance::new)
        );

        public static Criterion<FilledBucketTrigger.TriggerInstance> filledBucket(ItemPredicate.Builder pItem) {
            return CriteriaTriggers.FILLED_BUCKET.createCriterion(new FilledBucketTrigger.TriggerInstance(Optional.empty(), Optional.of(pItem.build())));
        }

        public boolean matches(ItemStack pStack) {
            return !this.item.isPresent() || this.item.get().test(pStack);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302253_;
        }
    }
}