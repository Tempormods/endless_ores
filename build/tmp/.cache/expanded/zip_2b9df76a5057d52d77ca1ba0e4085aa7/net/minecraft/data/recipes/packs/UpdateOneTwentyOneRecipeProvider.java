package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyOneRecipeProvider extends RecipeProvider {
    public UpdateOneTwentyOneRecipeProvider(PackOutput p_312536_, CompletableFuture<HolderLookup.Provider> p_333490_) {
        super(p_312536_, p_333490_);
    }

    @Override
    protected void buildRecipes(RecipeOutput p_310714_) {
        generateForEnabledBlockFamilies(p_310714_, FeatureFlagSet.of(FeatureFlags.f_302467_));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.f_303044_)
            .define('#', Items.IRON_INGOT)
            .define('C', Items.CRAFTING_TABLE)
            .define('R', Items.REDSTONE)
            .define('D', Items.DROPPER)
            .pattern("###")
            .pattern("#C#")
            .pattern("RDR")
            .unlockedBy("has_dropper", has(Items.DROPPER))
            .save(p_310714_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303571_, Blocks.TUFF, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303426_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_302213_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302743_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303652_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302914_, Blocks.TUFF, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302449_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_302818_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303547_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303545_, Blocks.TUFF, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303371_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_303237_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302382_, Blocks.TUFF);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302914_, Blocks.f_303652_, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302449_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_302818_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303547_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303545_, Blocks.f_303652_, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303371_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_303237_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302382_, Blocks.f_303652_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303545_, Blocks.f_303547_, 2);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303371_, Blocks.f_303547_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.DECORATIONS, Blocks.f_303237_, Blocks.f_303547_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302382_, Blocks.f_303547_);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302689_, Blocks.COPPER_BLOCK, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303448_, Blocks.EXPOSED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302507_, Blocks.WEATHERED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303811_, Blocks.OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303363_, Blocks.WAXED_COPPER_BLOCK, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302554_, Blocks.WAXED_EXPOSED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302612_, Blocks.WAXED_WEATHERED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303118_, Blocks.WAXED_OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302689_, Blocks.CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303448_, Blocks.EXPOSED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302507_, Blocks.WEATHERED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303811_, Blocks.OXIDIZED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303363_, Blocks.WAXED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302554_, Blocks.WAXED_EXPOSED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302612_, Blocks.WAXED_WEATHERED_CUT_COPPER, 1);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303118_, Blocks.WAXED_OXIDIZED_CUT_COPPER, 1);
        m_306715_(p_310714_, Blocks.f_303215_, Blocks.COPPER_BLOCK);
        m_306715_(p_310714_, Blocks.f_302995_, Blocks.EXPOSED_COPPER);
        m_306715_(p_310714_, Blocks.f_303236_, Blocks.WEATHERED_COPPER);
        m_306715_(p_310714_, Blocks.f_303549_, Blocks.OXIDIZED_COPPER);
        m_306715_(p_310714_, Blocks.f_303013_, Blocks.WAXED_COPPER_BLOCK);
        m_306715_(p_310714_, Blocks.f_303410_, Blocks.WAXED_EXPOSED_COPPER);
        m_306715_(p_310714_, Blocks.f_302872_, Blocks.WAXED_WEATHERED_COPPER);
        m_306715_(p_310714_, Blocks.f_303373_, Blocks.WAXED_OXIDIZED_COPPER);
        m_306759_(p_310714_, Blocks.f_302358_, Blocks.COPPER_BLOCK);
        m_306759_(p_310714_, Blocks.f_303271_, Blocks.EXPOSED_COPPER);
        m_306759_(p_310714_, Blocks.f_303674_, Blocks.WEATHERED_COPPER);
        m_306759_(p_310714_, Blocks.f_302668_, Blocks.OXIDIZED_COPPER);
        m_306759_(p_310714_, Blocks.f_302439_, Blocks.WAXED_COPPER_BLOCK);
        m_306759_(p_310714_, Blocks.f_303797_, Blocks.WAXED_EXPOSED_COPPER);
        m_306759_(p_310714_, Blocks.f_302556_, Blocks.WAXED_WEATHERED_COPPER);
        m_306759_(p_310714_, Blocks.f_302347_, Blocks.WAXED_OXIDIZED_COPPER);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303215_, Blocks.COPPER_BLOCK, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302995_, Blocks.EXPOSED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303236_, Blocks.WEATHERED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303549_, Blocks.OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303013_, Blocks.WAXED_COPPER_BLOCK, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303410_, Blocks.WAXED_EXPOSED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_302872_, Blocks.WAXED_WEATHERED_COPPER, 4);
        stonecutterResultFromBase(p_310714_, RecipeCategory.BUILDING_BLOCKS, Blocks.f_303373_, Blocks.WAXED_OXIDIZED_COPPER, 4);
        m_320343_().forEach(p_334633_ -> trimSmithing(p_310714_, p_334633_.f_303140_(), p_334633_.f_302799_()));
        copySmithingTemplate(p_310714_, Items.f_316167_, Items.f_315544_);
        copySmithingTemplate(p_310714_, Items.f_314806_, Items.COPPER_BLOCK);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.f_315945_, 4)
            .requires(Items.f_315544_)
            .unlockedBy("has_breeze_rod", has(Items.f_315544_))
            .save(p_310714_);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.f_314862_, 1)
            .define('I', Items.f_315544_)
            .define('#', Blocks.f_314894_)
            .pattern(" # ")
            .pattern(" I ")
            .unlockedBy("has_breeze_rod", has(Items.f_315544_))
            .unlockedBy("has_heavy_core", has(Blocks.f_314894_))
            .save(p_310714_);
        waxRecipes(p_310714_, FeatureFlagSet.of(FeatureFlags.f_302467_));
    }

    public static Stream<VanillaRecipeProvider.TrimTemplate> m_320343_() {
        return Stream.of(Items.f_314806_, Items.f_316167_)
            .map(p_335668_ -> new VanillaRecipeProvider.TrimTemplate(p_335668_, new ResourceLocation(getItemName(p_335668_) + "_smithing_trim")));
    }
}