package net.minecraft.world.item.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;

public class PotionBrewing {
    public static final int BREWING_TIME_SECONDS = 20;
    public static final PotionBrewing f_317012_ = new PotionBrewing(List.of(), List.of(), List.of());
    private final List<Ingredient> f_317135_;
    private final List<PotionBrewing.Mix<Potion>> f_316577_;
    private final List<PotionBrewing.Mix<Item>> f_314332_;
    private final List<net.minecraftforge.common.brewing.IBrewingRecipe> recipes;

    PotionBrewing(List<Ingredient> p_331253_, List<PotionBrewing.Mix<Potion>> p_333814_, List<PotionBrewing.Mix<Item>> p_332419_) {
        this(p_331253_, p_333814_, p_332419_, null);
    }

    PotionBrewing(List<Ingredient> p_331253_, List<PotionBrewing.Mix<Potion>> p_333814_, List<PotionBrewing.Mix<Item>> p_332419_, Builder builder) {
        this.f_317135_ = p_331253_;
        this.f_316577_ = p_333814_;
        this.f_314332_ = p_332419_;
        var tmp = new ArrayList<net.minecraftforge.common.brewing.IBrewingRecipe>();
        tmp.add(new net.minecraftforge.common.brewing.VanillaBrewingRecipe(this, this::mixVanilla));
        if (builder != null) {
            tmp.addAll(builder.recipes);
        }
        this.recipes = java.util.Collections.unmodifiableList(tmp);
    }

    public boolean isIngredient(ItemStack pInput) {
        if (pInput.isEmpty()) {
            return false;
        }

        for (var recipe : recipes) {
            if (recipe.isIngredient(pInput)) {
                return true;
            }
        }
        return false;
    }

    private boolean m_321499_(ItemStack p_328293_) {
        for (Ingredient ingredient : this.f_317135_) {
            if (ingredient.test(p_328293_)) {
                return true;
            }
        }

        return false;
    }

    public boolean isContainerIngredient(ItemStack pInput) {
        for (PotionBrewing.Mix<Item> mix : this.f_314332_) {
            if (mix.ingredient.test(pInput)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPotionIngredient(ItemStack pInput) {
        for (PotionBrewing.Mix<Potion> mix : this.f_316577_) {
            if (mix.ingredient.test(pInput)) {
                return true;
            }
        }

        return false;
    }

    public boolean isBrewablePotion(Holder<Potion> p_330984_) {
        for (PotionBrewing.Mix<Potion> mix : this.f_316577_) {
            if (mix.to.m_318604_(p_330984_)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMix(ItemStack pInput, ItemStack pReagent) {
        return !mix(pReagent, pInput).isEmpty();
    }

    /** @deprecated Forge: use hasMix(ItemStack, ItemStack)*/
    public boolean hasContainerMix(ItemStack pInput, ItemStack pReagent) {
        for (PotionBrewing.Mix<Item> mix : this.f_314332_) {
            if (pInput.is(mix.from) && mix.ingredient.test(pReagent)) {
                return true;
            }
        }

        return false;
    }

    /** @deprecated Forge: use hasMix(ItemStack, ItemStack)*/
    public boolean hasPotionMix(ItemStack pInput, ItemStack pReagent) {
        Optional<Holder<Potion>> optional = pInput.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_).f_317059_();
        if (optional.isEmpty()) {
            return false;
        } else {
            for (PotionBrewing.Mix<Potion> mix : this.f_316577_) {
                if (mix.from.m_318604_(optional.get()) && mix.ingredient.test(pReagent)) {
                    return true;
                }
            }

            return false;
        }
    }

    public ItemStack mix(ItemStack pReagent, ItemStack pPotion) {
        if (pPotion.isEmpty() || pPotion.getCount() != 1) return ItemStack.EMPTY;
        if (pReagent.isEmpty()) return ItemStack.EMPTY;

        for (var recipe : recipes) {
            ItemStack output = recipe.getOutput(pPotion, pReagent);
            if (!output.isEmpty()) {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack mixVanilla(ItemStack pReagent, ItemStack pPotion) {
        if (pPotion.isEmpty()) {
            return pPotion;
        } else {
            Optional<Holder<Potion>> optional = pPotion.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_).f_317059_();
            if (optional.isEmpty()) {
                return pPotion;
            } else {
                for (PotionBrewing.Mix<Item> mix : this.f_314332_) {
                    if (pPotion.is(mix.from) && mix.ingredient.test(pReagent)) {
                        return PotionContents.m_324840_(mix.to.value(), optional.get());
                    }
                }

                for (PotionBrewing.Mix<Potion> mix1 : this.f_316577_) {
                    if (mix1.from.m_318604_(optional.get()) && mix1.ingredient.test(pReagent)) {
                        return PotionContents.m_324840_(pPotion.getItem(), mix1.to);
                    }
                }

                return pPotion;
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid input for the start of a recipe
     */
    public boolean isValidInput(ItemStack stack) {
        for (var recipe : recipes) {
            if (recipe.isInput(stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable list containing all the recipes in the registry
     */
    public List<net.minecraftforge.common.brewing.IBrewingRecipe> getRecipes() {
        return this.recipes;
    }

    public static PotionBrewing m_320553_(FeatureFlagSet p_329176_) {
        PotionBrewing.Builder potionbrewing$builder = new PotionBrewing.Builder(p_329176_);
        addContainerRecipe(potionbrewing$builder);
        net.minecraftforge.event.ForgeEventFactory.onBrewingRecipeRegister(potionbrewing$builder, p_329176_);
        return potionbrewing$builder.m_324122_();
    }

    public static void addContainerRecipe(PotionBrewing.Builder p_332525_) {
        p_332525_.m_324493_(Items.POTION);
        p_332525_.m_324493_(Items.SPLASH_POTION);
        p_332525_.m_324493_(Items.LINGERING_POTION);
        p_332525_.m_320934_(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        p_332525_.m_320934_(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        p_332525_.m_321459_(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        p_332525_.m_321459_(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        p_332525_.m_321459_(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        p_332525_.m_322122_(Items.f_315544_, Potions.f_313979_);
        p_332525_.m_322122_(Items.SLIME_BLOCK, Potions.f_316406_);
        p_332525_.m_322122_(Items.STONE, Potions.f_315215_);
        p_332525_.m_322122_(Items.COBWEB, Potions.f_314032_);
        p_332525_.m_321459_(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        p_332525_.m_321459_(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        p_332525_.m_321459_(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        p_332525_.m_321459_(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        p_332525_.m_321459_(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        p_332525_.m_322122_(Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        p_332525_.m_321459_(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        p_332525_.m_322122_(Items.RABBIT_FOOT, Potions.LEAPING);
        p_332525_.m_321459_(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        p_332525_.m_321459_(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        p_332525_.m_321459_(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        p_332525_.m_321459_(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        p_332525_.m_321459_(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        p_332525_.m_321459_(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        p_332525_.m_321459_(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        p_332525_.m_321459_(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        p_332525_.m_321459_(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        p_332525_.m_321459_(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        p_332525_.m_321459_(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        p_332525_.m_322122_(Items.SUGAR, Potions.SWIFTNESS);
        p_332525_.m_321459_(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        p_332525_.m_321459_(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        p_332525_.m_321459_(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        p_332525_.m_321459_(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        p_332525_.m_322122_(Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        p_332525_.m_321459_(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        p_332525_.m_321459_(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        p_332525_.m_321459_(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        p_332525_.m_321459_(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        p_332525_.m_321459_(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        p_332525_.m_321459_(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        p_332525_.m_321459_(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        p_332525_.m_322122_(Items.SPIDER_EYE, Potions.POISON);
        p_332525_.m_321459_(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        p_332525_.m_321459_(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        p_332525_.m_322122_(Items.GHAST_TEAR, Potions.REGENERATION);
        p_332525_.m_321459_(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        p_332525_.m_321459_(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        p_332525_.m_322122_(Items.BLAZE_POWDER, Potions.STRENGTH);
        p_332525_.m_321459_(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        p_332525_.m_321459_(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        p_332525_.m_321459_(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        p_332525_.m_321459_(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        p_332525_.m_321459_(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        p_332525_.m_321459_(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    public static class Builder {
        private final List<Ingredient> f_314067_ = new ArrayList<>();
        private final List<PotionBrewing.Mix<Potion>> f_314622_ = new ArrayList<>();
        private final List<PotionBrewing.Mix<Item>> f_315307_ = new ArrayList<>();
        private final FeatureFlagSet f_315516_;
        private final List<net.minecraftforge.common.brewing.IBrewingRecipe> recipes = new ArrayList<>();

        public Builder(FeatureFlagSet p_332559_) {
            this.f_315516_ = p_332559_;
        }

        private static void m_321224_(Item p_335280_) {
            if (!(p_335280_ instanceof PotionItem)) {
                throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(p_335280_));
            }
        }

        public void m_320934_(Item p_332816_, Item p_336336_, Item p_330133_) {
            if (p_332816_.isEnabled(this.f_315516_) && p_336336_.isEnabled(this.f_315516_) && p_330133_.isEnabled(this.f_315516_)) {
                m_321224_(p_332816_);
                m_321224_(p_330133_);
                this.f_315307_.add(new PotionBrewing.Mix<>(p_332816_.builtInRegistryHolder(), Ingredient.of(p_336336_), p_330133_.builtInRegistryHolder()));
            }
        }

        public void m_324493_(Item p_329695_) {
            if (p_329695_.isEnabled(this.f_315516_)) {
                m_321224_(p_329695_);
                this.f_314067_.add(Ingredient.of(p_329695_));
            }
        }

        public void m_321459_(Holder<Potion> p_333042_, Item p_331299_, Holder<Potion> p_328607_) {
            if (p_333042_.value().isEnabled(this.f_315516_) && p_331299_.isEnabled(this.f_315516_) && p_328607_.value().isEnabled(this.f_315516_)) {
                this.f_314622_.add(new PotionBrewing.Mix<>(p_333042_, Ingredient.of(p_331299_), p_328607_));
            }
        }

        public void m_322122_(Item p_327705_, Holder<Potion> p_328478_) {
            if (p_328478_.value().isEnabled(this.f_315516_)) {
                this.m_321459_(Potions.WATER, p_327705_, Potions.MUNDANE);
                this.m_321459_(Potions.AWKWARD, p_327705_, p_328478_);
            }
        }

        public Builder add(net.minecraftforge.common.brewing.IBrewingRecipe recipe) {
            this.recipes.add(recipe);
            return this;
        }

        public PotionBrewing m_324122_() {
            return new PotionBrewing(List.copyOf(this.f_314067_), List.copyOf(this.f_314622_), List.copyOf(this.f_315307_), this);
        }
    }

    public static record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {
    }
}
