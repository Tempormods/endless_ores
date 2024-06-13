package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<UsedEnderEyeTrigger.TriggerInstance> {
    @Override
    public Codec<UsedEnderEyeTrigger.TriggerInstance> createInstance() {
        return UsedEnderEyeTrigger.TriggerInstance.f_303714_;
    }

    public void trigger(ServerPlayer pPlayer, BlockPos pPos) {
        double d0 = pPlayer.getX() - (double)pPos.getX();
        double d1 = pPlayer.getZ() - (double)pPos.getZ();
        double d2 = d0 * d0 + d1 * d1;
        this.trigger(pPlayer, p_73934_ -> p_73934_.matches(d2));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_302723_, MinMaxBounds.Doubles f_303708_)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UsedEnderEyeTrigger.TriggerInstance> f_303714_ = RecordCodecBuilder.create(
            p_325257_ -> p_325257_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(UsedEnderEyeTrigger.TriggerInstance::playerPredicate),
                        MinMaxBounds.Doubles.CODEC
                            .optionalFieldOf("distance", MinMaxBounds.Doubles.ANY)
                            .forGetter(UsedEnderEyeTrigger.TriggerInstance::f_303708_)
                    )
                    .apply(p_325257_, UsedEnderEyeTrigger.TriggerInstance::new)
        );

        public boolean matches(double pDistanceSq) {
            return this.f_303708_.matchesSqr(pDistanceSq);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302723_;
        }
    }
}