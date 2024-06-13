package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;

public record ShapedRecipePattern(int f_303446_, int f_302375_, NonNullList<Ingredient> f_303265_, Optional<ShapedRecipePattern.Data> f_302791_) {
    private static final int f_302599_ = 3;
    public static final MapCodec<ShapedRecipePattern> f_302908_ = ShapedRecipePattern.Data.f_303136_
        .flatXmap(
            ShapedRecipePattern::m_305688_,
            p_310854_ -> p_310854_.f_302791_().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipePattern> f_315058_ = StreamCodec.m_324771_(
        ShapedRecipePattern::m_307574_, ShapedRecipePattern::m_306640_
    );

    public static ShapedRecipePattern m_304825_(Map<Character, Ingredient> p_310983_, String... p_310430_) {
        return m_306906_(p_310983_, List.of(p_310430_));
    }

    public static ShapedRecipePattern m_306906_(Map<Character, Ingredient> p_313226_, List<String> p_310089_) {
        ShapedRecipePattern.Data shapedrecipepattern$data = new ShapedRecipePattern.Data(p_313226_, p_310089_);
        return m_305688_(shapedrecipepattern$data).getOrThrow();
    }

    private static DataResult<ShapedRecipePattern> m_305688_(ShapedRecipePattern.Data p_312333_) {
        String[] astring = m_306947_(p_312333_.f_302897_);
        int i = astring[0].length();
        int j = astring.length;
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
        CharSet charset = new CharArraySet(p_312333_.f_302495_.keySet());

        for (int k = 0; k < astring.length; k++) {
            String s = astring[k];

            for (int l = 0; l < s.length(); l++) {
                char c0 = s.charAt(l);
                Ingredient ingredient = c0 == ' ' ? Ingredient.EMPTY : p_312333_.f_302495_.get(c0);
                if (ingredient == null) {
                    return DataResult.error(() -> "Pattern references symbol '" + c0 + "' but it's not defined in the key");
                }

                charset.remove(c0);
                nonnulllist.set(l + i * k, ingredient);
            }
        }

        return !charset.isEmpty()
            ? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charset)
            : DataResult.success(new ShapedRecipePattern(i, j, nonnulllist, Optional.of(p_312333_)));
    }

    @VisibleForTesting
    static String[] m_306947_(List<String> p_311492_) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < p_311492_.size(); i1++) {
            String s = p_311492_.get(i1);
            i = Math.min(i, m_304979_(s));
            int j1 = m_306007_(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    k++;
                }

                l++;
            } else {
                l = 0;
            }
        }

        if (p_311492_.size() == l) {
            return new String[0];
        } else {
            String[] astring = new String[p_311492_.size() - l - k];

            for (int k1 = 0; k1 < astring.length; k1++) {
                astring[k1] = p_311492_.get(k1 + k).substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int m_304979_(String p_309836_) {
        int i = 0;

        while (i < p_309836_.length() && p_309836_.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    private static int m_306007_(String p_312853_) {
        int i = p_312853_.length() - 1;

        while (i >= 0 && p_312853_.charAt(i) == ' ') {
            i--;
        }

        return i;
    }

    public boolean m_304908_(CraftingContainer p_310690_) {
        for (int i = 0; i <= p_310690_.getWidth() - this.f_303446_; i++) {
            for (int j = 0; j <= p_310690_.getHeight() - this.f_302375_; j++) {
                if (this.m_306368_(p_310690_, i, j, true)) {
                    return true;
                }

                if (this.m_306368_(p_310690_, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean m_306368_(CraftingContainer p_313091_, int p_311269_, int p_310676_, boolean p_313153_) {
        for (int i = 0; i < p_313091_.getWidth(); i++) {
            for (int j = 0; j < p_313091_.getHeight(); j++) {
                int k = i - p_311269_;
                int l = j - p_310676_;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.f_303446_ && l < this.f_302375_) {
                    if (p_313153_) {
                        ingredient = this.f_303265_.get(this.f_303446_ - k - 1 + l * this.f_303446_);
                    } else {
                        ingredient = this.f_303265_.get(k + l * this.f_303446_);
                    }
                }

                if (!ingredient.test(p_313091_.getItem(i + j * p_313091_.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    private void m_307574_(RegistryFriendlyByteBuf p_335258_) {
        p_335258_.writeVarInt(this.f_303446_);
        p_335258_.writeVarInt(this.f_302375_);

        for (Ingredient ingredient : this.f_303265_) {
            Ingredient.f_317040_.m_318638_(p_335258_, ingredient);
        }
    }

    private static ShapedRecipePattern m_306640_(RegistryFriendlyByteBuf p_332293_) {
        int i = p_332293_.readVarInt();
        int j = p_332293_.readVarInt();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
        nonnulllist.replaceAll(p_327210_ -> Ingredient.f_317040_.m_318688_(p_332293_));
        return new ShapedRecipePattern(i, j, nonnulllist, Optional.empty());
    }

    public static record Data(Map<Character, Ingredient> f_302495_, List<String> f_302897_) {
        private static final Codec<List<String>> f_302331_ = Codec.STRING.listOf().comapFlatMap(p_311191_ -> {
            if (p_311191_.size() > ShapedRecipe.MAX_HEIGHT) {
                return DataResult.error(() -> "Invalid pattern: too many rows, " + ShapedRecipe.MAX_HEIGHT + " is maximum");
            } else if (p_311191_.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            } else {
                int i = p_311191_.get(0).length();

                for (String s : p_311191_) {
                    if (s.length() > ShapedRecipe.MAX_HEIGHT) {
                        return DataResult.error(() -> "Invalid pattern: too many columns, " + ShapedRecipe.MAX_HEIGHT + " is maximum");
                    }

                    if (i != s.length()) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }

                return DataResult.success(p_311191_);
            }
        }, Function.identity());
        private static final Codec<Character> f_302820_ = Codec.STRING.comapFlatMap(p_313217_ -> {
            if (p_313217_.length() != 1) {
                return DataResult.error(() -> "Invalid key entry: '" + p_313217_ + "' is an invalid symbol (must be 1 character only).");
            } else {
                return " ".equals(p_313217_) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(p_313217_.charAt(0));
            }
        }, String::valueOf);
        public static final MapCodec<ShapedRecipePattern.Data> f_303136_ = RecordCodecBuilder.mapCodec(
            p_310577_ -> p_310577_.group(
                        ExtraCodecs.strictUnboundedMap(f_302820_, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(p_311797_ -> p_311797_.f_302495_),
                        f_302331_.fieldOf("pattern").forGetter(p_309770_ -> p_309770_.f_302897_)
                    )
                    .apply(p_310577_, ShapedRecipePattern.Data::new)
        );
    }
}
