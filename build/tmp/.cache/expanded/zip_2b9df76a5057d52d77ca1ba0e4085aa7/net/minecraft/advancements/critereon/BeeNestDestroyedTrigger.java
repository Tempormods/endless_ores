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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger<BeeNestDestroyedTrigger.TriggerInstance> {
    @Override
    public Codec<BeeNestDestroyedTrigger.TriggerInstance> createInstance() {
        return BeeNestDestroyedTrigger.TriggerInstance.f_302285_;
    }

    public void trigger(ServerPlayer pPlayer, BlockState pState, ItemStack pStack, int pNumBees) {
        this.trigger(pPlayer, p_146660_ -> p_146660_.matches(pState, pStack, pNumBees));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_302528_, Optional<Holder<Block>> block, Optional<ItemPredicate> item, MinMaxBounds.Ints f_303141_
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BeeNestDestroyedTrigger.TriggerInstance> f_302285_ = RecordCodecBuilder.create(
            p_325190_ -> p_325190_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(BeeNestDestroyedTrigger.TriggerInstance::playerPredicate),
                        BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(BeeNestDestroyedTrigger.TriggerInstance::block),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(BeeNestDestroyedTrigger.TriggerInstance::item),
                        MinMaxBounds.Ints.CODEC
                            .optionalFieldOf("num_bees_inside", MinMaxBounds.Ints.ANY)
                            .forGetter(BeeNestDestroyedTrigger.TriggerInstance::f_303141_)
                    )
                    .apply(p_325190_, BeeNestDestroyedTrigger.TriggerInstance::new)
        );

        public static Criterion<BeeNestDestroyedTrigger.TriggerInstance> destroyedBeeNest(Block pBlock, ItemPredicate.Builder pItem, MinMaxBounds.Ints pNumBees) {
            return CriteriaTriggers.BEE_NEST_DESTROYED
                .createCriterion(
                    new BeeNestDestroyedTrigger.TriggerInstance(Optional.empty(), Optional.of(pBlock.builtInRegistryHolder()), Optional.of(pItem.build()), pNumBees)
                );
        }

        public boolean matches(BlockState pState, ItemStack pStack, int pNumBees) {
            if (this.block.isPresent() && !pState.is(this.block.get())) {
                return false;
            } else {
                return this.item.isPresent() && !this.item.get().test(pStack) ? false : this.f_303141_.matches(pNumBees);
            }
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302528_;
        }
    }
}