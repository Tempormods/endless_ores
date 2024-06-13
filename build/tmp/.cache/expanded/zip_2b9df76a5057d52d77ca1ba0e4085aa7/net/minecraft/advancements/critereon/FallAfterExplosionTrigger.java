package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class FallAfterExplosionTrigger extends SimpleCriterionTrigger<FallAfterExplosionTrigger.TriggerInstance> {
    @Override
    public Codec<FallAfterExplosionTrigger.TriggerInstance> createInstance() {
        return FallAfterExplosionTrigger.TriggerInstance.f_314732_;
    }

    public void m_320416_(ServerPlayer p_335354_, Vec3 p_333990_, @Nullable Entity p_335867_) {
        Vec3 vec3 = p_335354_.position();
        LootContext lootcontext = p_335867_ != null ? EntityPredicate.createContext(p_335354_, p_335867_) : null;
        this.trigger(p_335354_, p_328967_ -> p_328967_.m_323935_(p_335354_.serverLevel(), p_333990_, vec3, lootcontext));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_316027_,
        Optional<LocationPredicate> f_316796_,
        Optional<DistancePredicate> f_314749_,
        Optional<ContextAwarePredicate> f_317083_
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<FallAfterExplosionTrigger.TriggerInstance> f_314732_ = RecordCodecBuilder.create(
            p_334472_ -> p_334472_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(FallAfterExplosionTrigger.TriggerInstance::playerPredicate),
                        LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(FallAfterExplosionTrigger.TriggerInstance::f_316796_),
                        DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(FallAfterExplosionTrigger.TriggerInstance::f_314749_),
                        EntityPredicate.f_303210_.optionalFieldOf("cause").forGetter(FallAfterExplosionTrigger.TriggerInstance::f_317083_)
                    )
                    .apply(p_334472_, FallAfterExplosionTrigger.TriggerInstance::new)
        );

        public static Criterion<FallAfterExplosionTrigger.TriggerInstance> m_324889_(DistancePredicate p_331300_, EntityPredicate.Builder p_329821_) {
            return CriteriaTriggers.f_314306_
                .createCriterion(
                    new FallAfterExplosionTrigger.TriggerInstance(
                        Optional.empty(), Optional.empty(), Optional.of(p_331300_), Optional.of(EntityPredicate.wrap(p_329821_))
                    )
                );
        }

        @Override
        public void serializeToJson(CriterionValidator p_336137_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_336137_);
            p_336137_.m_307484_(this.f_317083_(), ".cause");
        }

        public boolean m_323935_(ServerLevel p_329103_, Vec3 p_328213_, Vec3 p_332081_, @Nullable LootContext p_327871_) {
            if (this.f_316796_.isPresent() && !this.f_316796_.get().matches(p_329103_, p_328213_.x, p_328213_.y, p_328213_.z)) {
                return false;
            } else {
                return this.f_314749_.isPresent()
                        && !this.f_314749_
                            .get()
                            .matches(p_328213_.x, p_328213_.y, p_328213_.z, p_332081_.x, p_332081_.y, p_332081_.z)
                    ? false
                    : !this.f_317083_.isPresent() || p_327871_ != null && this.f_317083_.get().matches(p_327871_);
            }
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_316027_;
        }
    }
}