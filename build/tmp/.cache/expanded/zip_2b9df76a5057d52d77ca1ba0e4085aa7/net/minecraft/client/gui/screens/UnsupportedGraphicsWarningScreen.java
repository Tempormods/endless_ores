package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnsupportedGraphicsWarningScreen extends Screen {
    private static final int f_303854_ = 20;
    private static final int f_302846_ = 5;
    private static final int f_303691_ = 20;
    private final Component f_302861_;
    private final FormattedText f_302315_;
    private final ImmutableList<UnsupportedGraphicsWarningScreen.ButtonOption> f_303024_;
    private MultiLineLabel f_302953_ = MultiLineLabel.EMPTY;
    private int f_302511_;
    private int f_303397_;

    protected UnsupportedGraphicsWarningScreen(
        Component p_310653_, List<Component> p_312556_, ImmutableList<UnsupportedGraphicsWarningScreen.ButtonOption> p_312852_
    ) {
        super(p_310653_);
        this.f_302315_ = FormattedText.composite(p_312556_);
        this.f_302861_ = CommonComponents.joinForNarration(p_310653_, ComponentUtils.formatList(p_312556_, CommonComponents.EMPTY));
        this.f_303024_ = p_312852_;
    }

    @Override
    public Component getNarrationMessage() {
        return this.f_302861_;
    }

    @Override
    public void init() {
        for (UnsupportedGraphicsWarningScreen.ButtonOption unsupportedgraphicswarningscreen$buttonoption : this.f_303024_) {
            this.f_303397_ = Math.max(this.f_303397_, 20 + this.font.width(unsupportedgraphicswarningscreen$buttonoption.f_302580_) + 20);
        }

        int l = 5 + this.f_303397_ + 5;
        int i1 = l * this.f_303024_.size();
        this.f_302953_ = MultiLineLabel.create(this.font, this.f_302315_, i1);
        int i = this.f_302953_.getLineCount() * 9;
        this.f_302511_ = (int)((double)this.height / 2.0 - (double)i / 2.0);
        int j = this.f_302511_ + i + 9 * 2;
        int k = (int)((double)this.width / 2.0 - (double)i1 / 2.0);

        for (UnsupportedGraphicsWarningScreen.ButtonOption unsupportedgraphicswarningscreen$buttonoption1 : this.f_303024_) {
            this.addRenderableWidget(
                Button.builder(unsupportedgraphicswarningscreen$buttonoption1.f_302580_, unsupportedgraphicswarningscreen$buttonoption1.f_303659_)
                    .bounds(k, j, this.f_303397_, 20)
                    .build()
            );
            k += l;
        }
    }

    @Override
    public void render(GuiGraphics p_310210_, int p_309572_, int p_312206_, float p_311484_) {
        super.render(p_310210_, p_309572_, p_312206_, p_311484_);
        p_310210_.drawCenteredString(this.font, this.title, this.width / 2, this.f_302511_ - 9 * 2, -1);
        this.f_302953_.renderCentered(p_310210_, this.width / 2, this.f_302511_);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static final class ButtonOption {
        final Component f_302580_;
        final Button.OnPress f_303659_;

        public ButtonOption(Component p_311722_, Button.OnPress p_312192_) {
            this.f_302580_ = p_311722_;
            this.f_303659_ = p_312192_;
        }
    }
}