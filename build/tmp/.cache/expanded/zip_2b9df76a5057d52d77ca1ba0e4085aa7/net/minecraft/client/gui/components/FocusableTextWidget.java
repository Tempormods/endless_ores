package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FocusableTextWidget extends MultiLineTextWidget {
    private static final int f_313934_ = 4;
    private final boolean alwaysShowBorder;
    private final int f_316338_;

    public FocusableTextWidget(int pMaxWidth, Component pMessage, Font pFont) {
        this(pMaxWidth, pMessage, pFont, 4);
    }

    public FocusableTextWidget(int p_335481_, Component p_335339_, Font p_328204_, int p_334529_) {
        this(p_335481_, p_335339_, p_328204_, true, p_334529_);
    }

    public FocusableTextWidget(int pMaxWidth, Component pMessage, Font pFont, boolean pAlwaysShowBorder, int p_335803_) {
        super(pMessage, pFont);
        this.setMaxWidth(pMaxWidth);
        this.setCentered(true);
        this.active = true;
        this.alwaysShowBorder = pAlwaysShowBorder;
        this.f_316338_ = p_335803_;
    }

    public void m_319387_(int p_328277_) {
        this.setMaxWidth(p_328277_ - this.f_316338_ * 4);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isFocused() || this.alwaysShowBorder) {
            int i = this.getX() - this.f_316338_;
            int j = this.getY() - this.f_316338_;
            int k = this.getWidth() + this.f_316338_ * 2;
            int l = this.getHeight() + this.f_316338_ * 2;
            int i1 = this.alwaysShowBorder ? (this.isFocused() ? -1 : -6250336) : -1;
            pGuiGraphics.fill(i + 1, j, i + k, j + l, -16777216);
            pGuiGraphics.renderOutline(i, j, k, l, i1);
        }

        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
    }
}