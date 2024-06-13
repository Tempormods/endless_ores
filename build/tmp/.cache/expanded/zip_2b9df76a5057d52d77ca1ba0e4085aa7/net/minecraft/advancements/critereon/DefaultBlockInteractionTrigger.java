package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class DefaultBlockInteractionTrigger extends SimpleCriterionTrigger<DefaultBlockInteractionTrigger.TriggerInstance> {
    @Override
    public Codec<DefaultBlockInteractionTrigger.TriggerInstance> createInstance() {
        return DefaultBlockInteractionTrigger.TriggerInstance.f_315938_;
    }

    public void m_320907_(ServerPlayer p_328009_, BlockPos p_332070_) {
        ServerLevel serverlevel = p_328009_.serverLevel();
        BlockState blockstate = serverlevel.getBlockState(p_332070_);
        LootParams lootparams = new LootParams.Builder(serverlevel)
            .withParameter(LootContextParams.ORIGIN, p_332070_.getCenter())
            .withParameter(LootContextParams.THIS_ENTITY, p_328009_)
            .withParameter(LootContextParams.BLOCK_STATE, blockstate)
            .create(LootContextParamSets.f_314281_);
        LootContext lootcontext = new LootContext.Builder(lootparams).create(Optional.empty());
        this.trigger(p_328009_, p_335442_ -> p_335442_.m_324233_(lootcontext));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_315553_, Optional<ContextAwarePredicate> f_314344_)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<DefaultBlockInteractionTrigger.TriggerInstance> f_315938_ = RecordCodecBuilder.create(
            p_330508_ -> p_330508_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(DefaultBlockInteractionTrigger.TriggerInstance::playerPredicate),
                        ContextAwarePredicate.f_303282_.optionalFieldOf("location").forGetter(DefaultBlockInteractionTrigger.TriggerInstance::f_314344_)
                    )
                    .apply(p_330508_, DefaultBlockInteractionTrigger.TriggerInstance::new)
        );

        public boolean m_324233_(LootContext p_336077_) {
            return this.f_314344_.isEmpty() || this.f_314344_.get().matches(p_336077_);
        }

        @Override
        public void serializeToJson(CriterionValidator p_336311_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_336311_);
            this.f_314344_.ifPresent(p_328396_ -> p_336311_.m_306042_(p_328396_, LootContextParamSets.f_314281_, ".location"));
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_315553_;
        }
    }
}