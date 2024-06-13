package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<RecipeCraftedTrigger.TriggerInstance> {
    @Override
    public Codec<RecipeCraftedTrigger.TriggerInstance> createInstance() {
        return RecipeCraftedTrigger.TriggerInstance.f_302747_;
    }

    public void trigger(ServerPlayer pPlayer, ResourceLocation pRecipeId, List<ItemStack> pItems) {
        this.trigger(pPlayer, p_282798_ -> p_282798_.matches(pRecipeId, pItems));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303120_, ResourceLocation recipeId, List<ItemPredicate> f_302778_)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<RecipeCraftedTrigger.TriggerInstance> f_302747_ = RecordCodecBuilder.create(
            p_325243_ -> p_325243_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(RecipeCraftedTrigger.TriggerInstance::playerPredicate),
                        ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(RecipeCraftedTrigger.TriggerInstance::recipeId),
                        ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(RecipeCraftedTrigger.TriggerInstance::f_302778_)
                    )
                    .apply(p_325243_, RecipeCraftedTrigger.TriggerInstance::new)
        );

        public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation pRecipeId, List<ItemPredicate.Builder> pPredicates) {
            return CriteriaTriggers.RECIPE_CRAFTED
                .createCriterion(
                    new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), pRecipeId, pPredicates.stream().map(ItemPredicate.Builder::build).toList())
                );
        }

        public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation pRecipeId) {
            return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), pRecipeId, List.of()));
        }

        public static Criterion<RecipeCraftedTrigger.TriggerInstance> m_321438_(ResourceLocation p_329582_) {
            return CriteriaTriggers.f_315310_.createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), p_329582_, List.of()));
        }

        boolean matches(ResourceLocation pRecipeId, List<ItemStack> pItems) {
            if (!pRecipeId.equals(this.recipeId)) {
                return false;
            } else {
                List<ItemStack> list = new ArrayList<>(pItems);

                for (ItemPredicate itempredicate : this.f_302778_) {
                    boolean flag = false;
                    Iterator<ItemStack> iterator = list.iterator();

                    while (iterator.hasNext()) {
                        if (itempredicate.test(iterator.next())) {
                            iterator.remove();
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
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303120_;
        }
    }
}