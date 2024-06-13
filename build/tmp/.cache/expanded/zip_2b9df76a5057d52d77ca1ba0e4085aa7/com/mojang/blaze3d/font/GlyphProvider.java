package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.FontOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GlyphProvider extends AutoCloseable {
    float f_315786_ = 7.0F;

    @Override
    default void close() {
    }

    @Nullable
    default GlyphInfo getGlyph(int pCharacter) {
        return null;
    }

    IntSet getSupportedGlyphs();

    @OnlyIn(Dist.CLIENT)
    public static record Conditional(GlyphProvider f_316017_, FontOption.Filter f_316533_) implements AutoCloseable {
        @Override
        public void close() {
            this.f_316017_.close();
        }
    }
}