package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
    private static final Component MESSAGE = Component.translatable("mco.account.privacy.information");
    private static final int f_303146_ = 15;
    private final LinearLayout f_303048_ = LinearLayout.vertical();
    private final Screen f_303656_;
    @Nullable
    private MultiLineTextWidget f_302587_;

    public RealmsParentalConsentScreen(Screen pNextScreen) {
        super(GameNarrator.NO_TITLE);
        this.f_303656_ = pNextScreen;
    }

    @Override
    public void init() {
        this.f_303048_.spacing(15).defaultCellSetting().alignHorizontallyCenter();
        this.f_302587_ = new MultiLineTextWidget(MESSAGE, this.font).setCentered(true);
        this.f_303048_.addChild(this.f_302587_);
        LinearLayout linearlayout = this.f_303048_.addChild(LinearLayout.horizontal().spacing(8));
        Component component = Component.translatable("mco.account.privacy.info.button");
        linearlayout.addChild(Button.builder(component, ConfirmLinkScreen.confirmLink(this, "https://aka.ms/MinecraftGDPR")).build());
        linearlayout.addChild(Button.builder(CommonComponents.GUI_BACK, p_308061_ -> this.onClose()).build());
        this.f_303048_.visitWidgets(p_325134_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325134_);
        });
        this.repositionElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.f_303656_);
    }

    @Override
    protected void repositionElements() {
        if (this.f_302587_ != null) {
            this.f_302587_.setMaxWidth(this.width - 15);
        }

        this.f_303048_.arrangeElements();
        FrameLayout.centerInRectangle(this.f_303048_, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return MESSAGE;
    }
}