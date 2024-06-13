package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecipeCache {
    private final RecipeCache.Entry[] f_302788_;
    private WeakReference<RecipeManager> f_302546_ = new WeakReference<>(null);

    public RecipeCache(int p_309405_) {
        this.f_302788_ = new RecipeCache.Entry[p_309405_];
    }

    public Optional<RecipeHolder<CraftingRecipe>> m_304754_(Level p_311354_, CraftingContainer p_310846_) {
        if (p_310846_.isEmpty()) {
            return Optional.empty();
        } else {
            this.m_305555_(p_311354_);

            for (int i = 0; i < this.f_302788_.length; i++) {
                RecipeCache.Entry recipecache$entry = this.f_302788_[i];
                if (recipecache$entry != null && recipecache$entry.m_307382_(p_310846_.m_58617_())) {
                    this.m_306919_(i);
                    return Optional.ofNullable(recipecache$entry.f_303234_());
                }
            }

            return this.m_307507_(p_310846_, p_311354_);
        }
    }

    private void m_305555_(Level p_310788_) {
        RecipeManager recipemanager = p_310788_.getRecipeManager();
        if (recipemanager != this.f_302546_.get()) {
            this.f_302546_ = new WeakReference<>(recipemanager);
            Arrays.fill(this.f_302788_, null);
        }
    }

    private Optional<RecipeHolder<CraftingRecipe>> m_307507_(CraftingContainer p_309716_, Level p_309968_) {
        Optional<RecipeHolder<CraftingRecipe>> optional = p_309968_.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_309716_, p_309968_);
        this.m_307800_(p_309716_.m_58617_(), optional.orElse(null));
        return optional;
    }

    private void m_306919_(int p_309395_) {
        if (p_309395_ > 0) {
            RecipeCache.Entry recipecache$entry = this.f_302788_[p_309395_];
            System.arraycopy(this.f_302788_, 0, this.f_302788_, 1, p_309395_);
            this.f_302788_[0] = recipecache$entry;
        }
    }

    private void m_307800_(List<ItemStack> p_313121_, @Nullable RecipeHolder<CraftingRecipe> p_330177_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_313121_.size(), ItemStack.EMPTY);

        for (int i = 0; i < p_313121_.size(); i++) {
            nonnulllist.set(i, p_313121_.get(i).copyWithCount(1));
        }

        System.arraycopy(this.f_302788_, 0, this.f_302788_, 1, this.f_302788_.length - 1);
        this.f_302788_[0] = new RecipeCache.Entry(nonnulllist, p_330177_);
    }

    static record Entry(NonNullList<ItemStack> f_303324_, @Nullable RecipeHolder<CraftingRecipe> f_303234_) {
        public boolean m_307382_(List<ItemStack> p_311947_) {
            if (this.f_303324_.size() != p_311947_.size()) {
                return false;
            } else {
                for (int i = 0; i < this.f_303324_.size(); i++) {
                    if (!ItemStack.m_322370_(this.f_303324_.get(i), p_311947_.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}