package com.mojang.blaze3d.font;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SheetGlyphInfo {
    int getPixelWidth();

    int getPixelHeight();

    void upload(int pXOffset, int pYOffset);

    boolean isColored();

    float getOversample();

    default float getLeft() {
        return this.getBearingX();
    }

    default float getRight() {
        return this.getLeft() + (float)this.getPixelWidth() / this.getOversample();
    }

    default float m_319386_() {
        return 7.0F - this.getBearingY();
    }

    default float m_323712_() {
        return this.m_319386_() + (float)this.getPixelHeight() / this.getOversample();
    }

    default float getBearingX() {
        return 0.0F;
    }

    default float getBearingY() {
        return 7.0F;
    }
}