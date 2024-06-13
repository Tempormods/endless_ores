package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericMessageScreen extends Screen {
    @Nullable
    private FocusableTextWidget f_315923_;

    public GenericMessageScreen(Component p_334099_) {
        super(p_334099_);
    }

    @Override
    protected void init() {
        this.f_315923_ = this.addRenderableWidget(new FocusableTextWidget(this.width, this.title, this.font, 12));
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.f_315923_ != null) {
            this.f_315923_.m_319387_(this.width);
            this.f_315923_.setPosition(this.width / 2 - this.f_315923_.getWidth() / 2, this.height / 2 - 9 / 2);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics p_328774_, int p_328895_, int p_327693_, float p_328562_) {
        this.m_318720_(p_328774_, p_328562_);
        this.m_324436_(p_328562_);
        this.m_323963_(p_328774_);
    }
}