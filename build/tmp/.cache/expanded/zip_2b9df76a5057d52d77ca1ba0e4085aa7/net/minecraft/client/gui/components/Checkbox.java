package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Checkbox extends AbstractButton {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE = new ResourceLocation("widget/checkbox");
    private static final int TEXT_COLOR = 14737632;
    private static final int f_302543_ = 4;
    private static final int f_302538_ = 8;
    private boolean selected;
    private final Checkbox.OnValueChange f_302693_;

    Checkbox(int pX, int pY, Component pMessage, Font p_312622_, boolean pSelected, Checkbox.OnValueChange p_309427_) {
        super(pX, pY, m_305019_(p_312622_) + 4 + p_312622_.width(pMessage), m_305019_(p_312622_), pMessage);
        this.selected = pSelected;
        this.f_302693_ = p_309427_;
    }

    public static Checkbox.Builder m_306644_(Component p_309446_, Font p_309998_) {
        return new Checkbox.Builder(p_309446_, p_309998_);
    }

    public static int m_305019_(Font p_310239_) {
        return 9 + 8;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.f_302693_.m_305600_(this, this.selected);
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        ResourceLocation resourcelocation;
        if (this.selected) {
            resourcelocation = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        } else {
            resourcelocation = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
        }

        int i = m_305019_(font);
        int j = this.getX() + i + 4;
        int k = this.getY() + (this.height >> 1) - (9 >> 1);
        pGuiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), i, i);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.drawString(font, this.getMessage(), j, k, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Component f_302198_;
        private final Font f_303149_;
        private int f_302567_ = 0;
        private int f_303461_ = 0;
        private Checkbox.OnValueChange f_302322_ = Checkbox.OnValueChange.f_302737_;
        private boolean f_302876_ = false;
        @Nullable
        private OptionInstance<Boolean> f_303710_ = null;
        @Nullable
        private Tooltip f_302406_ = null;

        Builder(Component p_312515_, Font p_311430_) {
            this.f_302198_ = p_312515_;
            this.f_303149_ = p_311430_;
        }

        public Checkbox.Builder m_307310_(int p_313014_, int p_311548_) {
            this.f_302567_ = p_313014_;
            this.f_303461_ = p_311548_;
            return this;
        }

        public Checkbox.Builder m_306786_(Checkbox.OnValueChange p_312502_) {
            this.f_302322_ = p_312502_;
            return this;
        }

        public Checkbox.Builder m_307950_(boolean p_310957_) {
            this.f_302876_ = p_310957_;
            this.f_303710_ = null;
            return this;
        }

        public Checkbox.Builder m_305580_(OptionInstance<Boolean> p_310610_) {
            this.f_303710_ = p_310610_;
            this.f_302876_ = p_310610_.get();
            return this;
        }

        public Checkbox.Builder m_305879_(Tooltip p_309712_) {
            this.f_302406_ = p_309712_;
            return this;
        }

        public Checkbox m_307240_() {
            Checkbox.OnValueChange checkbox$onvaluechange = this.f_303710_ == null ? this.f_302322_ : (p_311135_, p_313032_) -> {
                this.f_303710_.set(p_313032_);
                this.f_302322_.m_305600_(p_311135_, p_313032_);
            };
            Checkbox checkbox = new Checkbox(this.f_302567_, this.f_303461_, this.f_302198_, this.f_303149_, this.f_302876_, checkbox$onvaluechange);
            checkbox.setTooltip(this.f_302406_);
            return checkbox;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnValueChange {
        Checkbox.OnValueChange f_302737_ = (p_310417_, p_311975_) -> {
        };

        void m_305600_(Checkbox p_309925_, boolean p_310656_);
    }
}