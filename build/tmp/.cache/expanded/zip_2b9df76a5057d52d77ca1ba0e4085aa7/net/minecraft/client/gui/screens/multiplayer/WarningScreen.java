package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WarningScreen extends Screen {
    private static final int f_317157_ = 100;
    private final Component message;
    @Nullable
    private final Component check;
    private final Component narration;
    @Nullable
    protected Checkbox stopShowing;
    @Nullable
    private FocusableTextWidget f_316463_;
    private final FrameLayout f_316882_;

    protected WarningScreen(Component pTitle, Component pContent, Component pNarration) {
        this(pTitle, pContent, null, pNarration);
    }

    protected WarningScreen(Component pTitle, Component pContent, @Nullable Component pCheck, Component pNarration) {
        super(pTitle);
        this.message = pContent;
        this.check = pCheck;
        this.narration = pNarration;
        this.f_316882_ = new FrameLayout(0, 0, this.width, this.height);
    }

    protected abstract Layout initButtons();

    @Override
    protected void init() {
        LinearLayout linearlayout = this.f_316882_.addChild(LinearLayout.vertical().spacing(8));
        linearlayout.defaultCellSetting().alignHorizontallyCenter();
        linearlayout.addChild(new StringWidget(this.getTitle(), this.font));
        this.f_316463_ = linearlayout.addChild(
            new FocusableTextWidget(this.width - 100, this.message, this.font, 12), p_328910_ -> p_328910_.padding(12)
        );
        this.f_316463_.setCentered(false);
        LinearLayout linearlayout1 = linearlayout.addChild(LinearLayout.vertical().spacing(8));
        linearlayout1.defaultCellSetting().alignHorizontallyCenter();
        if (this.check != null) {
            this.stopShowing = linearlayout1.addChild(Checkbox.m_306644_(this.check, this.font).m_307240_());
        }

        linearlayout1.addChild(this.initButtons());
        this.f_316882_.visitWidgets(p_330212_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_330212_);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.f_316463_ != null) {
            this.f_316463_.setMaxWidth(this.width - 100);
        }

        this.f_316882_.arrangeElements();
        FrameLayout.centerInRectangle(this.f_316882_, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return this.narration;
    }
}