package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
    @Override
    public Codec<RecipeUnlockedTrigger.TriggerInstance> createInstance() {
        return RecipeUnlockedTrigger.TriggerInstance.f_303480_;
    }

    public void trigger(ServerPlayer pPlayer, RecipeHolder<?> pRecipe) {
        this.trigger(pPlayer, p_296143_ -> p_296143_.matches(pRecipe));
    }

    public static Criterion<RecipeUnlockedTrigger.TriggerInstance> unlocked(ResourceLocation pRecipeId) {
        return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new RecipeUnlockedTrigger.TriggerInstance(Optional.empty(), pRecipeId));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303217_, ResourceLocation recipe) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<RecipeUnlockedTrigger.TriggerInstance> f_303480_ = RecordCodecBuilder.create(
            p_325244_ -> p_325244_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(RecipeUnlockedTrigger.TriggerInstance::playerPredicate),
                        ResourceLocation.CODEC.fieldOf("recipe").forGetter(RecipeUnlockedTrigger.TriggerInstance::recipe)
                    )
                    .apply(p_325244_, RecipeUnlockedTrigger.TriggerInstance::new)
        );

        public boolean matches(RecipeHolder<?> pRecipe) {
            return this.recipe.equals(pRecipe.id());
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303217_;
        }
    }
}