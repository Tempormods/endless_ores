package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AnyBlockInteractionTrigger extends SimpleCriterionTrigger<AnyBlockInteractionTrigger.TriggerInstance> {
    @Override
    public Codec<AnyBlockInteractionTrigger.TriggerInstance> createInstance() {
        return AnyBlockInteractionTrigger.TriggerInstance.f_317132_;
    }

    public void m_324210_(ServerPlayer p_329152_, BlockPos p_334977_, ItemStack p_334131_) {
        ServerLevel serverlevel = p_329152_.serverLevel();
        BlockState blockstate = serverlevel.getBlockState(p_334977_);
        LootParams lootparams = new LootParams.Builder(serverlevel)
            .withParameter(LootContextParams.ORIGIN, p_334977_.getCenter())
            .withParameter(LootContextParams.THIS_ENTITY, p_329152_)
            .withParameter(LootContextParams.BLOCK_STATE, blockstate)
            .withParameter(LootContextParams.TOOL, p_334131_)
            .create(LootContextParamSets.ADVANCEMENT_LOCATION);
        LootContext lootcontext = new LootContext.Builder(lootparams).create(Optional.empty());
        this.trigger(p_329152_, p_330225_ -> p_330225_.m_323241_(lootcontext));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_314221_, Optional<ContextAwarePredicate> f_315989_)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<AnyBlockInteractionTrigger.TriggerInstance> f_317132_ = RecordCodecBuilder.create(
            p_335009_ -> p_335009_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(AnyBlockInteractionTrigger.TriggerInstance::playerPredicate),
                        ContextAwarePredicate.f_303282_.optionalFieldOf("location").forGetter(AnyBlockInteractionTrigger.TriggerInstance::f_315989_)
                    )
                    .apply(p_335009_, AnyBlockInteractionTrigger.TriggerInstance::new)
        );

        public boolean m_323241_(LootContext p_333498_) {
            return this.f_315989_.isEmpty() || this.f_315989_.get().matches(p_333498_);
        }

        @Override
        public void serializeToJson(CriterionValidator p_328875_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_328875_);
            this.f_315989_.ifPresent(p_336130_ -> p_328875_.m_306042_(p_336130_, LootContextParamSets.ADVANCEMENT_LOCATION, ".location"));
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_314221_;
        }
    }
}